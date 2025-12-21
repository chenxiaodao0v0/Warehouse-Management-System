package com.xmut.warehouse.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.module.user.entity.XmutUser;
import com.xmut.warehouse.common.result.R;

/**
 * 用户业务逻辑接口，继承MyBatis-Plus IService，自带分页等高级方法
 */
public interface XmutUserService extends IService<XmutUser> {
    // 自定义业务方法：用户登录（账号+密码校验，返回Token和用户信息）
    R<?> login(String username, String password);
}
