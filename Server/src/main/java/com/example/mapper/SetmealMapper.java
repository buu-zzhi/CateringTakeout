package com.example.mapper;

import com.example.DTO.SetmealPageQueryDTO;
import com.example.VO.DishItemVO;
import com.example.VO.SetmealVO;
import com.example.annotation.AutoFill;
import com.example.entity.Setmeal;
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
public interface SetmealMapper {
    List<Setmeal> list(Setmeal setmeal);

    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id=d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemById(Long setmealId);

    Page<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    @Select("select * from setmeal where id=#{id}")
    Setmeal getById(Long id);

    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    @Delete("delete from setmeal where id=#{setmealId}")
    void deleteById(Long setmealId);

    Integer countByMap(Map map);
}
