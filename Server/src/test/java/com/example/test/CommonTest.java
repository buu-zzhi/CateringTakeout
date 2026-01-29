package com.example.test;

import jakarta.servlet.http.HttpServlet;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.LocalTime;

@SpringBootTest
public class CommonTest {
    @Test
    public void testDate() {
        LocalDateTime begin = LocalDateTime.now().minusDays(1).with(LocalTime.MIN);
        System.out.println(begin);
    }
}
