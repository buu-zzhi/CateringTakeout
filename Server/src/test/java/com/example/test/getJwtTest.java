package com.example.test;

import com.alibaba.fastjson.JSONObject;
import com.example.DTO.AdminLoginDTO;
import com.example.constant.UserConstant;
import com.example.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
//@SpringBootTest
public class getJwtTest {
    @Autowired
    private RestClientSsl restClientSsl;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getJwt() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

        AdminLoginDTO loginDTO = new AdminLoginDTO(UserConstant.USERNAME, UserConstant.DEFAULT_PASSWORD);
        String json = JSONObject.toJSONString(loginDTO);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
//        httpPost.setHeader("Content-Type", "application/json");

        CloseableHttpResponse response = httpClient.execute(httpPost);

        String jsonString = EntityUtils.toString(response.getEntity());
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String token = jsonObject.getJSONObject(jwtProperties.getAdminDataName()).getString(jwtProperties.getAdminTokenName());
        System.out.println(token);
        redisTemplate.opsForValue().set(jwtProperties.getAdminTokenName(), token);

        response.close();
        httpClient.close();
    }

    @Test
    public void getPassword() {
        String pwmd5 = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        System.out.println(pwmd5);
    }
}
