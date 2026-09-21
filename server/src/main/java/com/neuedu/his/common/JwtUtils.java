package com.neuedu.his.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具
 */
@Component
public class JwtUtils {

    private static String secretKey;

    /** 令牌有效期：12 小时 */
    private static final long EXPIRE_MS = 12 * 60 * 60 * 1000L;

    @Value("${his.jwt.secretkey:hiscloud}")
    public void setSecretKey(String key) {
        JwtUtils.secretKey = key;
    }

    /**
     * 签发令牌
     *
     * @param userId   用户 ID
     * @param userName 登录名
     * @param useType  用户类别（决定前端可见菜单）
     */
    public static String sign(Integer userId, String userName, Integer useType) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("userId", userId);
        claims.put("userName", userName);
        claims.put("useType", useType);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /** 解析令牌，失败返回 null */
    public static Claims parse(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /** 校验令牌是否有效 */
    public static boolean verify(String token) {
        return token != null && !token.isBlank() && parse(token) != null;
    }
}
