package com.xmut.warehouse.module.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 商品基础信息实体类（无仓库、库存字段，职责单一）
 */
@Data
@TableName("xmut_goods")
public class XmutGoods {
    // 主键自动生成UUID，唯一标识商品
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    // 商品基础信息字段（无冗余）
    private String name; // 商品名称
    private Double price; // 商品价格
    @TableField("category_id")
    private String categoryId; // 商品类别ID
    private String pic; // 商品图片地址
    private Integer status; // 上下架状态（1-上架，2-下架）
    private String remark; // 商品备注
    @TableField("create_time")
    private Date createTime; // 创建时间
}
