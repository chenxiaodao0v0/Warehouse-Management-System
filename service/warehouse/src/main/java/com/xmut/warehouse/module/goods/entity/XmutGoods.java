package com.xmut.warehouse.module.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@TableName("xmut_goods")
public class XmutGoods {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    // 商品名称不能为空，提示信息自定义
    @NotBlank(message = "商品名称不能为空")
    private String name;

    // 商品价格不能为空 + 不能小于0
    @NotNull(message = "商品价格不能为空")
    @Min(value = 0, message = "商品价格不能为负数")
    private Double price;

    // 商品类别ID不能为空
    @NotBlank(message = "商品类别ID不能为空")
    private String categoryId;

    private String pic;

    private Integer status;

    private String remark;

    @TableField("create_time")
    private Date createTime;
}
