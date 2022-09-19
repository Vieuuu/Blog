package com.example.blogdemo.common.exception;

import com.example.blogdemo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，@ExceptionHandler进行标注对应异常类
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //前后端分离项目，返回状态码给前端分析
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e){
        log.error("运行时异常:------------{}", e);
        return Result.fail(e.getMessage());
    }

    //Shiro异常说明登录失败，抛出UnAUthorized状态码
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = ShiroException.class)
    public Result handler(ShiroException e){
        log.error("Shiro登陆处理异常:--- ---------{}", e);
        return Result.fail(401, e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e){
        log.error("实体校验异常:--- ---------{}", e);
        //获取所有的绑定错误
        BindingResult bindingResult = e.getBindingResult();
        //只取一个错误进行发送
        ObjectError error = bindingResult.getAllErrors().stream().findFirst().get();
        return Result.fail(error.getDefaultMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e){
        log.error("Assert异常:--- ---------{}", e);
        return Result.fail(e.getMessage());
    }

}
