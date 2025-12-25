package com.xmut.warehouse.module.enterprise.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("xmut_enterprise")
public class XmutEnterprise {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name; // 企业名称
    private String address; // 地址
    private String contact; // 联系人
    private String phone; // 联系电话
    private String remark; // 备注
    @TableField("update_time")
    private Date updateTime; // 最后更新时间
}
