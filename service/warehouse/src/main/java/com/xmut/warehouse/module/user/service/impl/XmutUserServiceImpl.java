package com.xmut.warehouse.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.constant.RoleConstant;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.common.util.JwtUtil;
import com.xmut.warehouse.common.util.PasswordUtil;
import com.xmut.warehouse.module.user.entity.XmutUser;
import com.xmut.warehouse.module.user.mapper.XmutUserMapper;
import com.xmut.warehouse.module.user.service.XmutUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户业务逻辑实现类
 */
@Service
public class XmutUserServiceImpl extends ServiceImpl<XmutUserMapper, XmutUser> implements XmutUserService {

    @Autowired
    private JwtUtil jwtUtil; // 注入JWT工具类

    @Override
    public R<?> login(String username, String password) {
        // 1. 按账号查询用户（使用LambdaQueryWrapper，避免手写SQL）
        LambdaQueryWrapper<XmutUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XmutUser::getUsername, username);
        XmutUser user = this.baseMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (user == null) {
            return R.fail("账号不存在");
        }

        // 3. 校验用户是否启用
        if (user.getStatus() == 0) {
            return R.fail("账号已禁用，请联系管理员");
        }

        // 4. 校验密码（明文密码 vs 数据库加密密码）
        if (!PasswordUtil.matches(password, user.getPassword())) {
            return R.fail("密码错误");
        }

        // 5. 生成JWT Token（携带用户ID和角色）
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        // 6. 更新用户最后登录时间
        user.setLastLoginTime(new Date());
        this.baseMapper.updateById(user);

        // 7. 封装返回数据（Token + 用户基本信息，不返回密码）
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userId", user.getId());
        resultMap.put("nickname", user.getNickname());
        resultMap.put("role", user.getRole());

        return R.success(resultMap);
    }
}
