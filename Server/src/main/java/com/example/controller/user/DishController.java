package com.example.controller.user;

import com.example.VO.DishVO;
import com.example.constant.StatusConstant;
import com.example.entity.Dish;
import com.example.result.Result;
import com.example.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @Cacheable(cacheNames = "DishCache", key = "#categoryId")
    public Result<List<DishVO>> list(Long categoryId) {
        // 首先在redis中查询菜品是否存在
//        String key = "dish_" + categoryId;
//        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
//        if (list != null) {
//            return Result.success(list);
//        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        List<DishVO> list = dishService.listWithFlavor(dish);

        // 将数据存储至redis
//        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }
}
