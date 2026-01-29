package com.example.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatisticsVO {
    private Integer confirmed;
    private Integer deliveryInProgress;
    private Integer toBeConfirmed;
}
