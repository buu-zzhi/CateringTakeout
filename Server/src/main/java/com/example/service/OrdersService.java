package com.example.service;

import com.example.DTO.*;
import com.example.VO.OrderPaymentVO;
import com.example.VO.OrderStatisticsVO;
import com.example.VO.OrderVO;
import com.example.VO.OrdersSubmitVO;
import com.example.entity.Orders;
import com.example.entity.ShoppingCart;
import com.example.result.PageResult;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrdersService {
    OrderVO getById(Long id);

    PageResult page(OrderPageQueryDTO orderPageQueryDTO);

    /**
     * 异步提交订单（削峰）
     */
    OrdersSubmitVO submitOrderAsync(OrdersSubmitDTO ordersSubmitDTO);

    OrdersSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 根据订单号查询异步下单处理结果
     */
    OrdersSubmitVO querySubmitResult(String orderNumber);

    /**
     * 供MQ消费者调用的落单逻辑（幂等）
     */
    void createOrderFromMessage(OrdersSubmitDTO ordersSubmitDTO, Long userId, String orderNumber, List<ShoppingCart> shoppingCarts);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    void paySuccess(String number);

    OrderStatisticsVO statistics();

    PageResult conditionSearch(OrderPageQueryDTO orderPageQueryDTO);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void reminder(Long id);

    void userCancelById(Long id);

    void repetition(Long id);

    void cancel(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void complete(Long id);
}
