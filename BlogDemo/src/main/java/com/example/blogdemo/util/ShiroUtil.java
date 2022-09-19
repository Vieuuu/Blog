package com.example.blogdemo.util;

import com.example.blogdemo.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;

public class ShiroUtil{
    //因为定义的返回AuthenticationInfo的principal字段是一个AccountInfo
    public static AccountProfile getProfile(){
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }
}
