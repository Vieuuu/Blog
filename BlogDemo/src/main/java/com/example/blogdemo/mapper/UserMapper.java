package com.example.blogdemo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blogdemo.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


//在对应的Mapper 接口上 基础基本的 BaseMapper<T> T是对应的pojo类
@Repository //告诉容器你是持久层的 @Repository是spring提供的注释，能够将该类注册成Bean
public interface UserMapper extends BaseMapper<User> {

}