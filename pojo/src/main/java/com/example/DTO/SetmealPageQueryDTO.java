package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetmealPageQueryDTO implements Serializable {
    private Long categoryId;
    private String name;
    private int page;
    private int pageSize;
    private Integer status;
}
