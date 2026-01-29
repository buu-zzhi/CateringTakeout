package com.example.task;

import com.example.constant.MessageConstant;
import com.example.entity.Orders;
import com.example.mapper.OrdersMapper;
import com.example.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 定时处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        LocalDateTime ddl = LocalDateTime.now().plusMinutes(-15);
        List<Orders> list= ordersMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, ddl);

        if (list != null && !list.isEmpty()) {
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason(MessageConstant.UN_PAY_CANCELLED_REASON);
                orders.setCancelTime(LocalDateTime.now());
                ordersMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void processDeliveryOrder() {
        LocalDateTime ddl = LocalDateTime.now().plusHours(-1);
        List<Orders> list = ordersMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, ddl);
        if (list != null && !list.isEmpty()) {
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
                ordersMapper.update(orders);
            }
        }
    }

    /**
     * websocket测试方法
     */
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void sendMessageToClient() {
//        webSocketServer.sendToAllClient("来自服务端的消息：" + LocalDateTime.now());
//    }
}
