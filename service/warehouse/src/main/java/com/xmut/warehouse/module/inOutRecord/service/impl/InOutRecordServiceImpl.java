package com.xmut.warehouse.module.inOutRecord.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.generator.IdGenerator;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.common.util.PhoneValidator;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsMapper;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import com.xmut.warehouse.module.inOutRecord.mapper.InOutRecordMapper;
import com.xmut.warehouse.module.inOutRecord.service.InOutRecordService;
import com.xmut.warehouse.module.warehouse.service.WarehouseGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class InOutRecordServiceImpl extends ServiceImpl<InOutRecordMapper, InOutRecord> implements InOutRecordService {

    @Autowired
    private XmutGoodsMapper goodsMapper;

    // 注入仓库-商品Service
    @Autowired
    private WarehouseGoodsService warehouseGoodsService;

    // ========== 原有分页查询方法不变 ==========
    @Override
    public R<IPage<InOutRecord>> getInOutRecordPage(Page<InOutRecord> page, String contactPerson) {
        LambdaQueryWrapper<InOutRecord> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(contactPerson)) {
            queryWrapper.like(InOutRecord::getContactPerson, contactPerson);
        }
        queryWrapper.orderByDesc(InOutRecord::getOperateTime);
        IPage<InOutRecord> recordIPage = this.baseMapper.selectPage(page, queryWrapper);
        return R.success(recordIPage);
    }

    // ========== 入库方法修改（调用中间表库存操作） ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> goodsInStock(InOutRecord inOutRecord) {
        // 1. 原有参数校验（不变）
        if (!StringUtils.hasText(inOutRecord.getGoodsId())) {
            return R.fail("商品ID不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getWarehouseId())) {
            return R.fail("操作仓库ID不能为空");
        }
        if (inOutRecord.getQuantity() == null || inOutRecord.getQuantity() <= 0) {
            return R.fail("入库数量必须为正整数");
        }
        if (!StringUtils.hasText(inOutRecord.getContactPerson())) {
            return R.fail("对接人不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getContactPhone())) {
            return R.fail("对接人电话不能为空");
        }
        if (!PhoneValidator.isValidPhone(inOutRecord.getContactPhone())) {
            return R.fail("对接人电话格式不正确");
        }
        if (!StringUtils.hasText(inOutRecord.getOperatorId())) {
            return R.fail("操作人ID不能为空");
        }

        // 2. 校验商品是否存在（仅校验商品基础信息）
        XmutGoods goods = goodsMapper.selectById(inOutRecord.getGoodsId());
        if (goods == null) {
            return R.fail("该商品不存在，无法入库");
        }

        // 3. 手动设置ID
        if (!StringUtils.hasText(inOutRecord.getId())) {
            inOutRecord.setId(IdGenerator.nextRecordId());
        }

        // 4. 强制设置入库类型+操作时间（不变）
        inOutRecord.setType(1);
        inOutRecord.setOperateTime(new Date());

        // 5. 核心修改：调用WarehouseGoodsService更新库存（入库：changeNum=+quantity）
        R<?> stockResult = warehouseGoodsService.updateStock(
                inOutRecord.getGoodsId(),
                inOutRecord.getWarehouseId(),
                inOutRecord.getQuantity() // 正数：入库增加库存
        );
        if (stockResult.getCode() != 200) {
            return R.fail(stockResult.getCode(), stockResult.getMsg());
        }

        // 6. 新增入库记录（不变）
        boolean saveSuccess = this.save(inOutRecord);
        if (!saveSuccess) {
            // 库存已更新，记录新增失败，事务回滚
            return R.fail("入库记录新增失败，库存操作回滚");
        }

        return R.success("商品入库成功");
    }

    // ========== 出库方法修改（调用中间表库存操作） ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> goodsOutStock(InOutRecord inOutRecord) {
        // 1. 原有参数校验（不变）
        if (!StringUtils.hasText(inOutRecord.getGoodsId())) {
            return R.fail("商品ID不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getWarehouseId())) {
            return R.fail("操作仓库ID不能为空");
        }
        if (inOutRecord.getQuantity() == null || inOutRecord.getQuantity() <= 0) {
            return R.fail("出库数量必须为正整数");
        }
        if (!StringUtils.hasText(inOutRecord.getContactPerson())) {
            return R.fail("对接人不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getContactPhone())) {
            return R.fail("对接人电话不能为空");
        }
        if (!PhoneValidator.isValidPhone(inOutRecord.getContactPhone())) {
            return R.fail("对接人电话格式不正确");
        }
        if (!StringUtils.hasText(inOutRecord.getOperatorId())) {
            return R.fail("操作人ID不能为空");
        }

        // 2. 校验商品是否存在（仅校验商品基础信息）
        XmutGoods goods = goodsMapper.selectById(inOutRecord.getGoodsId());
        if (goods == null) {
            return R.fail("该商品不存在，无法出库");
        }

        // 3. 手动设置ID
        if (!StringUtils.hasText(inOutRecord.getId())) {
            inOutRecord.setId(IdGenerator.nextRecordId());
        }

        // 4. 强制设置出库类型+操作时间（不变）
        inOutRecord.setType(2);
        inOutRecord.setOperateTime(new Date());

        // 5. 核心修改：调用WarehouseGoodsService更新库存（出库：changeNum=-quantity）
        R<?> stockResult = warehouseGoodsService.updateStock(
                inOutRecord.getGoodsId(),
                inOutRecord.getWarehouseId(),
                -inOutRecord.getQuantity() // 负数：出库减少库存
        );
        if (stockResult.getCode() != 200) {
            return R.fail(stockResult.getCode(), stockResult.getMsg());
        }

        // 6. 新增出库记录（不变）
        boolean saveSuccess = this.save(inOutRecord);
        if (!saveSuccess) {
            return R.fail("出库记录新增失败，库存操作回滚");
        }

        return R.success("商品出库成功");
    }

    // ========== 调货方法修改（核心：原仓库-库存，目标仓库+库存） ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> goodsTransfer(InOutRecord inOutRecord) {
        // 1. 原有参数校验（新增原仓库≠目标仓库）
        if (!StringUtils.hasText(inOutRecord.getGoodsId())) {
            return R.fail("商品ID不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getWarehouseId())) {
            return R.fail("原操作仓库ID不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getRelatedWarehouseId())) {
            return R.fail("调货目标仓库ID不能为空");
        }
        if (inOutRecord.getWarehouseId().equals(inOutRecord.getRelatedWarehouseId())) {
            return R.fail("原仓库和目标仓库不能为同一个仓库，无法调货");
        }
        if (inOutRecord.getQuantity() == null || inOutRecord.getQuantity() <= 0) {
            return R.fail("调货数量必须为正整数");
        }
        if (!StringUtils.hasText(inOutRecord.getContactPerson())) {
            return R.fail("对接人不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getContactPhone())) {
            return R.fail("对接人电话不能为空");
        }
        if (!PhoneValidator.isValidPhone(inOutRecord.getContactPhone())) {
            return R.fail("对接人电话格式不正确");
        }
        if (!StringUtils.hasText(inOutRecord.getOperatorId())) {
            return R.fail("操作人ID不能为空");
        }

        // 2. 校验商品是否存在（仅校验商品基础信息）
        XmutGoods goods = goodsMapper.selectById(inOutRecord.getGoodsId());
        if (goods == null) {
            return R.fail("该商品不存在，无法调货");
        }

        // 3. 手动设置ID
        if (!StringUtils.hasText(inOutRecord.getId())) {
            inOutRecord.setId(IdGenerator.nextRecordId());
        }

        // 4. 强制设置调货类型+操作时间（不变）
        inOutRecord.setType(3);
        inOutRecord.setOperateTime(new Date());

        // 5. 核心修改：原仓库减库存（-quantity）
        R<?> sourceStockResult = warehouseGoodsService.updateStock(
                inOutRecord.getGoodsId(),
                inOutRecord.getWarehouseId(),
                -inOutRecord.getQuantity() // 原仓库：出库（减库存）
        );
        if (sourceStockResult.getCode() != 200) {
            return R.fail(sourceStockResult.getCode(), "原仓库库存操作失败：" + sourceStockResult.getMsg());
        }

        // 6. 核心修改：目标仓库加库存（+quantity）
        R<?> targetStockResult = warehouseGoodsService.updateStock(
                inOutRecord.getGoodsId(),
                inOutRecord.getRelatedWarehouseId(),
                inOutRecord.getQuantity() // 目标仓库：入库（加库存）
        );
        if (targetStockResult.getCode() != 200) {
            // 目标仓库库存操作失败，事务回滚（原仓库库存已自动恢复）
            return R.fail(targetStockResult.getCode(), "目标仓库库存操作失败：" + targetStockResult.getMsg());
        }

        // 7. 新增调货记录（不变）
        boolean saveSuccess = this.save(inOutRecord);
        if (!saveSuccess) {
            return R.fail("调货记录新增失败，库存操作回滚");
        }

        return R.success("商品调货成功（原仓库减库存、目标仓库加库存）");
    }

    @Override
    public R<List<InOutRecord>> getRecordByTimeAndWarehouse(Date startTime, Date endTime, String warehouseId) {
        LambdaQueryWrapper<InOutRecord> wrapper = new LambdaQueryWrapper<>();
        // 时间范围筛选（startTime不为null才加条件）
        if (startTime != null) {
            wrapper.ge(InOutRecord::getOperateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(InOutRecord::getOperateTime, endTime);
        }
        // 仓库筛选（支持模糊匹配调货记录：原仓库/目标仓库都算）
        if (StringUtils.hasText(warehouseId)) {
            wrapper.and(w -> w.eq(InOutRecord::getWarehouseId, warehouseId)
                    .or().eq(InOutRecord::getRelatedWarehouseId, warehouseId));
        }
        // 按操作时间倒序（最新记录在前，符合使用习惯）
        wrapper.orderByDesc(InOutRecord::getOperateTime);
        List<InOutRecord> recordList = this.list(wrapper);
        return R.success(recordList);
    }

    @Override
    public R<List<InOutRecord>> getRecordByGoodsId(String goodsId) {
        LambdaQueryWrapper<InOutRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InOutRecord::getGoodsId, goodsId)
                .orderByDesc(InOutRecord::getOperateTime); // 按操作时间倒序，最新的在前
        List<InOutRecord> records = this.baseMapper.selectList(queryWrapper);
        return R.success(records);
    }

    @Override
    public R<IPage<InOutRecord>> getRecentInOutRecords(Page<InOutRecord> page) {
        LambdaQueryWrapper<InOutRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(InOutRecord::getOperateTime); // 按操作时间倒序，最新的在前
        IPage<InOutRecord> recordIPage = this.baseMapper.selectPage(page, queryWrapper);
        return R.success(recordIPage);
    }
}