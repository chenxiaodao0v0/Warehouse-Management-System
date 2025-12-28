package com.xmut.warehouse;

import com.xmut.warehouse.module.user.service.XmutUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WarehouseApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WarehouseApplication.class, args);
        
        // 初始化超级管理员账号
        XmutUserService userService = context.getBean(XmutUserService.class);
        ((com.xmut.warehouse.module.user.service.impl.XmutUserServiceImpl) userService).initSuperAdmin();
    }
}