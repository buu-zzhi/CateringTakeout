package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishPageQueryDTO implements Serializable {
    private int page;
    private int pageSize;
    private String name;
    private Long categoryId;
    private Integer status;
}
