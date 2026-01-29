package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPageQueryDTO implements Serializable {
    private String name;
    private Integer type;
    private int page;
    private int pageSize;
}
