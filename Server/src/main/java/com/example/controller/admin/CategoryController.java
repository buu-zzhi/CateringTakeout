package com.example.controller.admin;

import com.example.DTO.CategoryDTO;
import com.example.DTO.CategoryPageQueryDTO;
import com.example.entity.Category;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @GetMapping("/list")
    public Result<List<Category>> getByType(Integer type) {
        List<Category> categoryList = categoryService.getByType(type);
        return Result.success(categoryList);
    }

    @DeleteMapping
    public Result deleteById(Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }

    @PostMapping
    public Result addCate(@RequestBody CategoryDTO categoryDTO) {
        categoryService.addCate(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
}
