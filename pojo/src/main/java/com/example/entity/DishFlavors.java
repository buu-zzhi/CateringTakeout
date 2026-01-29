package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishFlavors implements Serializable {
    private Long dishId;
    private Long id;
    private String name;
    private String value;
}
