package com.example.mq.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDeadLetterMessage implements Serializable {
    private OrderCreateMessage orderCreateMessage;
    private Integer reconsumeTimes;
    private String errorMessage;
    private LocalDateTime failedAt;
}
