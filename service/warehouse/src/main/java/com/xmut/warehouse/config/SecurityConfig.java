package com.xmut.warehouse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors() // 启用CORS支持
                .and()
                .csrf().disable() // 关闭CSRF（前后端分离必关）
                .authorizeRequests()
                // 优先放行静态资源和上传的文件（放在最前面，确保优先匹配）
                .antMatchers("/upload/**", "/static/**", "/error", "/favicon.ico").permitAll()
                // 放行登录接口
                .antMatchers("/api/user/login").permitAll()
                // 预检请求必须放行，否则CORS无法正常工作
                .antMatchers("OPTIONS/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
                .and()
                // 关闭默认表单登录和HTTP基础认证（前后端分离无需）
                .formLogin().disable()
                .httpBasic().disable()
                // JWT过滤器放在UsernamePasswordAuthenticationFilter之前
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}