package com.example.service;

import com.example.DTO.*;
import com.example.VO.OrderPaymentVO;
import com.example.VO.OrderStatisticsVO;
import com.example.VO.OrderVO;
import com.example.VO.OrdersSubmitVO;
import com.example.entity.Orders;
import com.example.result.PageResult;
import org.apache.ibatis.annotations.Select;

public interface OrdersService {
    OrderVO getById(Long id);

    PageResult page(OrderPageQueryDTO orderPageQueryDTO);

    OrdersSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

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
