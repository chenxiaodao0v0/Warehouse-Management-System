package com.xmut.warehouse.module.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 仓库实体类
 */
@Data
@TableName("xmut_warehouse")
public class XmutWarehouse {
    // 主键需要手动输入
    @TableId(type = IdType.INPUT)  // 使用手动输入ID生成策略
    private String id;

    // 仓库基础信息字段
    @JsonProperty("warehouseName") // 前端使用warehouseName，后端映射为name
    private String name; // 仓库名称
    private String address; // 仓库地址
    @TableField("manager")  // 数据库中实际字段名为manager
    private String contact; // 联系人（负责人）
    private String phone; // 联系电话
    private Integer status; // 仓库状态（0-禁用，1-启用）
    @TableField("enterprise_id")  // 企业ID
    private String enterpriseId; // 关联企业ID
    @TableField("create_time")
    private Date createTime; // 创建时间
    // 注意：xmut_warehouse表中没有update_time字段，所以不包含该属性
}