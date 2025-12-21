package com.xmut.warehouse.module.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 仓库实体类（与你的企业实体、用户实体格式一致，可直接复用）
 * 对应数据库表：xmut_warehouse（需手动在MySQL创建该表，字段与实体一致即可）
 */
@Data
@TableName("xmut_warehouse") // 与你的数据库表名对应，无需修改（若表名不同，仅修改此处）
public class XmutWarehouse {
    /**
     * 仓库主键ID（与用户/企业模块一致，使用UUID）
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 关联企业ID（关联xmut_enterprise表的主键ID，必填）
     */
    private String enterpriseId;

    /**
     * 仓库名称（必填，唯一）
     */
    private String warehouseName;

    /**
     * 仓库地址
     */
    private String address;

    /**
     * 仓库联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 仓库状态（0-禁用，1-启用，默认1，与用户状态格式一致）
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间（与用户/企业模块一致）
     */
    private Date createTime;

    /**
     * 更新时间（与用户/企业模块一致）
     */
    private Date updateTime;
}
