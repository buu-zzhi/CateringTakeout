package com.example.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "新增员工的数据")
public class EmployeeDTO implements Serializable {
    @ApiModelProperty(value = "非必选")
    private Long id;
    private String name;
    private String username;
    private String phone;
    private String sex;
    private String idNumber;
}
