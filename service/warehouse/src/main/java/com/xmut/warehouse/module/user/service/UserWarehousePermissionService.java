package com.xmut.warehouse.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.entity.UserWarehousePermission;

import java.util.List;

/**
 * 用户-仓库权限关联表Service接口
 */
public interface UserWarehousePermissionService extends IService<UserWarehousePermission> {
    /**
     * 获取用户的所有仓库权限
     */
    R<List<String>> getUserWarehousePermissions(String userId);

    /**
     * 设置用户仓库权限
     */
    R<?> setUserWarehousePermissions(String userId, List<String> warehouseIds);

    /**
     * 根据用户ID删除仓库权限
     */
    R<?> deleteUserWarehousePermissions(String userId);
}