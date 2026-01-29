package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Mapper
public interface UserMapper {
    @Select("select * from user where openid=#{openid}")
    public User getByOpenid(String openid);

    void insert(User user);

    Integer countByMap(Map map);
}
