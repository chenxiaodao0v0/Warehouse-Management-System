package com.xmut.warehouse.module.inOutRecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 出入库及调货记录实体类（精准适配你的xmut_in_out_record表）
 */
@Data
@TableName("xmut_in_out_record") // 对应你的表名
public class InOutRecord {
    /**
     * 主键ID，自定义生成策略
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 关联货品ID
     */
    @TableField("goods_id")
    private String goodsId;

    /**
     * 关联操作仓库ID
     */
    @TableField("warehouse_id")
    private String warehouseId;

    /**
     * 操作类型：1-入库，2-出库，3-调货
     */
    private Integer type;

    /**
     * 调货目标仓库ID（调货类型必填，其他类型可为null）
     */
    @TableField("related_warehouse_id")
    private String relatedWarehouseId;

    /**
     * 操作数量（正数）
     */
    private Integer quantity;

    /**
     * 对接人
     */
    @TableField("contact_person")
    private String contactPerson;

    /**
     * 对接人电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 操作备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 操作时间（默认当前时间）
     */
    @TableField("operate_time")
    private Date operateTime;

    /**
     * 操作人ID（关联用户表）
     */
    @TableField("operator_id")
    private String operatorId;
}