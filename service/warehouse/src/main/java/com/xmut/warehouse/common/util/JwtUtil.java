package com.xmut.warehouse.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret; // 配置文件中的密钥（如xmut_warehouse_jwt_2025）
    @Value("${jwt.expire}")
    private long expire;

    // 核心：基于配置的secret生成固定密钥（避免每次随机）
    private SecretKey getSecretKey() {
        // 将配置的secret转为足够长度的字节数组（不足则补全）
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            byte[] newKeyBytes = new byte[32];
            System.arraycopy(keyBytes, 0, newKeyBytes, 0, keyBytes.length);
            keyBytes = newKeyBytes;
        }
        return Keys.hmacShaKeyFor(keyBytes); // 生成固定的HS256密钥
    }

    // 生成Token（用固定密钥）
    public String generateToken(String userId, Integer role) {
        SecretKey key = getSecretKey(); // 用固定密钥
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析Token（用同一个固定密钥）
    public Claims getClaimsByToken(String token) {
        try {
            SecretKey key = getSecretKey(); // 用同一个固定密钥
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null; // Token无效/过期
        }
    }

    // 验证Token是否过期
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
