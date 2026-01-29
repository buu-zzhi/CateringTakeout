package com.example.service;

import com.example.DTO.CategoryDTO;
import com.example.DTO.CategoryPageQueryDTO;
import com.example.entity.Category;
import com.example.result.PageResult;

import java.util.List;

public interface CategoryService {
    List<Category> getByType(Integer type);

    void deleteById(Long id);

    void addCate(CategoryDTO categoryDTO);

    void startOrStop(Integer status, Long id);

    void update(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
}
