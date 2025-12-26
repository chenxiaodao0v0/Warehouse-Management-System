package com.xmut.warehouse.module.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 仓库-商品关联明细实体类（对应xmut_warehouse_goods表）
 */
@Data
@TableName("xmut_warehouse_goods")
public class WarehouseGoods {
    // 主键自动生成UUID
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 关联仓库ID */
    @TableField("warehouse_id")
    private String warehouseId;

    /** 关联商品ID */
    @TableField("goods_id")
    private String goodsId;

    /** 该仓库下该商品的库存 */
    private Integer stock;

    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    private Date updateTime;
}
