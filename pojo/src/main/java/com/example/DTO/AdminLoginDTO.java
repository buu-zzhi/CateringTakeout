package com.example.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "前端传递给后端的登陆数据")
public class AdminLoginDTO implements Serializable {
    @ApiModelProperty(value = "登陆用户名")
    private String username;
    @ApiModelProperty(value = "登陆密码")
    private String password;
}
