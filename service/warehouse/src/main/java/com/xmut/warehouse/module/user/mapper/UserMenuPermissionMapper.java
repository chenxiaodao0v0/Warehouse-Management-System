package com.xmut.warehouse.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.user.entity.UserMenuPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-菜单权限关联表Mapper接口
 */
@Mapper
public interface UserMenuPermissionMapper extends BaseMapper<UserMenuPermission> {
}