package com.xmut.warehouse.module.user.controller;

import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.service.XmutUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户接口层，提供登录、用户管理等接口
 */
@RestController
@RequestMapping("/api/user") // 接口统一前缀，方便前端代理转发
public class XmutUserController {

    @Autowired
    private XmutUserService userService;

    /**
     * 用户登录接口
     * @param username 登录账号
     * @param password 登录密码
     * @return 登录结果（Token + 用户信息）
     */
    @PostMapping("/login")
    public R<?> login(@RequestParam String username, @RequestParam String password) {
        // 调用Service层登录方法
        return userService.login(username, password);
    }

    // 后续可添加用户列表、新增/修改/删除用户接口（先实现登录，再扩展）
}
