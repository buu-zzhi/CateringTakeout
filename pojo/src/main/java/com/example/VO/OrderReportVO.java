package com.example.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportVO implements Serializable {
    private String dateList;
    private Double orderCompletionRate;
    private String orderCountList;
    private Integer totalOrderCount;
    private Integer validOrderCount;
    private String validOrderCountList;
}
