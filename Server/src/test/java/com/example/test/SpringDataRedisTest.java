package com.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

//@SpringBootTest
@ActiveProfiles("test")
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate() {
        System.out.println(redisTemplate);
    }

    @Test
    public void testString() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("age", 18);
        valueOperations.set("yzm", 18, 60, TimeUnit.SECONDS);
        System.out.println(valueOperations.get("name"));
        valueOperations.setIfAbsent("sex", "famale");
        valueOperations.setIfAbsent("age", 19);
        System.out.println(valueOperations.get("age"));
    }

    @Test
    public void TestHash() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("people", "name", "zhangsan");
        hashOperations.put("people", "age", 18);
        hashOperations.put("people", "yzm", 893789);
        System.out.println(hashOperations.get("people", "age"));
        hashOperations.delete("people", "age");
        System.out.println(hashOperations.get("people", "age"));
        System.out.println(hashOperations.keys("people"));
        System.out.println(hashOperations.values("people"));
    }

    @Test
    public void testList() {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPushAll("list", 1,2, 3, "a", "anc");
        listOperations.leftPush("list", "list");
        System.out.println(listOperations.range("list", 0, -1));
        System.out.println(listOperations.size("list"));
        listOperations.rightPop("list");
        System.out.println(listOperations.range("list", 0, -1));
    }

    @Test
    public void testSet() {
        SetOperations setOperations = redisTemplate.opsForSet();
        setOperations.add("set1", "a", 1, 2, 3, "c", "d");
        setOperations.add("set2", "c", 1, 4, 3, "e", "f");
        System.out.println(setOperations.members("set1"));
        System.out.println(setOperations.size("set2"));
        System.out.println(setOperations.intersect("set1", "set2"));
        setOperations.remove("set1", 1, 2, 3, 4);
        System.out.println(setOperations.union("set1", "set2"));
    }

    @Test
    public void testZSet() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("zset", "a", 3);
        zSetOperations.add("zset", "df", 0.1);
        zSetOperations.add("zset", "zako", 3.14);
        zSetOperations.add("zset", "zayu", 10);
        System.out.println(zSetOperations.range("zset", 0, -1));
        zSetOperations.incrementScore("zset", "df", 5);
        zSetOperations.remove("zset", "a");
        System.out.println(zSetOperations.range("zset", 0, -1));
    }

    @Test
    public void testCommon() {
        System.out.println(redisTemplate.keys("*"));
        System.out.println(redisTemplate.hasKey("address"));
        System.out.println(redisTemplate.type("people"));
        redisTemplate.delete("set2");
        System.out.println(redisTemplate.keys("*"));
    }
}
