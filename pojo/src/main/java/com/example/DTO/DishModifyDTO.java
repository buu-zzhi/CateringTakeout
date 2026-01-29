package com.example.DTO;

import com.example.entity.DishFlavors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishModifyDTO implements Serializable {
    private Long categoryId;
    private String description;
    private List<DishFlavors> flavors = new ArrayList<>();
    private Long id;
    private String image;
    private BigDecimal price;
    private String name;
    private Integer status;
}
