package com.xmut.warehouse.module.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 仓库实体类（已与你的数据库表字段精准绑定）
 */
@Data
@TableName("xmut_warehouse") // 对应你的数据库表名，无需修改
public class XmutWarehouse {
    /**
     * 主键ID，自定义生成策略
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 关联企业ID（对应数据库enterprise_id字段，驼峰自动匹配，注解可省略，保留更清晰）
     */
    @TableField("enterprise_id")
    private String enterpriseId;

    /**
     * 仓库名称
     */
    @TableField("name")
    private String name;

    /**
     * 仓库地址
     */
    @TableField("address")
    private String address;

    /**
     * 仓库负责人
     */
    @TableField("manager")
    private String manager;

    /**
     * 负责人电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 仓库状态（对应数据库status字段，无需注解）
     */
    private Integer status;

    /**
     * 创建时间（对应数据库create_time字段，驼峰自动匹配）
     */
    private Date createTime;

}