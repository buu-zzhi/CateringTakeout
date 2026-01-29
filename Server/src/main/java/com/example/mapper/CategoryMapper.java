package com.example.mapper;

import com.example.DTO.CategoryPageQueryDTO;
import com.example.annotation.AutoFill;
import com.example.entity.Category;
import com.example.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CategoryMapper {
    List<Category> getByType(Integer type);

    @Delete("delete from category where id=#{id}")
    void deleteById(Long id);

    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void addCate(Category category);

    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    Page<Category> list(CategoryPageQueryDTO categoryPageQueryDTO);
}
