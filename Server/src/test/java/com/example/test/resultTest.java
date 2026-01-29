package com.example.test;

import com.example.result.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class resultTest {
    @Test
    public void resultTest() throws JsonProcessingException {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "zhangsan");
        info.put("age", 18);
        Result r = Result.success(info);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        System.out.println(json);
    }
}
