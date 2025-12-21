package com.xmut.warehouse.module.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品Mapper（与企业、仓库Mapper格式一致，继承BaseMapper）
 */
@Mapper
public interface XmutGoodsMapper extends BaseMapper<XmutGoods> {
    // 基础CRUD由BaseMapper提供，以下为自定义关联查询（可选，按需使用）
    /**
     * 根据仓库ID查询商品列表
     */
    List<XmutGoods> selectByWarehouseId(String warehouseId);

    /**
     * 根据类别ID查询商品列表
     */
    List<XmutGoods> selectByCategoryId(String categoryId);
}
