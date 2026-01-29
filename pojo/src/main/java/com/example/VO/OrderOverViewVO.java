package com.example.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOverViewVO {
    private Integer allOrders;
    private Integer cancelledOrders;
    private Integer completedOrders;
    private Integer deliveredOrders;
    private Integer waitingOrders;
}
