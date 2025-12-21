package com.xmut.warehouse.module.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品（货品）实体类
 * 对应数据库表：xmut_goods（需手动创建，字段与实体一致）
 */
@Data
@TableName("xmut_goods")
public class XmutGoods {
    /**
     * 商品主键ID（UUID，与企业、仓库模块一致）
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 商品名称（必填，组合唯一：类别ID+仓库ID+商品名称）
     */
    private String goodsName;

    /**
     * 货品类别ID（必填，关联xmut_goods_category表主键）
     */
    private String categoryId;

    /**
     * 仓库ID（必填，关联xmut_warehouse表主键）
     */
    private String warehouseId;

    /**
     * 商品规格（如：500g/袋、100ml/瓶）
     */
    private String spec;

    /**
     * 计量单位（如：袋、瓶、个）
     */
    private String unit;

    /**
     * 商品单价（保留2位小数）
     */
    private BigDecimal price;

    /**
     * 商品库存（整数，出入库时联动修改）
     */
    private Integer stock;

    /**
     * 商品图片路径（存储URL，如：/upload/goods/xxx.png）
     */
    private String goodsImg;

    /**
     * 商品状态（0-禁用，1-启用，默认1）
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间（自动填充）
     */
    private Date createTime;

    /**
     * 更新时间（自动填充）
     */
    private Date updateTime;
}
