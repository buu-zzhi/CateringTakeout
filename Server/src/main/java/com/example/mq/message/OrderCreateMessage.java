package com.example.mq.message;

import com.example.DTO.OrdersSubmitDTO;
import com.example.entity.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateMessage implements Serializable {
    private Long userId;

    /*
    * 订单id 用于做幂等防重
    */
    private String orderNumber;
    private OrdersSubmitDTO ordersSubmitDTO;
    /*
    * 记录当前购物车快照
    */
    private List<ShoppingCart> shoppingCarts;

    /*
    * 自定义重试次数（应用层重试）
    */
    private Integer retryCount;
}
