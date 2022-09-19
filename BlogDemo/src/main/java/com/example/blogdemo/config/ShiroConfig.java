package com.example.blogdemo.config;

import com.example.blogdemo.shiro.AccountRealm;
import com.example.blogdemo.shiro.JwtFilter;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig{
    @Resource
    JwtFilter jwtFilter;

    @Bean
    public SessionManager sessionManager(RedisSessionDAO redisSessionDAO){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();

        //注入redisSessionDao，为redis-shiro要求
        sessionManager.setSessionDAO(redisSessionDAO);
        return sessionManager;
    }

    //realms里面存储用户相关信息,需要自定义
    //自定义了AccountRealm
    @Bean
    public SessionsSecurityManager securityManager(AccountRealm accountRealm, SessionManager sessionManager, RedisCacheManager redisCacheManager){
        //设置SecurityManager的数据源为accountRealm
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(accountRealm);
        //注入sessionManager
        securityManager.setSessionManager(sessionManager);
        //注入redisCacheManager，为redis-shiro要求
        securityManager.setCacheManager(redisCacheManager);
        return securityManager;
    }

    //shiro过滤器链定义
    //定义什么链接需要经过什么过滤器
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String, String> filterMap = new LinkedHashMap<>();
        //说明所有的链接都要经过这个过滤器
        filterMap.put("/**", "jwt"); // 主要通过注解方式校验权限
        chainDefinition.addPathDefinitions(filterMap);
        return chainDefinition;
    }


    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        //定义ShiroFilterFactoryBean的过滤器，存储在Map中
        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt", jwtFilter);
        shiroFilter.setFilters(filters);
        //获取定义的过滤器链
        Map<String, String> filterMap = shiroFilterChainDefinition.getFilterChainMap();
        //设置过滤器链
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }
}
