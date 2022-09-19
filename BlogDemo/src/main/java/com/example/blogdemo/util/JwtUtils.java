package com.example.blogdemo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

//Jwt工具类
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "jwtproperties.jwt")
public class JwtUtils {

    //通过配置文件注入
    private String secret;  //密钥
    private long expire;    //过期时间
    private String header;  //头部

    /**
     * 生成jwt token
     * setExpiration 设置Jwt的过期时间
     * signWith 设置签名
     * setIssuedAt 创建时间
     *
     */
    public String generateToken(long userId) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userId+"")  //将userId存入subject作为主体
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 验证token，解析Jwt字符串
     */
    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            log.debug("Validation error", e);
            return null;
        }
    }

    /**
     * token是否过期
     * @return true 过期
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}