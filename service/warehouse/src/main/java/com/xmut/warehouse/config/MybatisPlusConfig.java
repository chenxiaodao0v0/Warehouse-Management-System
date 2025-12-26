package com.xmut.warehouse.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
public class MybatisPlusConfig {

    private static final Logger logger = LoggerFactory.getLogger(MybatisPlusConfig.class);

    /**
     * 配置MyBatis-Plus的分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 配置MyBatis-Plus的MetaObjectHandler，用于自动填充创建时间
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new CommonMetaObjectHandler();
    }
}

/**
 * 通用MetaObjectHandler，只处理创建时间和更新时间
 */
class CommonMetaObjectHandler implements MetaObjectHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        // 只填充时间字段，ID由服务层手动设置
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}