package com.example.blogdemo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blogdemo.entity.Blog;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogMapper extends BaseMapper<Blog> {

}