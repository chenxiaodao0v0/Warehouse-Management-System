package com.xmut.warehouse.module.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品（货品）实体类（已与你的xmut_goods表字段精准绑定，解决不匹配问题）
 */
@Data
@TableName("xmut_goods") // 对应你的数据库表名，无需修改
public class XmutGoods {
    /**
     * 商品主键ID（对应数据库id字段，UUID格式）
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 货品名称（核心绑定：实体goodsName ↔ 数据库name）
     */
    @TableField("name")
    private String goodsName;

    /**
     * 货品类别ID（对应数据库category_id，驼峰自动匹配，注解可省略，保留更清晰）
     */
    @TableField("category_id")
    private String categoryId;

    /**
     * 仓库ID（对应数据库warehouse_id，驼峰自动匹配，注解可省略，保留更清晰）
     */
    @TableField("warehouse_id")
    private String warehouseId;

    /**
     * 商品单价（对应数据库price字段，完全匹配）
     */
    private BigDecimal price;

    /**
     * 商品库存（对应数据库stock字段，完全匹配）
     */
    private Integer stock;

    /**
     * 商品图片路径（核心绑定：实体goodsImg ↔ 数据库pic）
     */
    @TableField("pic")
    private String goodsImg;

    /**
     * 商品状态（对应数据库status字段，完全匹配：0-下架，1-上架）
     */
    private Integer status;

    /**
     * 货品备注（对应数据库remark字段，完全匹配）
     */
    private String remark;

    /**
     * 创建时间（对应数据库create_time字段，驼峰自动匹配）
     */
    private Date createTime;

    /**
     * 商品规格（实体独有，数据库表无对应字段，标记为非数据库字段）
     */
    @TableField(exist = false)
    private String spec;

    /**
     * 计量单位（实体独有，数据库表无对应字段，标记为非数据库字段）
     */
    @TableField(exist = false)
    private String unit;

}
