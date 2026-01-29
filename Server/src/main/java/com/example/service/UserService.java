package com.example.service;

import com.example.DTO.UserLoginDTO;
import com.example.entity.User;

public interface UserService {
    public User wxLogin(UserLoginDTO userLoginDTO);
}
