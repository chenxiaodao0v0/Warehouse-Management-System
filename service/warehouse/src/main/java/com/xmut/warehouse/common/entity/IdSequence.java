package com.xmut.warehouse.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * ID序列实体类，用于生成自定义格式的ID
 */
@Data
@TableName("xmut_id_sequence")
public class IdSequence {
    /**
     * 序列名称，如 "goods", "warehouse", "record"
     */
    @TableId(type = IdType.INPUT)
    @TableField("seq_name")
    private String seqName;

    /**
     * 当前序列值
     */
    @TableField("current_value")
    private Long currentValue;

    /**
     * 序列前缀，如 "G", "W", "R"
     */
    @TableField("prefix")
    private String prefix;

    /**
     * 序列值的位数
     */
    @TableField("digits")
    private Integer digits;
}