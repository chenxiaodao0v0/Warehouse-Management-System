package com.xmut.warehouse.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.xmut.warehouse.common.generator.IdGenerator;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
public class MybatisPlusConfig {

    private static final Logger logger = LoggerFactory.getLogger(MybatisPlusConfig.class);

    /**
     * 注册自定义ID生成器
     */
    @Bean
    public IdentifierGenerator identifierGenerator() {
        return new IdGenerator();
    }

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
     * 配置MyBatis-Plus的MetaObjectHandler，用于自动填充ID
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new IdGeneratorMetaObjectHandler();
    }
}

/**
 * ID生成器MetaObjectHandler
 */
class IdGeneratorMetaObjectHandler implements MetaObjectHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        // 检查ID是否已经存在，如果存在则不填充
        Object idValue = this.getFieldValByName("id", metaObject);
        if (idValue != null && !"".equals(idValue)) {
            logger.debug("ID already exists: {}", idValue);
            return;
        }
        
        // 在插入时设置自定义ID
        Object originalObject = metaObject.getOriginalObject();
        logger.debug("Generating ID for entity: {}", originalObject.getClass().getSimpleName());
        
        try {
            // 直接创建ID生成器来生成ID
            IdGenerator idGenerator = new IdGenerator();
            String customId = idGenerator.nextIdByEntity(originalObject);
            
            logger.debug("Generated ID: {}", customId);
            
            if (customId != null) {
                this.strictInsertFill(metaObject, "id", String.class, customId);
            } else {
                logger.error("Failed to generate ID for entity: {}", originalObject.getClass().getSimpleName());
            }
        } catch (Exception e) {
            logger.error("Error generating ID for entity: {}", originalObject.getClass().getSimpleName(), e);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时不需要填充ID
    }
}