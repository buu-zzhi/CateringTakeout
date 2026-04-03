package com.example.mq.producer;

import com.example.mq.message.OrderCreateMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.mq.enabled", havingValue = "true")
public class OrderCreateProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${app.mq.order-create-topic}")
    private String orderCreateTopic;

    public void sendOrderCreateMessage(OrderCreateMessage message) {
        if (message.getRetryCount() == null) {
            message.setRetryCount(0);
        }
        rocketMQTemplate.convertAndSend(orderCreateTopic, message);
        log.info("订单创建消息发送成功, orderNumber={}, userId={}, retryCount={}",
                message.getOrderNumber(), message.getUserId(), message.getRetryCount());
    }
}
