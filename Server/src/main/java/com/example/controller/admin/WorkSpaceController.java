package com.example.controller.admin;

import com.example.VO.BusinessDataVO;
import com.example.VO.DishOverViewVO;
import com.example.VO.OrderOverViewVO;
import com.example.VO.SetmealOverViewVO;
import com.example.result.Result;
import com.example.service.WorkSpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@RestController
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;

    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData() {
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        log.info("begin:{}", begin);
        BusinessDataVO businessDataVO = workSpaceService.getBusinessDataVO(begin, end);
        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> getOverViewSetmeals() {
        SetmealOverViewVO setmeal = workSpaceService.getOverViewSetmeals();
        return Result.success(setmeal);
    }

    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> getOverViewDishes() {
        DishOverViewVO dishOverViewVO = workSpaceService.getOverViewDishes();
        return Result.success(dishOverViewVO);
    }

    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> getOverViewOrders() {
        OrderOverViewVO orderOverViewVO = workSpaceService.getOverViewOrders();
        return Result.success(orderOverViewVO);
    }

}
