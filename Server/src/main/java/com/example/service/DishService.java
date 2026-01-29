package com.example.service;

import com.example.DTO.DishModifyDTO;
import com.example.DTO.DishPageQueryDTO;
import com.example.VO.DishVO;
import com.example.entity.Dish;
import com.example.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DishService {
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    List<Dish> getByCategoryId(Long categoryId);

    DishVO getByIdWithFlavor(Long id);

    void updateStatus(Long id, Integer status);

    void update(DishModifyDTO dishModifyDTO);

    void deleteByIds(List<Long> ids);

    void addDish(DishModifyDTO dishModifyDTO);

    List<DishVO> listWithFlavor(Dish dish);
}
