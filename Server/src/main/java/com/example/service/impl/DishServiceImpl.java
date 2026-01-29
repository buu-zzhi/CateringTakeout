package com.example.service.impl;

import com.example.DTO.DishModifyDTO;
import com.example.DTO.DishPageQueryDTO;
import com.example.VO.DishVO;
import com.example.constant.MessageConstant;
import com.example.constant.StatusConstant;
import com.example.context.BaseContext;
import com.example.entity.Dish;
import com.example.entity.DishFlavors;
import com.example.exception.BaseException;
import com.example.mapper.DishFlavorsMapper;
import com.example.mapper.DishMapper;
import com.example.mapper.SetmealDishMapper;
import com.example.result.PageResult;
import com.example.service.DishService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorsMapper dishFlavorsMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<Dish> dishPage = dishMapper.list(dishPageQueryDTO);
        long total = dishPage.getTotal();
        List<Dish> dishList = dishPage.getResult();
        return new PageResult(total, dishList);
    }

    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        DishPageQueryDTO dishPageQueryDTO = DishPageQueryDTO.builder()
                                            .categoryId(categoryId)
                                            .build();
        List<Dish> disList = dishMapper.list(dishPageQueryDTO).getResult();
        return disList;
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);

        List<DishFlavors> dishFlavors = dishFlavorsMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Dish dish = Dish.builder().id(id).status(status).build();
        dishMapper.update(dish);
    }

    @Override
    public void update(DishModifyDTO dishModifyDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishModifyDTO, dish);
        dishMapper.update(dish);
        List<Long> ids = new ArrayList<>();
        ids.add(dishModifyDTO.getId());
        dishFlavorsMapper.deleteByDishId(ids);
        List<DishFlavors> flavors = dishModifyDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishModifyDTO.getId());
            });
            dishFlavorsMapper.insertBatch(flavors);
        }
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new BaseException(MessageConstant.DISH_ON_SALE);
            }
        }

        List<Long> setmealIds = setmealDishMapper.getSetmealByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new BaseException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        dishMapper.deleteById(ids);
        dishFlavorsMapper.deleteByDishId(ids);
    }

    @Transactional
    @Override
    public void addDish(DishModifyDTO dishModifyDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishModifyDTO, dish);
        dishMapper.insert(dish);

        Long DishId = dish.getId();
        List<DishFlavors> flavors = dishModifyDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(DishId);
            });
            dishFlavorsMapper.insertBatch(flavors);
        }
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.userList(dish);
        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            List<DishFlavors> flavors = dishFlavorsMapper.getByDishId(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }
}
