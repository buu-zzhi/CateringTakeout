package com.example.service;

import com.example.DTO.SetmealDTO;
import com.example.DTO.SetmealPageQueryDTO;
import com.example.VO.DishItemVO;
import com.example.VO.SetmealVO;
import com.example.entity.Setmeal;
import com.example.result.PageResult;

import java.util.List;

public interface SetmealService {
    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void update(SetmealDTO setmealDTO);

    SetmealVO getByIdWithDish(Long id);

    void saveWithDish(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);

    void deleteBatch(List<Long> ids);
}
