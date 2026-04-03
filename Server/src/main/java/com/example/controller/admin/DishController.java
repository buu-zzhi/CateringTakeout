package com.example.controller.admin;

import com.example.DTO.DishModifyDTO;
import com.example.DTO.DishPageQueryDTO;
import com.example.VO.DishVO;
import com.example.entity.Dish;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    public Result<List<Dish>> getByCategoryId(Long categoryId) {
        List<Dish> dishList = dishService.getByCategoryId(categoryId);
        return Result.success(dishList);
    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        dishService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody DishModifyDTO dishModifyDTO) {
        dishService.update(dishModifyDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Long> ids) {
        dishService.deleteByIds(ids);
        return Result.success();
    }

    @PostMapping
    public Result addDish(@RequestBody DishModifyDTO dishModifyDTO) {
        dishService.addDish(dishModifyDTO);
        return Result.success();
    }

//    private void cleanCache(String pattern) {
//        Set key = redisTemplate.keys(pattern);
//        redisTemplate.delete(key);
//    }
}
