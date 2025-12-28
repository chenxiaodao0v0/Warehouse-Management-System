package com.xmut.warehouse.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.entity.UserWarehousePermission;
import com.xmut.warehouse.module.user.mapper.UserWarehousePermissionMapper;
import com.xmut.warehouse.module.user.service.UserWarehousePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户-仓库权限关联表Service实现类
 */
@Service
public class UserWarehousePermissionServiceImpl extends ServiceImpl<UserWarehousePermissionMapper, UserWarehousePermission> implements UserWarehousePermissionService {

    @Autowired
    private UserWarehousePermissionMapper userWarehousePermissionMapper;

    @Override
    public R<List<String>> getUserWarehousePermissions(String userId) {
        LambdaQueryWrapper<UserWarehousePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWarehousePermission::getUserId, userId);
        List<UserWarehousePermission> permissions = this.list(queryWrapper);
        
        List<String> warehouseIds = permissions.stream()
                .map(UserWarehousePermission::getWarehouseId)
                .collect(Collectors.toList());
        
        return R.success(warehouseIds);
    }

    @Override
    @Transactional
    public R<?> setUserWarehousePermissions(String userId, List<String> warehouseIds) {
        // 先删除用户现有的所有仓库权限
        this.deleteUserWarehousePermissions(userId);
        
        // 批量插入新的仓库权限
        if (warehouseIds != null && !warehouseIds.isEmpty()) {
            for (String warehouseId : warehouseIds) {
                UserWarehousePermission permission = new UserWarehousePermission();
                permission.setUserId(userId);
                permission.setWarehouseId(warehouseId);
                this.save(permission);
            }
        }
        
        return R.success("用户仓库权限设置成功");
    }

    @Override
    @Transactional
    public R<?> deleteUserWarehousePermissions(String userId) {
        LambdaQueryWrapper<UserWarehousePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWarehousePermission::getUserId, userId);
        this.remove(queryWrapper);
        return R.success("用户仓库权限删除成功");
    }
}