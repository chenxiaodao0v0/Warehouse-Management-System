package com.xmut.warehouse.module.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.warehouse.entity.WarehouseGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库-商品关联明细Mapper（基础CRUD）
 */
@Mapper
public interface WarehouseGoodsMapper extends BaseMapper<WarehouseGoods> {
}
