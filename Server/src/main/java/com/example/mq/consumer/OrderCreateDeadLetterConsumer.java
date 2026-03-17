package com.example.mq.consumer;

import com.example.mq.message.OrderCreateDeadLetterMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "${app.mq.order-create-dead-letter-topic}",
        consumerGroup = "${app.mq.order-create-dead-letter-consumer-group}",
        consumeMode = ConsumeMode.CONCURRENTLY
)
public class OrderCreateDeadLetterConsumer implements RocketMQListener<OrderCreateDeadLetterMessage> {

    @Override
    public void onMessage(OrderCreateDeadLetterMessage message) {
        log.error("收到订单创建死信消息, orderNumber={}, userId={}, reconsumeTimes={}, errorMessage={}, failedAt={}",
                message.getOrderCreateMessage().getOrderNumber(),
                message.getOrderCreateMessage().getUserId(),
                message.getReconsumeTimes(),
                message.getErrorMessage(),
                message.getFailedAt());
    }
}
