package com.xmut.warehouse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 仅对API路径应用CORS
                .allowedOriginPatterns("*") // 允许所有来源
                .allowedMethods("*") // 允许所有HTTP方法，包括OPTIONS
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带认证信息
                .maxAge(3600); // 预检请求有效期
    }
}