package com.example.blogdemo.shiro;

import cn.hutool.json.JSONUtil;
import com.example.blogdemo.common.Result;
import com.example.blogdemo.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends AuthenticatingFilter {
    @Autowired
    JwtUtils jwtUtils;

    //将jwt封装成AuthenticationToken交给Shiro处理
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //之前定义的header为Authorization
        String jwt = request.getHeader("Authorization");
        //没有jwt直接返回空
        if(!StringUtils.hasText(jwt)){
            return null;
        }
        //创建token
        return new JwtToken(jwt);
    }

    //Shiro处理登录逻辑
    //判断jwt是否过期，异常等
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        //没有jwt，不用进行拦截，直接授予访问
        //后期可能通过角色进行过滤
        if(!StringUtils.hasText(jwt)){
            return true;
        }
        else{
            //校验Jwt
            Claims claim = jwtUtils.getClaimByToken(jwt);
            //如果过期地话
            if(claim == null || jwtUtils.isTokenExpired(claim.getExpiration())){
                throw new ExpiredCredentialsException("token已经失效，请重新登录");
            }
            //校验成功，执行登录处理
            return executeLogin(servletRequest, servletResponse);
        }
    }

    //由于项目前后端分离，当登陆失败抛出异常时，应该返回统一结果封装
    //返回结果用Json格式转换
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        //返回错误原因
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        Throwable throwable = e.getCause() == null ? e : e.getCause();
        Result result = Result.fail(throwable.getMessage());
        //转换为json格式,hutools的工具类
        String json = JSONUtil.toJsonStr(result);
        try {
            servletResponse.getWriter().print(json);
        } catch (IOException ex) {
        }
        return false;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
