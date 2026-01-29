package com.example.mapper;

import com.example.DTO.DishPageQueryDTO;
import com.example.annotation.AutoFill;
import com.example.entity.Dish;
import com.example.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DishMapper {
    Page<Dish> list(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    void deleteById(List<Long> ids);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    List<Dish> userList(Dish dish);

    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long id);

    Integer countByMap(Map map);
}
