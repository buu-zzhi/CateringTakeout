package com.example.VO;

import com.example.entity.DishFlavors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishVO implements Serializable {
    private Long categoryId;
    private String CategoryName;
    private String description;
    private List<DishFlavors> flavors = new ArrayList<>();
    private Long id;
    private String image;
    private String name;
    private BigDecimal price;
    private Integer status;
    private LocalDateTime updateTime;
}
