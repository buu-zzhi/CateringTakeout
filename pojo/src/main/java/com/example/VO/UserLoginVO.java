package com.example.VO;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserLoginVO implements Serializable {
    private Long id;
    private String openid;
    private String token;
}
