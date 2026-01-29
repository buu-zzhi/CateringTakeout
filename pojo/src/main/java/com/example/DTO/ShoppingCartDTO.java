package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDTO implements Serializable {
    private String dishFlavor;
    private Long dishId;
    private Long setmealId;
}
