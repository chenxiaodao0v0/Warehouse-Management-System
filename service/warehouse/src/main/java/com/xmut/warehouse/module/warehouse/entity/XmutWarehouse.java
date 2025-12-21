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
     * 仓库主键ID（对应数据库id字段）
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 关联企业ID（对应数据库enterprise_id字段，驼峰自动匹配，注解可省略，保留更清晰）
     */
    @TableField("enterprise_id")
    private String enterpriseId;

    /**
     * 仓库名称（对应数据库name字段，核心绑定：实体类warehouseName ↔ 数据库name）
     */
    @TableField("name")
    private String warehouseName;

    /**
     * 仓库地址（对应数据库address字段，无需注解）
     */
    private String address;

    /**
     * 仓库负责人（对应数据库manager字段，核心绑定：实体类contact ↔ 数据库manager）
     */
    @TableField("manager")
    private String contact;

    /**
     * 负责人电话（对应数据库phone字段，无需注解）
     */
    private String phone;

    /**
     * 仓库状态（对应数据库status字段，无需注解）
     */
    private Integer status;

    /**
     * 备注信息（你数据库表中无该字段，若需要可添加，或删除该行）
     */
    @TableField(exist = false) // 表示该属性在数据库表中不存在，仅用于实体类传输
    private String remark;

    /**
     * 创建时间（对应数据库create_time字段，驼峰自动匹配）
     */
    private Date createTime;


}
