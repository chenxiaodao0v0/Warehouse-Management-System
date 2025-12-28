package com.xmut.warehouse.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.entity.UserMenuPermission;
import com.xmut.warehouse.module.user.mapper.UserMenuPermissionMapper;
import com.xmut.warehouse.module.user.service.UserMenuPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户-菜单权限关联表Service实现类
 */
@Service
public class UserMenuPermissionServiceImpl extends ServiceImpl<UserMenuPermissionMapper, UserMenuPermission> implements UserMenuPermissionService {

    @Autowired
    private UserMenuPermissionMapper userMenuPermissionMapper;

    @Override
    public R<List<String>> getUserMenuPermissions(String userId) {
        LambdaQueryWrapper<UserMenuPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserMenuPermission::getUserId, userId);
        List<UserMenuPermission> permissions = this.list(queryWrapper);
        
        List<String> menuCodes = permissions.stream()
                .map(UserMenuPermission::getMenuCode)
                .collect(Collectors.toList());
        
        return R.success(menuCodes);
    }

    @Override
    @Transactional
    public R<?> setUserMenuPermissions(String userId, List<String> menuCodes) {
        // 先删除用户现有的所有菜单权限
        this.deleteUserMenuPermissions(userId);
        
        // 批量插入新的菜单权限
        if (menuCodes != null && !menuCodes.isEmpty()) {
            for (String menuCode : menuCodes) {
                UserMenuPermission permission = new UserMenuPermission();
                permission.setUserId(userId);
                permission.setMenuCode(menuCode);
                this.save(permission);
            }
        }
        
        return R.success("用户菜单权限设置成功");
    }

    @Override
    @Transactional
    public R<?> deleteUserMenuPermissions(String userId) {
        LambdaQueryWrapper<UserMenuPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserMenuPermission::getUserId, userId);
        this.remove(queryWrapper);
        return R.success("用户菜单权限删除成功");
    }
}