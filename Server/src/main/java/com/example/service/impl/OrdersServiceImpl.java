package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.DTO.*;
import com.example.VO.OrderPaymentVO;
import com.example.VO.OrderStatisticsVO;
import com.example.VO.OrderVO;
import com.example.VO.OrdersSubmitVO;
import com.example.constant.MessageConstant;
import com.example.constant.ReminderConstant;
import com.example.context.BaseContext;
import com.example.entity.AddressBook;
import com.example.entity.OrderDetail;
import com.example.entity.Orders;
import com.example.entity.ShoppingCart;
import com.example.exception.BaseException;
import com.example.mapper.OrderDetailMapper;
import com.example.mapper.OrdersMapper;
import com.example.mapper.ShoppingCartMapper;
import com.example.mq.message.OrderCreateMessage;
import com.example.mq.producer.OrderCreateProducer;
import com.example.result.PageResult;
import com.example.service.AddressBookService;
import com.example.service.OrdersService;
import com.example.websocket.WebSocketServer;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderCreateProducer orderCreateProducer;
    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public OrderVO getById(Long id) {
        Orders orders = ordersMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    public PageResult page(OrderPageQueryDTO orderPageQueryDTO) {
        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());
        orderPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<Orders> page = ordersMapper.pageQuery(orderPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();
        if (page != null && !page.isEmpty()) {
            for (Orders orders : page) {
                Long orderId = orders.getId();

                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), orderVOList);
    }


    @Override
    public OrdersSubmitVO submitOrderAsync(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = validateAndGetShoppingCart(userId, ordersSubmitDTO.getAddressBookId());

        String orderNumber = generateOrderNumber();
        OrderCreateMessage message = OrderCreateMessage.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .ordersSubmitDTO(ordersSubmitDTO)
                .shoppingCarts(shoppingCartList)
                .build();

        orderCreateProducer.sendOrderCreateMessage(message);

        return OrdersSubmitVO.builder()
                .orderNumber(orderNumber)
                .orderAmount(ordersSubmitDTO.getAmount())
                .orderTime(LocalDateTime.now())
                .accepted(Boolean.TRUE)
                .submitStatus("QUEUED")
                .message("下单请求已受理，正在排队处理")
                .build();
    }

    @Transactional
    @Override
    public OrdersSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = validateAndGetShoppingCart(userId, ordersSubmitDTO.getAddressBookId());
        String orderNumber = generateOrderNumber();

        createOrderFromMessage(ordersSubmitDTO, userId, orderNumber, shoppingCartList);
        Orders orders = ordersMapper.getByNumber(orderNumber);

        return OrdersSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .accepted(Boolean.TRUE)
                .submitStatus("SUCCESS")
                .message("下单成功")
                .build();
    }

    @Override
    public OrdersSubmitVO querySubmitResult(String orderNumber) {
        Orders orders = ordersMapper.getByNumber(orderNumber);
        if (orders == null) {
            return OrdersSubmitVO.builder()
                    .orderNumber(orderNumber)
                    .accepted(Boolean.TRUE)
                    .submitStatus("QUEUED")
                    .message("订单仍在处理中")
                    .build();
        }

        return OrdersSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .accepted(Boolean.TRUE)
                .submitStatus("SUCCESS")
                .message("订单处理完成")
                .build();
    }

    @Transactional
    @Override
    public void createOrderFromMessage(OrdersSubmitDTO ordersSubmitDTO, Long userId, String orderNumber, List<ShoppingCart> shoppingCarts) {
        Orders existedOrder = ordersMapper.getByNumber(orderNumber);
        if (existedOrder != null) {
            log.info("订单已存在，忽略重复消息，orderNumber={}", orderNumber);
            return;
        }

        AddressBook addressBook = addressBookService.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new BaseException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new BaseException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        LocalDateTime now = LocalDateTime.now();

        // 向订单插入数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(userId);
        orders.setOrderTime(now);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(orderNumber);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(formatAddress(addressBook));
        ordersMapper.insert(orders);

        //向订单明细插入多条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        Long orderId = orders.getId();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);
    }

    private List<ShoppingCart> validateAndGetShoppingCart(Long userId, Long addressBookId) {
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new BaseException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new BaseException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        return shoppingCartList;
    }

    private String generateOrderNumber() {
        return System.currentTimeMillis() + RandomStringUtils.randomNumeric(4);
    }

    private String formatAddress(AddressBook addressBook) {
        String province = addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName();
        String city = addressBook.getCityName() == null ? "" : addressBook.getCityName();
        String district = addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName();
        String detail = addressBook.getDetail() == null ? "" : addressBook.getDetail();
        return province + city + district + detail;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = RandomStringUtils.randomNumeric(32);
        OrderPaymentVO orderPaymentVO = OrderPaymentVO.builder()
                .timeStamp(timeStamp)
                .nonceStr(nonceStr)
                .signType("RSA")
                .build();
        return orderPaymentVO;
    }

    @Override
    public void paySuccess(String number) {
        Long userId = 4L;   // 此处应该由wx回调
        Orders od = ordersMapper.getByNumberAndUserId(userId, number);
        log.info("userId:{}", userId);
        log.info("number:{}", number);
        Orders orders = Orders.builder()
                .id(od.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);
        Map map = new HashMap<>();
        map.put("type", ReminderConstant.CALL_REMINDER);
        map.put("orderId", od.getId());
        map.put("content", "订单号：" + number);

        String json = JSON.toJSONString(map);
        log.info("json:{}", json);
        webSocketServer.sendToAllClient(json);
    }

    @Override
    public OrderStatisticsVO statistics() {
        Integer confirmed = ordersMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = ordersMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer toBeConfirmed = ordersMapper.countStatus(Orders.TO_BE_CONFIRMED);

        OrderStatisticsVO orderStatisticsVO = OrderStatisticsVO.builder()
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress)
                .toBeConfirmed(toBeConfirmed).build();
        return orderStatisticsVO;
    }

    @Override
    public PageResult conditionSearch(OrderPageQueryDTO orderPageQueryDTO) {
        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());
        Page<Orders> page = ordersMapper.pageQuery(orderPageQueryDTO);
        List<OrderVO> orderVOList = new ArrayList<>();
        List<Orders> ordersList = page.getResult();
        if (!ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), orderVOList);
    }

    private String getOrderDishesStr(Orders orders) {
        List<OrderDetail> list = orderDetailMapper.getByOrderId(orders.getId());
        List<String> orderDishList = list.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        return orderDishList.toString();
    }

    @Override
    public void reminder(Long id) {
        Orders orders = ordersMapper.getById(id);

        if (orders == null) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map map = new HashMap<>();
        map.put("type", ReminderConstant.USER_REMINDER);
        map.put("orderId", id);
        map.put("content", "订单号：" + orders.getNumber());
//        log.info("json:{}", JSON.toJSONString(map));
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        ordersMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = ordersMapper.getById(ordersRejectionDTO.getId());

        if (orders == null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Integer payStatus = orders.getPayStatus();
        if (payStatus.equals(Orders.PAID)) {
            // 需要退款
        }

        Orders od = Orders.builder()
                .id(orders.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        ordersMapper.update(od);
    }

    @Override
    public void userCancelById(Long id) {
        Orders orders = ordersMapper.getById(id);

        if (orders == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (orders.getStatus() > 2) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders od = new Orders();
        od.setId(id);
        if (orders.getPayStatus().equals(Orders.PAID)) {
            // 实现退款功能
            od.setPayStatus(Orders.REFUND);
        }
        od.setStatus(Orders.CANCELLED);
        od.setCancelTime(LocalDateTime.now());
        od.setCancelReason(MessageConstant.USER_CANCEL);
        ordersMapper.update(od);
    }

    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> shoppingCarts = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartMapper.insertBatch(shoppingCarts);
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = ordersMapper.getById(ordersCancelDTO.getId());

        if (orders.getPayStatus().equals(Orders.PAID)) {
            // 退款
        }

        Orders od = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelTime(LocalDateTime.now())
                .cancelReason(ordersCancelDTO.getCancelReason())
                .build();
        ordersMapper.update(od);
    }

    @Override
    public void delivery(Long id) {
        Orders orders = ordersMapper.getById(id);

        if (orders == null || !orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders od = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        ordersMapper.update(od);
    }

    @Override
    public void complete(Long id) {
        Orders orders = ordersMapper.getById(id);

        if (orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders od = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        ordersMapper.update(od);
    }
}
