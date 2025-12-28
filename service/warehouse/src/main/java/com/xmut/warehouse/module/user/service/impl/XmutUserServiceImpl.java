package com.xmut.warehouse.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Override
    public R<?> listUsers(int pageNum, int pageSize, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<XmutUser> queryWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> 
                wrapper.like(XmutUser::getUsername, keyword)
                    .or()
                    .like(XmutUser::getNickname, keyword)
            );
        }
        queryWrapper.orderByDesc(XmutUser::getCreateTime); // 按创建时间倒序
        
        // 分页查询
        Page<XmutUser> page = new Page<>(pageNum, pageSize);
        IPage<XmutUser> resultPage = this.page(page, queryWrapper);
        
        // 添加调试日志
        System.out.println("查询到的用户列表数量: " + resultPage.getRecords().size());
        if (!resultPage.getRecords().isEmpty()) {
            XmutUser firstUser = resultPage.getRecords().get(0);
            System.out.println("第一个用户的手机号: " + firstUser.getPhone());
            System.out.println("第一个用户的邮箱: " + firstUser.getEmail());
        }
        
        return R.success(resultPage);
    }

    @Override
    public R<?> addUser(XmutUser user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<XmutUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XmutUser::getUsername, user.getUsername());
        int count = (int)this.count(queryWrapper); // 修复：强制转换long为int
        if (count > 0) {
            return R.fail("用户名已存在");
        }
        
        // 设置默认值
        user.setId(null);
        user.setPassword(PasswordUtil.encrypt("123456")); // 修复：使用正确的方法名
        user.setCreateTime(new Date());
        user.setStatus(1); // 默认启用
        
        boolean result = this.save(user);
        if (result) {
            return R.success("用户添加成功");
        } else {
            return R.fail("用户添加失败");
        }
    }

    @Override
    public R<?> updateUser(XmutUser user) {
        // 检查用户名是否被其他用户使用
        LambdaQueryWrapper<XmutUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XmutUser::getUsername, user.getUsername());
        queryWrapper.ne(XmutUser::getId, user.getId()); // 排除当前用户
        int count = (int)this.count(queryWrapper); // 修复：强制转换long为int
        if (count > 0) {
            return R.fail("用户名已存在");
        }
        
        boolean result = this.updateById(user);
        if (result) {
            return R.success("用户更新成功");
        } else {
            return R.fail("用户更新失败");
        }
    }

    @Override
    public R<?> deleteUser(String id) {
        // 检查是否为当前登录用户，避免删除自己
        // 这里需要获取当前登录用户ID，可以通过JWT token获取
        // 简化处理：先直接删除
        
        boolean result = this.removeById(id);
        if (result) {
            return R.success("用户删除成功");
        } else {
            return R.fail("用户删除失败");
        }
    }

    @Override
    public R<?> resetPassword(String id) {
        XmutUser user = this.getById(id);
        if (user == null) {
            return R.fail("用户不存在");
        }
        
        // 重置密码为默认密码
        user.setPassword(PasswordUtil.encrypt("123456")); // 修复：使用正确的方法名
        boolean result = this.updateById(user);
        if (result) {
            return R.success("密码重置成功");
        } else {
            return R.fail("密码重置失败");
        }
    }

    @Override
    public R<?> changePassword(String id, String oldPassword, String newPassword) {
        XmutUser user = this.getById(id);
        if (user == null) {
            return R.fail("用户不存在");
        }
        
        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            return R.fail("原密码错误");
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encrypt(newPassword)); // 修复：使用正确的方法名
        boolean result = this.updateById(user);
        if (result) {
            return R.success("密码修改成功");
        } else {
            return R.fail("密码修改失败");
        }
    }

    @Override
    public R<?> toggleUserStatus(String id, Integer status) {
        XmutUser user = this.getById(id);
        if (user == null) {
            return R.fail("用户不存在");
        }
        
        user.setStatus(status);
        boolean result = this.updateById(user);
        if (result) {
            String statusText = status == 1 ? "启用" : "禁用";
            return R.success("用户" + statusText + "成功");
        } else {
            return R.fail("用户状态更新失败");
        }
    }
    
    /**
     * 初始化超级管理员账号
     */
    public void initSuperAdmin() {
        LambdaQueryWrapper<XmutUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XmutUser::getRole, RoleConstant.SUPER_ADMIN);
        XmutUser superAdmin = this.getOne(queryWrapper);
        
        // 如果没有超级管理员账号，则创建一个
        if (superAdmin == null) {
            XmutUser admin = new XmutUser();
            admin.setUsername("admin");
            admin.setNickname("超级管理员");
            admin.setPassword(PasswordUtil.encrypt("admin123")); // 修复：使用正确的方法名
            admin.setRole(RoleConstant.SUPER_ADMIN);
            admin.setStatus(1);
            admin.setCreateTime(new Date());
            this.save(admin);
            System.out.println("已创建超级管理员账号：admin / admin123");
        } else {
            System.out.println("超级管理员账号已存在");
        }
    }
}