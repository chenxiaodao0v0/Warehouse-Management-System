package com.xmut.warehouse.module.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商品实体类（精准适配你的xmut_goods表）
 */
@Data
@TableName("xmut_goods") // 对应你的表名
public class XmutGoods {
    /**
     * 主键ID，自定义生成策略
     */
    @TableId(type = IdType.INPUT) // 使用手动输入ID生成策略
    private String id;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 商品分类ID
     */
    @TableField("category_id")
    private String categoryId;

    /**
     * 价格（分）
     */
    @TableField("price")
    private Integer price;

    /**
     * 图片路径
     */
    @TableField("pic")
    private String pic;

    /**
     * 商品状态：1-上架，2-下架
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private java.util.Date createTime;

}