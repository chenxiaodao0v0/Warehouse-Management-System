package com.xmut.warehouse.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.entity.UserMenuPermission;

import java.util.List;

/**
 * 用户-菜单权限关联表Service接口
 */
public interface UserMenuPermissionService extends IService<UserMenuPermission> {
    /**
     * 获取用户的所有菜单权限
     */
    R<List<String>> getUserMenuPermissions(String userId);

    /**
     * 设置用户菜单权限
     */
    R<?> setUserMenuPermissions(String userId, List<String> menuCodes);

    /**
     * 根据用户ID删除菜单权限
     */
    R<?> deleteUserMenuPermissions(String userId);
}