package com.example.blogdemo.controller;

import com.example.blogdemo.common.Result;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequiresAuthentication //需要登录
    @GetMapping("/index")
    public Result index(){
        User user = userService.getById(1L);
        return Result.succ(user);
    }

    //验证不通过，抛出异常，全局异常处理器处理
    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user){
        userService.save(user);
        return Result.succ(user);
    }
}
