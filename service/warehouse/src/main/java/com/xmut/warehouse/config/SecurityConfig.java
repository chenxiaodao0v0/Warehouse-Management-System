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
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter; // 注入JWT过滤器

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 关闭CSRF
                .authorizeRequests()
                // 放行登录接口
                .antMatchers("/api/user/login").permitAll()
                // 其他接口需要认证
                .anyRequest().authenticated()
                .and()
                // 关闭默认表单登录
                .formLogin().disable()
                .httpBasic().disable()
                // 添加JWT过滤器：在UsernamePasswordAuthenticationFilter之前执行
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
