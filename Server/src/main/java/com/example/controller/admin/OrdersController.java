package com.example.controller.admin;

import com.example.DTO.OrderPageQueryDTO;
import com.example.DTO.OrdersCancelDTO;
import com.example.DTO.OrdersConfirmDTO;
import com.example.DTO.OrdersRejectionDTO;
import com.example.VO.OrderStatisticsVO;
import com.example.VO.OrderVO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.OrdersService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrdersController")
@RequestMapping("/admin/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO = ordersService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id) {
        OrderVO orderVO = ordersService.getById(id);
        return Result.success(orderVO);
    }

    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrderPageQueryDTO orderPageQueryDTO) {
        PageResult page = ordersService.conditionSearch(orderPageQueryDTO);
        return Result.success(page);
    }

    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        ordersService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        ordersService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        ordersService.cancel(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable("id") Long id) {
        ordersService.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable("id") Long id) {
        ordersService.complete(id);
        return Result.success();
    }
}
