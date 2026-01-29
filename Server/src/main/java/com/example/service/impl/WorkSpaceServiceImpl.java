package com.example.service.impl;

import com.example.VO.BusinessDataVO;
import com.example.VO.DishOverViewVO;
import com.example.VO.OrderOverViewVO;
import com.example.VO.SetmealOverViewVO;
import com.example.constant.StatusConstant;
import com.example.entity.Orders;
import com.example.mapper.DishMapper;
import com.example.mapper.OrdersMapper;
import com.example.mapper.SetmealMapper;
import com.example.mapper.UserMapper;
import com.example.service.WorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public BusinessDataVO getBusinessDataVO(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        Integer newUsers = userMapper.countByMap(map);
        Integer totalOrderCount = ordersMapper.countByMap(map);

        map.put("status", Orders.COMPLETED);
        Integer validOrderCount = ordersMapper.countByMap(map);
        Double turnover = ordersMapper.sumByDay(map);
        turnover = turnover == null ? 0 : turnover;

        Double orderCompletionRate = 0.0;
        Double unitPrice = 0.0;
        if (totalOrderCount != 0 && validOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            unitPrice = turnover.doubleValue() / validOrderCount;
        }
        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .validOrderCount(validOrderCount)
                .build();
    }

    @Override
    public SetmealOverViewVO getOverViewSetmeals() {
        Map map = new HashMap<>();
        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(map);

        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(map);
        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    @Override
    public DishOverViewVO getOverViewDishes() {
        Map map = new HashMap<>();
        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(map);

        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(map);
        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    @Override
    public OrderOverViewVO getOverViewOrders() {
        Map map = new HashMap<>();
        Integer allOrders = ordersMapper.countByMap(map);

        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders  = ordersMapper.countByMap(map);

        map.put("status", Orders.COMPLETED);
        Integer completedOrders  = ordersMapper.countByMap(map);

        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders  = ordersMapper.countByMap(map);

        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders  = ordersMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .completedOrders(completedOrders)
                .build();
    }
}
