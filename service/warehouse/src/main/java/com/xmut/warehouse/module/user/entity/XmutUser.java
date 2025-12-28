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
    
    // 显式添加getter和setter方法，确保在Lombok注解处理器未正确工作时仍能正常编译和运行
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public Integer getRole() {
        return role;
    }
    
    public void setRole(Integer role) {
        this.role = role;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}