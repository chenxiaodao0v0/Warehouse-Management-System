package com.xmut.warehouse.module.user.controller;

import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.service.UserMenuPermissionService;
import com.xmut.warehouse.module.user.service.UserWarehousePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理控制器，处理菜单权限和仓库权限
 */
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private UserMenuPermissionService userMenuPermissionService;

    @Autowired
    private UserWarehousePermissionService userWarehousePermissionService;

    /**
     * 获取用户的菜单权限
     */
    @GetMapping("/menu/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or #userId == authentication.name")
    public R<?> getUserMenuPermissions(@PathVariable String userId) {
        return userMenuPermissionService.getUserMenuPermissions(userId);
    }

    /**
     * 设置用户的菜单权限
     */
    @PostMapping("/menu")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> setUserMenuPermissions(@RequestBody Map<String, Object> requestData) {
        String userId = (String) requestData.get("userId");
        @SuppressWarnings("unchecked")
        List<String> menuCodes = (List<String>) requestData.get("menuCodes");
        return userMenuPermissionService.setUserMenuPermissions(userId, menuCodes);
    }

    /**
     * 获取用户的仓库权限
     */
    @GetMapping("/warehouse/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or #userId == authentication.name")
    public R<?> getUserWarehousePermissions(@PathVariable String userId) {
        return userWarehousePermissionService.getUserWarehousePermissions(userId);
    }

    /**
     * 设置用户的仓库权限
     */
    @PostMapping("/warehouse")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> setUserWarehousePermissions(@RequestBody Map<String, Object> requestData) {
        String userId = (String) requestData.get("userId");
        @SuppressWarnings("unchecked")
        List<String> warehouseIds = (List<String>) requestData.get("warehouseIds");
        return userWarehousePermissionService.setUserWarehousePermissions(userId, warehouseIds);
    }

    /**
     * 获取所有权限信息（菜单权限和仓库权限）
     */
    @GetMapping("/all/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or #userId == authentication.name")
    public R<?> getAllPermissions(@PathVariable String userId) {
        // 获取菜单权限
        R<List<String>> menuPermissions = userMenuPermissionService.getUserMenuPermissions(userId);
        // 获取仓库权限
        R<List<String>> warehousePermissions = userWarehousePermissionService.getUserWarehousePermissions(userId);

        // 合并结果
        if (menuPermissions.getCode() == 200 && warehousePermissions.getCode() == 200) {
            Map<String, Object> result = new HashMap<>();
            result.put("menuPermissions", menuPermissions.getData());
            result.put("warehousePermissions", warehousePermissions.getData());
            return R.success(result);
        } else {
            return R.fail("获取权限信息失败");
        }
    }

    /**
     * 批量设置用户权限（菜单权限和仓库权限）
     */
    @PostMapping("/batch")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public R<?> setAllPermissions(@RequestBody Map<String, Object> requestData) {
        String userId = (String) requestData.get("userId");
        @SuppressWarnings("unchecked")
        List<String> menuCodes = (List<String>) requestData.get("menuCodes");
        @SuppressWarnings("unchecked")
        List<String> warehouseIds = (List<String>) requestData.get("warehouseIds");

        // 设置菜单权限
        R<?> menuResult = userMenuPermissionService.setUserMenuPermissions(userId, menuCodes);
        if (menuResult.getCode() != 200) {
            return menuResult;
        }

        // 设置仓库权限
        R<?> warehouseResult = userWarehousePermissionService.setUserWarehousePermissions(userId, warehouseIds);
        if (warehouseResult.getCode() != 200) {
            return warehouseResult;
        }

        return R.success("权限设置成功");
    }
}