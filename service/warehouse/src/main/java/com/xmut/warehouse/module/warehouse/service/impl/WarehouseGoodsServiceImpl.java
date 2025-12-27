hipackage com.xmut.warehouse.module.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsMapper;
import com.xmut.warehouse.module.goods.entity.XmutGoodsCategory;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsCategoryMapper;
import com.xmut.warehouse.module.warehouse.entity.WarehouseGoods;
import com.xmut.warehouse.module.warehouse.mapper.WarehouseGoodsMapper;
import com.xmut.warehouse.module.warehouse.service.WarehouseGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 仓库-商品关联明细Service实现类（核心库存操作）
 */
@Service
public class WarehouseGoodsServiceImpl extends ServiceImpl<WarehouseGoodsMapper, WarehouseGoods> implements WarehouseGoodsService {


    @Autowired
    private XmutGoodsMapper xmutGoodsMapper;
    
    @Autowired
    private XmutGoodsCategoryMapper xmutGoodsCategoryMapper;

    // 获取分类名称的方法
    private String getCategoryName(String categoryId) {
        if (!StringUtils.hasText(categoryId)) {
            return "无分类";
        }
        XmutGoodsCategory category = xmutGoodsCategoryMapper.selectById(categoryId);
        return category != null ? category.getName() : "未知分类";
    }

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

    // 新增：实现查询仓库商品（含商品名称、价格等）
    @Override
    public R<List<Map<String, Object>>> getGoodsWithInfoByWarehouseId(String warehouseId) {
        // 1. 参数校验：仓库ID不能为空
        if (!StringUtils.hasText(warehouseId)) {
            return R.fail("仓库ID不能为空");
        }

        // 2. 查询该仓库下所有的仓库-商品关联记录
        LambdaQueryWrapper<WarehouseGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WarehouseGoods::getWarehouseId, warehouseId);
        List<WarehouseGoods> warehouseGoodsList = this.baseMapper.selectList(queryWrapper);

        // 3. 构建前端所需的数据结构，确保与前端表格列定义完全一致
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (WarehouseGoods warehouseGoods : warehouseGoodsList) {
            // 创建一个纯净的Map用于前端展示，字段顺序与前端表格列定义一致
            Map<String, Object> displayItem = new HashMap<>();
            
            // 从商品表获取详细信息
            XmutGoods goods = xmutGoodsMapper.selectById(warehouseGoods.getGoodsId());
            if (goods != null) {
                // 按照前端表格列顺序设置字段值（已移除minStock字段）
                displayItem.put("goodsName", goods.getName() != null ? goods.getName() : "未知商品");
                displayItem.put("goodsNo", goods.getId() != null ? goods.getId() : "N/A");
                displayItem.put("categoryName", getCategoryName(goods.getCategoryId()) != null ? getCategoryName(goods.getCategoryId()) : "未分类");
                displayItem.put("specification", goods.getRemark() != null ? goods.getRemark() : "");
                displayItem.put("unit", "个");
                displayItem.put("stock", warehouseGoods.getStock() != null ? warehouseGoods.getStock() : 0);
                displayItem.put("price", goods.getPrice() != null ? goods.getPrice() : 0.0);
                displayItem.put("image", goods.getPic() != null ? goods.getPic() : "");
            } else {
                // 商品不存在时的默认值
                displayItem.put("goodsName", "【商品已删除】");
                displayItem.put("goodsNo", "N/A");
                displayItem.put("categoryName", "N/A");
                displayItem.put("specification", "");
                displayItem.put("unit", "个");
                displayItem.put("stock", warehouseGoods.getStock() != null ? warehouseGoods.getStock() : 0);
                displayItem.put("price", 0.0);
                displayItem.put("image", "");
            }
            
            resultList.add(displayItem);
        }

        // 4. 返回结果
        return R.success(resultList);
    }

    @Override
    public R<List<Map<String, Object>>> getLowStockGoods(Integer threshold, String warehouseId) {
        // 阈值默认10（用户不传则按10判断）
        if (threshold == null || threshold < 0) {
            threshold = 10;
        }
        LambdaQueryWrapper<WarehouseGoods> wrapper = new LambdaQueryWrapper<>();
        // 核心：库存低于或等于阈值
        wrapper.le(WarehouseGoods::getStock, threshold);
        // 可选：指定仓库查询
        if (StringUtils.hasText(warehouseId)) {
            wrapper.eq(WarehouseGoods::getWarehouseId, warehouseId);
        }
        // 用selectMaps：灵活返回字段，方便补充商品名称
        List<Map<String, Object>> resultList = this.baseMapper.selectMaps(wrapper);

        // 补充商品名称（关键：用户要看到商品名，不是只看ID）
        for (Map<String, Object> record : resultList) {
            String goodsId = (String) record.get("goods_id");
            XmutGoods goods = xmutGoodsMapper.selectById(goodsId);
            // 兼容商品已删除的情况
            record.put("goods_name", goods != null ? goods.getName() : "【商品已删除】");
            // 补充商品价格（额外优化，提升实用性）
            record.put("goods_price", goods != null ? goods.getPrice() : 0.0);
        }
        return R.success(resultList);
    }
}