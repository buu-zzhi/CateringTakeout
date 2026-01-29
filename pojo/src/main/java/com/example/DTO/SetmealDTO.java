package com.example.DTO;

import com.example.entity.SetmealDish;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetmealDTO implements Serializable {
    private Long categoryId;
    private String description;
    private Long id;
    private String image;
    private String name;
    private BigDecimal price;
    List<SetmealDish> setmealDishes;
    private Integer status;
}
