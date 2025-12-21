package com.xmut.warehouse.module.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库Mapper（与用户/企业Mapper格式一致，继承BaseMapper，无需手动写SQL）
 */
@Mapper // 标记为MyBatis Mapper，SpringBoot自动扫描，无需额外配置
public interface XmutWarehouseMapper extends BaseMapper<XmutWarehouse> {
    // 基础CRUD由BaseMapper提供，复杂查询可在此添加（当前无需额外方法）
}
