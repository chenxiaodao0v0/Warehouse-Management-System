package com.xmut.warehouse.config;

import com.xmut.warehouse.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器：解析请求头的Token，将用户信息存入SecurityContext
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 1. 从请求头获取Authorization（Token）
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 没有Token，直接放行（后续SecurityConfig会拦截未认证请求）
            chain.doFilter(request, response);
            return;
        }

        // 2. 提取Token（去掉"Bearer "前缀）
        String token = authHeader.substring(7);

        // 3. 解析Token，获取用户信息
        Claims claims = jwtUtil.getClaimsByToken(token);
        if (claims == null || jwtUtil.isTokenExpired(claims)) {
            // Token无效/过期，直接放行
            chain.doFilter(request, response);
            return;
        }

        // 4. 构造Spring Security的认证对象（存入用户ID和角色）
        String userId = claims.getSubject();
        Integer role = claims.get("role", Integer.class);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId, null, new ArrayList<>() // 权限列表可后续补充，这里先存用户ID
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 5. 将认证对象存入SecurityContext（Spring Security会认为已认证）
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6. 继续执行过滤器链
        chain.doFilter(request, response);
    }
}
