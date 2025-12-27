package com.xmut.warehouse.module.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 仓库Mapper（与用户/企业Mapper格式一致，继承BaseMapper，无需手动写SQL）
 */
@Mapper // 标记为MyBatis Mapper，SpringBoot自动扫描，无需额外配置
public interface XmutWarehouseMapper extends BaseMapper<XmutWarehouse> {
    
    /**
     * 获取最大的仓库ID，用于ID序列生成
     */
    @Select("SELECT MAX(id) FROM xmut_warehouse WHERE id LIKE 'W%'")
    String getMaxWarehouseId();
}