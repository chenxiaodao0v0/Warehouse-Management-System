package com.xmut.warehouse.module.inOutRecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 出入库记录实体类
 */
@Data
@TableName("xmut_in_out_record")
public class InOutRecord {
    // 主键需要手动输入
    @TableId(type = IdType.INPUT)  // 使用手动输入ID生成策略
    private String id;

    // 出入库记录基础信息字段
    @TableField("goods_id")
    private String goodsId; // 关联货品ID
    
    @TableField("warehouse_id")
    private String warehouseId; // 关联操作仓库ID
    
    private Integer type; // 操作类型（1-入库，2-出库，3-调货）
    
    @TableField("related_warehouse_id")
    private String relatedWarehouseId; // 调货目标仓库ID
    
    private Integer quantity; // 操作数量（正数）
    
    @TableField("contact_person")
    private String contactPerson; // 对接人
    
    @TableField("contact_phone")
    private String contactPhone; // 对接人电话
    
    private String remark; // 操作备注
    
    @TableField("operate_time")
    private Date operateTime; // 操作时间
    
    @TableField("operator_id")
    private String operatorId; // 操作人ID（关联用户表）
    
    @TableField("create_time")
    private Date createTime; // 创建时间
    @TableField("update_time")
    private Date updateTime; // 更新时间
}