package com.xmut.warehouse.module.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.warehouse.entity.WarehouseGoods;

import java.util.List;
import java.util.Map;

/**
 * 仓库-商品关联明细Service接口（核心：库存操作）
 */
public interface WarehouseGoodsService extends IService<WarehouseGoods> {
    /**
     * 按仓库ID查询该仓库的所有商品（含库存）
     */
    R<List<WarehouseGoods>> getGoodsByWarehouseId(String warehouseId);

    /**
     * 按商品ID+仓库ID查询关联记录（核心：用于库存校验/更新）
     */
    WarehouseGoods getByGoodsIdAndWarehouseId(String goodsId, String warehouseId);

    /**
     * 更新商品库存（入库+，出库-）
     */
    R<?> updateStock(String goodsId, String warehouseId, Integer changeNum);

    // 新增：查询某仓库所有商品（含商品名称、价格等基础信息）
    R<List<Map<String, Object>>> getGoodsWithInfoByWarehouseId(String warehouseId);
}
