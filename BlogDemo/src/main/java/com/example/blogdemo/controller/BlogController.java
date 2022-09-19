package com.example.blogdemo.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogdemo.common.Result;
import com.example.blogdemo.entity.Blog;
import com.example.blogdemo.mapper.BlogMapper;
import com.example.blogdemo.service.BlogService;
import com.example.blogdemo.util.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
public class BlogController {
    @Resource
    BlogService blogService;

    //absolute path
    final static String uploadPath = "/home/Beauuu/static/";

    @Resource
    BlogMapper blogMapper;

    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {
        Page page = new Page(currentPage, 5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.succ(pageData);
    }

    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable("id") Long id){
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客已经删除");
        return Result.succ(blog);
    }

    //@Vadidated注解作用于RequiresAuthentication之前
    @RequiresAuthentication
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog){
        Blog temp = null;
        //编辑博客
        if(blog.getId() != null){
            temp = blogService.getById(blog.getId());
            //Principal字段是一个AccountInfo
            Assert.isTrue(temp.getUserId().equals(ShiroUtil.getProfile().getId()), "没有权限编辑");
        }//添加博客
        else{
            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);
        }

        BeanUtil.copyProperties(blog, temp, "id", "userId", "created", "status");
        blogService.saveOrUpdate(temp);
        return Result.succ(null);
    }

    @RequiresAuthentication
    @GetMapping("/blog/delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "对应id的博客已经删除");
        Assert.isTrue(blog.getUserId().equals(ShiroUtil.getProfile().getId()), "没有权限删除");
        blogMapper.deleteById(id);
        return Result.succ(null);
    }

    @RequiresAuthentication
    @PostMapping("/blog/upload")
    public Result upload(@NotNull @RequestParam("image") MultipartFile uploadImg) throws IOException {
        File saveFile = new File(uploadPath);
        if(!saveFile.exists()) {
            saveFile.mkdir();
        }
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String uuid = UUID.randomUUID().toString();
        String saveName = date+"-"+uuid+"-"+uploadImg.getOriginalFilename();
        String savePathName = uploadPath+saveName;
        File result = new File(savePathName);
        uploadImg.transferTo(result);
        String viewUrl = "http://20.196.67.21/static/"+saveName;
        return Result.succ(viewUrl);
    }

//    @RequiresAuthentication
//    @PostMapping("/blog/upload")
//    public Result upload(HttpServletRequest httpServletRequest) throws IOException {
//        Object a = httpServletRequest.getAttributeNames();
//
//        File saveFile = new File(uploadPath);
//
//
//        return Result.succ("1");
//    }
}
