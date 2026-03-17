package com.example.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersSubmitVO {
    private Long id;
    private BigDecimal orderAmount;
    private String orderNumber;
    private LocalDateTime orderTime;

    /**
     * 是否已进入异步处理队列
     */
    private Boolean accepted;

    /**
     * 下单处理状态：QUEUED/SUCCESS/FAILED
     */
    private String submitStatus;

    /**
     * 状态描述信息
     */
    private String message;
}
