package com.xmut.warehouse.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.user.entity.UserWarehousePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-仓库权限关联表Mapper接口
 */
@Mapper
public interface UserWarehousePermissionMapper extends BaseMapper<UserWarehousePermission> {
}