package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.DTO.UserLoginDTO;
import com.example.constant.MessageConstant;
import com.example.entity.User;
import com.example.exception.BaseException;
import com.example.mapper.UserMapper;
import com.example.properties.WeChatProperties;
import com.example.service.UserService;
import com.example.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String json = getOpenid(userLoginDTO.getCode());
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        if (openid == null) {
            throw new BaseException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.getByOpenid(openid);

        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "authorization_code");
        map.put("js_code", code);
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        String json = HttpClientUtils.doGet(WX_LOGIN, map);
        return json;
    }
}
