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
}
