package com.xmut.warehouse.module.user.controller;

import com.xmut.warehouse.common.constant.RoleConstant;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.entity.XmutUser;
import com.xmut.warehouse.module.user.service.XmutUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
     * 获取当前用户信息
     * @return 用户信息
     */
    @GetMapping("/info")
    public R<?> getUserInfo(Authentication authentication) {
        // 从认证信息中获取用户ID
        String userId = (String) authentication.getPrincipal();
        System.out.println("获取用户信息 - 当前用户ID: " + userId); // 添加调试信息
        if (userId == null) {
            System.out.println("获取用户信息失败 - 用户未认证"); // 添加调试信息
            return R.fail("用户未认证");
        }
        
        XmutUser user = this.userService.getById(userId);
        System.out.println("获取用户信息 - 查询到的用户: " + user); // 添加调试信息
        if (user == null) {
            System.out.println("获取用户信息失败 - 用户不存在"); // 添加调试信息
            return R.fail("用户不存在");
        }
        
        // 返回用户信息（不包含密码）
        user.setPassword(null); // 不返回密码信息
        System.out.println("获取用户信息成功 - 用户数据: " + user); // 添加调试信息
        return R.success(user);
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
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public R<?> updateUserInfo(@RequestBody XmutUser user, Authentication authentication) {
        // 获取当前用户ID
        String currentUserId = (String) authentication.getPrincipal();
        if (currentUserId == null) {
            return R.fail("用户未认证");
        }
        
        // 确保只能更新自己的信息
        if (!currentUserId.equals(user.getId())) {
            return R.fail("不能更新其他用户的信息");
        }
        
        // 检查用户是否存在
        XmutUser existingUser = this.userService.getById(user.getId());
        if (existingUser == null) {
            return R.fail("用户不存在");
        }
        
        // 设置不允许修改的字段
        user.setPassword(existingUser.getPassword()); // 保持原密码不变
        user.setRole(existingUser.getRole()); // 保持原角色不变
        user.setStatus(existingUser.getStatus()); // 保持原状态不变
        user.setUsername(existingUser.getUsername()); // 保持用户名不变
        
        boolean result = this.userService.updateById(user);
        if (result) {
            return R.success("用户信息更新成功");
        } else {
            return R.fail("用户信息更新失败");
        }
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