package com.xmut.warehouse.module.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsMapper;
import com.xmut.warehouse.module.warehouse.entity.WarehouseGoods;
import com.xmut.warehouse.module.warehouse.mapper.WarehouseGoodsMapper;
import com.xmut.warehouse.module.warehouse.service.WarehouseGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 仓库-商品关联明细Service实现类（核心库存操作）
 */
@Service
public class WarehouseGoodsServiceImpl extends ServiceImpl<WarehouseGoodsMapper, WarehouseGoods> implements WarehouseGoodsService {


    @Autowired
    private XmutGoodsMapper xmutGoodsMapper;

    @Override
    public R<List<WarehouseGoods>> getGoodsByWarehouseId(String warehouseId) {
        // 参数校验
        if (!StringUtils.hasText(warehouseId)) {
            return R.fail("仓库ID不能为空");
        }
        // 按仓库ID查询
        LambdaQueryWrapper<WarehouseGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseGoods::getWarehouseId, warehouseId);
        List<WarehouseGoods> warehouseGoodsList = this.baseMapper.selectList(queryWrapper);
        return R.success(warehouseGoodsList);
    }

    @Override
    public WarehouseGoods getByGoodsIdAndWarehouseId(String goodsId, String warehouseId) {
        // 参数校验
        if (!StringUtils.hasText(goodsId) || !StringUtils.hasText(warehouseId)) {
            return null;
        }
        // 按商品ID+仓库ID精准查询
        LambdaQueryWrapper<WarehouseGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseGoods::getGoodsId, goodsId)
                .eq(WarehouseGoods::getWarehouseId, warehouseId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> updateStock(String goodsId, String warehouseId, Integer changeNum) {
        // 1. 参数校验
        if (!StringUtils.hasText(goodsId) || !StringUtils.hasText(warehouseId) || changeNum == null) {
            return R.fail("参数不能为空");
        }
        // 2. 查询仓库-商品关联记录
        WarehouseGoods warehouseGoods = this.getByGoodsIdAndWarehouseId(goodsId, warehouseId);
        if (warehouseGoods == null) {
            // 无记录：仅当入库（changeNum>0）时，新增关联记录；出库（changeNum<0）时，返回库存不足
            if (changeNum > 0) {
                warehouseGoods = new WarehouseGoods();
                warehouseGoods.setGoodsId(goodsId);
                warehouseGoods.setWarehouseId(warehouseId);
                warehouseGoods.setStock(changeNum); // 初始库存=入库数量
                this.save(warehouseGoods);
                return R.success("库存新增成功");
            } else {
                return R.fail("该仓库无该商品，无法出库");
            }
        }
        // 3. 有记录：计算新库存，校验出库库存是否充足
        Integer newStock = warehouseGoods.getStock() + changeNum;
        if (newStock < 0) {
            return R.fail("商品库存不足，当前库存：" + warehouseGoods.getStock() + "，需出库：" + Math.abs(changeNum));
        }
        // 4. 更新库存
        warehouseGoods.setStock(newStock);
        this.updateById(warehouseGoods);
        return R.success("库存更新成功");
    }

    // 新增：实现查询仓库商品（含商品名称、价格）
    @Override
    public R<List<Map<String, Object>>> getGoodsWithInfoByWarehouseId(String warehouseId) {
        // 1. 参数校验：仓库ID不能为空
        if (!StringUtils.hasText(warehouseId)) {
            return R.fail("仓库ID不能为空");
        }

        // 2. 查询该仓库下所有的仓库-商品关联记录（只查核心字段）
        LambdaQueryWrapper<WarehouseGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseGoods::getWarehouseId, warehouseId)
                .select(
                        WarehouseGoods::getId,
                        WarehouseGoods::getWarehouseId,
                        WarehouseGoods::getGoodsId,
                        WarehouseGoods::getStock,
                        WarehouseGoods::getCreateTime,
                        WarehouseGoods::getUpdateTime
                );
        // 用selectMaps获取结果，返回Map集合，方便补充商品名称
        List<Map<String, Object>> resultList = this.baseMapper.selectMaps(queryWrapper);

        // 3. 循环给每条记录补充商品名称、价格等基础信息
        for (Map<String, Object> record : resultList) {
            // 获取商品ID
            String goodsId = (String) record.get("goods_id");
            // 若商品ID有效，查询商品基础信息
            if (StringUtils.hasText(goodsId)) {
                XmutGoods goods = xmutGoodsMapper.selectById(goodsId);
                if (goods != null) {
                    // 补充商品名称
                    record.put("goods_name", goods.getName());
                    // 可选：补充商品价格、类别等其他基础信息
                    record.put("goods_price", goods.getPrice());
                    record.put("goods_category_id", goods.getCategoryId());
                    record.put("goods_status", goods.getStatus());
                } else {
                    // 若商品不存在，填充默认值
                    record.put("goods_name", "【商品已删除】");
                    record.put("goods_price", 0.0);
                }
            } else {
                // 商品ID无效，填充默认值
                record.put("goods_name", "【商品ID无效】");
                record.put("goods_price", 0.0);
            }
        }

        // 4. 返回结果
        return R.success(resultList);
    }
}
