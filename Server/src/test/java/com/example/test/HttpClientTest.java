package com.example.test;

import com.example.properties.JwtProperties;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

//@SpringBootTest
public class HttpClientTest {
    @Autowired
    private HttpClientProperties httpClientProperties;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtProperties jwtProperties;

    @Test
    public void testGet() throws IOException {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");
        String token = redisTemplate.opsForValue().get(jwtProperties.getAdminTokenName()).toString();
         httpGet.addHeader(jwtProperties.getAdminTokenName(), token);
         CloseableHttpResponse response = closeableHttpClient.execute(httpGet);

         int statusCode = response.getStatusLine().getStatusCode();
         System.out.println(statusCode);

         HttpEntity entity = response.getEntity();
         String body = EntityUtils.toString(entity);
        System.out.println(body);

        response.close();
        closeableHttpClient.close();
    }

}
