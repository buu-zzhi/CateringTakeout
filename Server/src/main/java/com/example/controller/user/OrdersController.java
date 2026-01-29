package com.example.controller.user;

import com.example.DTO.OrderPageQueryDTO;
import com.example.DTO.OrdersPaymentDTO;
import com.example.DTO.OrdersSubmitDTO;
import com.example.VO.OrderPaymentVO;
import com.example.VO.OrderVO;
import com.example.VO.OrdersSubmitVO;
import com.example.entity.Orders;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrdersController")
@RequestMapping("/user/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        OrderVO orderVO = ordersService.getById(id);
        return Result.success(orderVO);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> page(OrderPageQueryDTO orderPageQueryDTO) {
        PageResult result = ordersService.page(orderPageQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id) {
        ordersService.reminder(id);
        return Result.success();
    }

    @PostMapping("/submit")
    public Result<OrdersSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrdersSubmitVO ordersSubmitVO = ordersService.submitOrder(ordersSubmitDTO);
        return Result.success(ordersSubmitVO);
    }

    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        OrderPaymentVO orderPaymentVO = ordersService.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        ordersService.userCancelById(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id) {
        ordersService.repetition(id);
        return Result.success();
    }


}
