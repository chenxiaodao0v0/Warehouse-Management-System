package com.xmut.warehouse.config;

import com.xmut.warehouse.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // 1. 定义白名单（登录接口等无需Token校验的接口）
    private final List<String> whiteList = Arrays.asList("/api/user/login");
    // 添加静态资源路径到白名单
    private final List<String> staticResourceList = Arrays.asList("/upload/", "/static/", "/error", "/favicon.ico");
    // 路径匹配器（支持ant风格路径，和Security的antMatchers一致）
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 2. 获取当前请求URI
        String requestURI = request.getRequestURI();

        // 3. 判断当前请求是否在白名单中，若是则直接放行（跳过Token校验）
        for (String whitePath : whiteList) {
            if (pathMatcher.match(whitePath, requestURI)) {
                // 白名单接口，直接执行后续过滤器链（放行）
                chain.doFilter(request, response);
                return; // 终止当前过滤器逻辑，避免后续Token校验
            }
        }

        // 4. 判断当前请求是否为静态资源，若是则直接放行
        for (String staticPath : staticResourceList) {
            if (requestURI.startsWith(staticPath)) {
                // 静态资源，直接执行后续过滤器链（放行）
                chain.doFilter(request, response);
                return; // 终止当前过滤器逻辑，避免后续Token校验
            }
        }

        // ========== 原有Token校验逻辑（不变，仅对非白名单接口生效） ==========
        // 1. 从请求头获取Authorization（Token）
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 无Token/格式错误，直接返回401未认证，不放行
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write("{\"code\":-1,\"msg\":\"请携带有效JWT Token进行认证\",\"data\":null}");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // 终止过滤器链，不执行后续请求
        }

        // 2. 提取Token（去掉"Bearer "前缀）
        String token = authHeader.substring(7);

        // 3. 解析Token，获取用户信息
        Claims claims = jwtUtil.getClaimsByToken(token);
        if (claims == null || jwtUtil.isTokenExpired(claims)) {
            // Token无效/过期，返回401，不放行
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write("{\"code\":-1,\"msg\":\"Token无效或已过期，请重新登录\",\"data\":null}");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // 终止过滤器链
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

        // 6. Token有效，继续执行过滤器链（放行请求）
        chain.doFilter(request, response);
    }
}