package com.example.controller.admin;

import com.example.constant.ShopConstant;
import com.example.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("{status}")
    public Result setStatus(@PathVariable Integer status) {
        redisTemplate.opsForValue().set(ShopConstant.SHOP_STATUS_KEY, status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(ShopConstant.SHOP_STATUS_KEY);
        return Result.success(status);
    }
}
