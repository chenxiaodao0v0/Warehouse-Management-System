package com.xmut.warehouse.module.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商品分类实体类
 */
@Data
@TableName("xmut_goods_category")
public class XmutGoodsCategory {
    /**
     * 主键ID，使用UUID生成策略
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private java.util.Date createTime;
}