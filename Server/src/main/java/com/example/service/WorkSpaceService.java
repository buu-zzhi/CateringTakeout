package com.example.service;

import com.example.VO.BusinessDataVO;
import com.example.VO.DishOverViewVO;
import com.example.VO.OrderOverViewVO;
import com.example.VO.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {
    BusinessDataVO getBusinessDataVO(LocalDateTime begin, LocalDateTime end);

    SetmealOverViewVO getOverViewSetmeals();

    DishOverViewVO getOverViewDishes();

    OrderOverViewVO getOverViewOrders();
}
