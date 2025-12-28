package com.xmut.warehouse.module.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户-菜单权限关联表实体类
 */
@Data
@TableName("xmut_user_menu_permission")
public class UserMenuPermission {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("user_id")
    private String userId; // 用户ID

    @TableField("menu_code")
    private String menuCode; // 菜单编码，如 'user_management', 'warehouse_management' 等
}