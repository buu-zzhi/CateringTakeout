package com.example.mq.consumer;

import com.example.mq.message.OrderCreateDeadLetterMessage;
import com.example.mq.message.OrderCreateMessage;
import com.example.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "${app.mq.order-create-topic}",
        consumerGroup = "${app.mq.order-create-consumer-group}",
        consumeMode = ConsumeMode.CONCURRENTLY
)
@ConditionalOnProperty(name = "app.mq.enabled", havingValue = "true")
public class OrderCreateConsumer implements RocketMQListener<OrderCreateMessage> {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${app.mq.order-create-max-reconsume-times}")
    private Integer maxReconsumeTimes;

    @Value("${app.mq.order-create-dead-letter-topic}")
    private String deadLetterTopic;

    @Value("${app.mq.order-create-topic}")
    private String orderCreateTopic;

    @Value("${app.mq.order-create-retry-delay-level}")
    private Integer retryDelayLevel;

    @Override
    public void onMessage(OrderCreateMessage payload) {
        int retryCount = payload.getRetryCount() == null ? 0 : payload.getRetryCount();

        log.info("收到订单创建消息, orderNumber={}, userId={}, retryCount={}",
                payload.getOrderNumber(), payload.getUserId(), retryCount);

        try {
            ordersService.createOrderFromMessage(
                    payload.getOrdersSubmitDTO(),
                    payload.getUserId(),
                    payload.getOrderNumber(),
                    payload.getShoppingCarts()
            );
        } catch (Exception e) {
            if (retryCount >= maxReconsumeTimes) {
                OrderCreateDeadLetterMessage deadLetterMessage = OrderCreateDeadLetterMessage.builder()
                        .orderCreateMessage(payload)
                        .reconsumeTimes(retryCount)
                        .errorMessage(e.getMessage())
                        .failedAt(LocalDateTime.now())
                        .build();
                rocketMQTemplate.convertAndSend(deadLetterTopic, deadLetterMessage);
                log.error("订单创建消息重试达到上限, 已转入死信队列. orderNumber={}, reconsumeTimes={}, maxReconsumeTimes={}",
                        payload.getOrderNumber(), retryCount, maxReconsumeTimes, e);
                return;
            }

            payload.setRetryCount(retryCount + 1);
            rocketMQTemplate.syncSend(
                    orderCreateTopic,
                    MessageBuilder.withPayload(payload).build(),
                    3000,
                    retryDelayLevel
            );
            log.warn("订单创建消息消费失败, 已投递重试消息. orderNumber={}, nextRetryCount={}, maxReconsumeTimes={}",
                    payload.getOrderNumber(), payload.getRetryCount(), maxReconsumeTimes, e);
        }
    }
}
