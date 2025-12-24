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
                .csrf().disable() // 关闭CSRF（前后端分离必关）
                .authorizeRequests()
                // 确保登录接口路径和实际一致，精准放行
                .antMatchers("/api/user/login").permitAll()
                // 若有注册、验证码等接口，也可在此添加放行
                // .antMatchers("/api/user/register").permitAll()
                // 所有非白名单接口需要认证
                .anyRequest().authenticated()
                .and()
                // 关闭默认表单登录和HTTP基础认证（前后端分离无需）
                .formLogin().disable()
                .httpBasic().disable()
                // JWT过滤器放在UsernamePasswordAuthenticationFilter之前（原有逻辑不变）
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
