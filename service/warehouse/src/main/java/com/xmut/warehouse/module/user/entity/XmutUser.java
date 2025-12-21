package com.xmut.warehouse.module.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 用户实体类，映射xmut_user表
 */
@Data // Lombok注解，自动生成get/set/toString等方法
@TableName("xmut_user") // 对应数据库表，符合xmut_前缀要求
public class XmutUser {
    @TableId(type = IdType.ASSIGN_UUID) // 主键自动生成UUID
    private String id;
    private String username; // 登录账号（唯一）
    private String password; // BCrypt加密后的密码
    private String nickname; // 用户昵称
    private Integer role; // 角色：1-超级管理员，2-信息管理员（对应RoleConstant）
    private Integer status; // 状态：0-禁用，1-启用
    private String phone; // 联系方式
    private String email; // 邮箱
    private Date createTime; // 创建时间
    private Date lastLoginTime; // 最后登录时间
}
