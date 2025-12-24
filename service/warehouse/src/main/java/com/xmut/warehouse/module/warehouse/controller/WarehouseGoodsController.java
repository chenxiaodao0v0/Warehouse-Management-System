package com.xmut.warehouse.module.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.warehouse.entity.WarehouseGoods;
import com.xmut.warehouse.module.warehouse.service.WarehouseGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 仓库-商品关联明细Controller（查询仓库商品等接口）
 */
@RestController
@RequestMapping("/api/warehouse-goods")
public class WarehouseGoodsController {

    @Autowired
    private WarehouseGoodsService warehouseGoodsService;

    /**
     * 查询某仓库的所有商品（含库存）
     * 接口地址：http://localhost:8080/api/warehouse-goods/warehouse/你的仓库ID
     */
    @GetMapping("/warehouse/{warehouseId}")
    public R<List<WarehouseGoods>> getGoodsByWarehouseId(@PathVariable String warehouseId) {
        return warehouseGoodsService.getGoodsByWarehouseId(warehouseId);
    }

    /**
     * 查询某商品在所有仓库的库存分布
     * 接口地址：http://localhost:8080/api/warehouse-goods/goods/你的商品ID
     */
    @GetMapping("/goods/{goodsId}")
    public R<List<WarehouseGoods>> getWarehouseByGoodsId(@PathVariable String goodsId) {
        LambdaQueryWrapper<WarehouseGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseGoods::getGoodsId, goodsId);
        List<WarehouseGoods> warehouseGoodsList = warehouseGoodsService.list(queryWrapper);
        return R.success(warehouseGoodsList);
    }

    // 新增：查询某仓库所有商品（含商品名称、价格）
    @GetMapping("/warehouse/info/{warehouseId}")
    public R<List<Map<String, Object>>> getGoodsWithInfoByWarehouseId(@PathVariable String warehouseId) {
        return warehouseGoodsService.getGoodsWithInfoByWarehouseId(warehouseId);
    }
}
