package com.xmut.warehouse.module.inOutRecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 出入库及调货记录实体类（精准适配你的xmut_in_out_record表，补充完整@TableField注解+字段校验注解）
 */
@Data
@TableName("xmut_in_out_record") // 对应你的表名
public class InOutRecord {
    /**
     * UUID主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 关联货品ID
     */
    @NotBlank(message = "商品ID不能为空") // 字段校验：非空
    @TableField("goods_id")
    private String goodsId;

    /**
     * 关联操作仓库ID
     */
    @NotBlank(message = "仓库ID不能为空") // 字段校验：非空
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
    @NotNull(message = "操作数量不能为空") // 字段校验：非null
    @Min(value = 1, message = "操作数量必须为正整数") // 字段校验：最小值1
    private Integer quantity;

    /**
     * 对接人
     */
    @NotBlank(message = "联系人不能为空") // 字段校验：非空
    @TableField("contact_person")
    private String contactPerson;

    /**
     * 对接人电话
     */
    @NotBlank(message = "联系电话不能为空") // 字段校验：非空
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误，请输入11位有效手机号") // 字段校验：手机号格式
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
    @NotBlank(message = "操作人ID不能为空") // 字段校验：非空
    @TableField("operator_id")
    private String operatorId;
}
