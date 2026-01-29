package com.example.service.impl;

import com.example.DTO.CategoryDTO;
import com.example.DTO.CategoryPageQueryDTO;
import com.example.constant.MessageConstant;
import com.example.constant.StatusConstant;
import com.example.context.BaseContext;
import com.example.entity.Category;
import com.example.exception.BaseException;
import com.example.mapper.CategoryMapper;
import com.example.mapper.DishMapper;
import com.example.mapper.SetmealMapper;
import com.example.result.PageResult;
import com.example.service.CategoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public List<Category> getByType(Integer type) {
        return categoryMapper.getByType(type);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
//        TODO 待修改
//        Integer count = dishMapper.countByCategoryId(id);
//        if(count > 0){
//            //当前分类下有菜品，不能删除
//            throw new BaseException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
//        }
//
//        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
//        count = setmealMapper.countByCategoryId(id);
//        if(count > 0){
//            //当前分类下有菜品，不能删除
//            throw new BaseException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
//        }
        categoryMapper.deleteById(id);
    }

    @Override
    public void addCate(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.addCate(category);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        categoryMapper.update(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> categoryPage = categoryMapper.list(categoryPageQueryDTO);
        List<Category> categories = categoryPage.getResult();
        long total = (long) categoryPage.getTotal();
        return new PageResult(total, categories);
    }
}
