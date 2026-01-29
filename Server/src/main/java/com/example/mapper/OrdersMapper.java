package com.example.mapper;

import com.example.DTO.GoodsSalesDTO;
import com.example.DTO.OrderPageQueryDTO;
import com.example.entity.Orders;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface OrdersMapper {
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    Page<Orders> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    void insert(Orders orders);

    void update(Orders orders);

    @Select("select * from orders where status=#{status} and order_time <= #{ddl}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime ddl);

    @Select("select * from orders where user_id=#{userId} and number=#{number}")
    Orders getByNumberAndUserId(Long userId, String number);

    @Select("select count(id) from orders where status=#{status}")
    Integer countStatus(Integer status);

    Double sumByDay(Map map);

    Integer countByMap(Map map);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
