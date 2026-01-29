package com.example.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataVO {
    private Integer newUsers;
    private Double orderCompletionRate;
    private Double turnover;
    private Double unitPrice;
    private Integer validOrderCount;
}
