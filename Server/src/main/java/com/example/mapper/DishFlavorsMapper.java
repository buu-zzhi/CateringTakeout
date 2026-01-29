package com.example.mapper;

import com.example.annotation.AutoFill;
import com.example.entity.DishFlavors;
import com.example.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface DishFlavorsMapper {
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavors> getByDishId(Long dishId);


    void deleteByDishId(List<Long> ids);

    void insertBatch(List<DishFlavors> flavors);
}
