package com.xmut.warehouse.module.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户-仓库权限关联表实体类
 */
@Data
@TableName("xmut_user_warehouse_permission")
public class UserWarehousePermission {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("user_id")
    private String userId; // 用户ID

    @TableField("warehouse_id")
    private String warehouseId; // 仓库ID
}