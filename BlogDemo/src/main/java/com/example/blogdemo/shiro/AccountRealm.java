package com.example.blogdemo.shiro;

//自定义Realm

import cn.hutool.core.bean.BeanUtil;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.service.UserService;
import com.example.blogdemo.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Realm提供待验证数据的比对值，即安全数据源,可以理解为数据的源头，可以是数据库，文件等。
 * Shiro从从Realm获取安全数据（如用户、角色、权限），就是说SecurityManager要验证用户
 * 身份，那么它需要从Realm获取相应的用户进行比较以确定用户身份是否合法；也需要从Realm得
 * 到用户相应的角色/权限进行验证用户是否能进行操作。
 */
@Component
public class AccountRealm extends AuthorizingRealm {
    @Resource
    JwtUtils jwtUtils;

    @Resource
    UserService userService;

    @Override
    public boolean supports(AuthenticationToken token){
        //仅支持JwtToken类型
        return token instanceof JwtToken;
    }

    //获取用户的权限，将获得的权限信息封装成AuthorizationInfo返回给Shiro
    //权限用于后期进行权限过滤，访问受限资源的验证
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    //通过token存储的用户信息，进行身份验证
    //验证成功则返回相关信息，封装在AuthenticationInfo中
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;
        //getClaimByToken返回的是Claims，通过getSubject方法可以获得用户的Id
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();

        //自定义验证逻辑
        User user = userService.getById(Long.valueOf(userId));
        if(user == null){
            throw new UnknownAccountException("账户不存在");
        }
        if(user.getStatus() == -1){
            throw new LockedAccountException("账户已被锁定");
        }

        //验证通过
        //返回类型为SimpleAuthenticationInfo，其中的principle一般是封装后的用户信息（公开信息）
        //私密信息就不封装进去
        AccountProfile accountProfile = new AccountProfile();
        //利用BeanUtil工具类进行拷贝
        BeanUtil.copyProperties(user, accountProfile);
        return new SimpleAuthenticationInfo(accountProfile, jwtToken.getCredentials(), this.getName());
    }
}
