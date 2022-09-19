package com.example.blogdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blogdemo.entity.Blog;
import com.example.blogdemo.mapper.BlogMapper;
import com.example.blogdemo.service.BlogService;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
