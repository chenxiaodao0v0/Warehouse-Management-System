package com.xmut.warehouse.module.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private String name; // 仓库名称
    private String address; // 仓库地址
    private String contact; // 联系人
    private String phone; // 联系电话
    private Integer status; // 仓库状态（1-启用，2-禁用）
    private String remark; // 仓库备注
    @TableField("create_time")
    private Date createTime; // 创建时间
    @TableField("update_time")
    private Date updateTime; // 更新时间
}
