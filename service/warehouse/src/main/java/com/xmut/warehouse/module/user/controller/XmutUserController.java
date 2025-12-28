package com.xmut.warehouse.module.user.controller;

import com.xmut.warehouse.common.constant.RoleConstant;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.entity.XmutUser;
import com.xmut.warehouse.module.user.service.XmutUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
     * @param loginData 登录参数（包含username和password）
     * @return 登录结果（Token + 用户信息）
     */
    @PostMapping("/login")
    public R<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        // 调用Service层登录方法
        return userService.login(username, password);
    }

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @return 用户分页列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> listUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return userService.listUsers(pageNum, pageSize, keyword);
    }

    /**
     * 添加用户
     * @param user 用户信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> addUser(@RequestBody XmutUser user) {
        return userService.addUser(user);
    }

    /**
     * 更新用户
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> updateUser(@RequestBody XmutUser user) {
        return userService.updateUser(user);
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }

    /**
     * 重置用户密码
     * @param id 用户ID
     * @return 重置结果
     */
    @PutMapping("/resetPassword/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> resetPassword(@PathVariable String id) {
        return userService.resetPassword(id);
    }

    /**
     * 修改用户密码
     * @param id 用户ID
     * @param changePasswordData 包含旧密码和新密码的数据
     * @return 修改结果
     */
    @PutMapping("/changePassword/{id}")
    public R<?> changePassword(@PathVariable String id, @RequestBody Map<String, String> changePasswordData) {
        String oldPassword = changePasswordData.get("oldPassword");
        String newPassword = changePasswordData.get("newPassword");
        return userService.changePassword(id, oldPassword, newPassword);
    }

    /**
     * 启用/禁用用户
     * @param id 用户ID
     * @param status 状态（1-启用，0-禁用）
     * @return 更新结果
     */
    @PutMapping("/toggleStatus/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> toggleUserStatus(@PathVariable String id, @RequestParam Integer status) {
        return userService.toggleUserStatus(id, status);
    }
}