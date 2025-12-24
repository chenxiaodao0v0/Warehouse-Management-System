package com.xmut.warehouse.module.inOutRecord.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsMapper;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import com.xmut.warehouse.module.inOutRecord.mapper.InOutRecordMapper;
import com.xmut.warehouse.module.inOutRecord.service.InOutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 出入库及调货记录Service实现类（适配你的xmut_in_out_record表，核心业务逻辑）
 */
@Service
public class InOutRecordServiceImpl extends ServiceImpl<InOutRecordMapper, InOutRecord> implements InOutRecordService {

    @Autowired
    private XmutGoodsMapper goodsMapper;

    @Override
    public R<IPage<InOutRecord>> getInOutRecordPage(Page<InOutRecord> page, String contactPerson) {
        LambdaQueryWrapper<InOutRecord> queryWrapper = new LambdaQueryWrapper<>();
        // 对接人模糊查询
        if (StringUtils.hasText(contactPerson)) {
            queryWrapper.like(InOutRecord::getContactPerson, contactPerson);
        }
        // 按操作时间倒序排序（最新记录在前）
        queryWrapper.orderByDesc(InOutRecord::getOperateTime);
        // 分页查询
        IPage<InOutRecord> recordIPage = this.baseMapper.selectPage(page, queryWrapper);
        return R.success(recordIPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 事务保障：异常时回滚所有操作
    public R<?> goodsInStock(InOutRecord inOutRecord) {
        // 1. 参数校验（适配你的表必填字段）
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
        if (!StringUtils.hasText(inOutRecord.getOperatorId())) {
            return R.fail("操作人ID不能为空");
        }

        // 2. 强制设置操作类型为入库（1），设置操作时间
        inOutRecord.setType(1);
        inOutRecord.setOperateTime(new Date());

        // 3. 查询商品是否存在
        XmutGoods goods = goodsMapper.selectById(inOutRecord.getGoodsId());
        if (goods == null) {
            return R.fail("该商品不存在，无法入库");
        }

        // 4. 新增出入库记录
        boolean saveSuccess = this.save(inOutRecord);
        if (!saveSuccess) {
            return R.fail("入库记录新增失败");
        }

        // 5. 更新商品库存（入库：库存增加）
        goods.setStock(goods.getStock() + inOutRecord.getQuantity());
        boolean updateGoodsSuccess = goodsMapper.updateById(goods) > 0;
        if (!updateGoodsSuccess) {
            // 库存更新失败，事务自动回滚（入库记录也会被删除）
            return R.fail("商品库存更新失败，入库操作回滚");
        }

        return R.success("商品入库成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> goodsOutStock(InOutRecord inOutRecord) {
        // 1. 参数校验（适配你的表必填字段）
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
        if (!StringUtils.hasText(inOutRecord.getOperatorId())) {
            return R.fail("操作人ID不能为空");
        }

        // 2. 强制设置操作类型为出库（2），设置操作时间
        inOutRecord.setType(2);
        inOutRecord.setOperateTime(new Date());

        // 3. 查询商品是否存在并校验库存
        XmutGoods goods = goodsMapper.selectById(inOutRecord.getGoodsId());
        if (goods == null) {
            return R.fail("该商品不存在，无法出库");
        }
        if (goods.getStock() < inOutRecord.getQuantity()) {
            return R.fail("商品库存不足，当前库存：" + goods.getStock() + "，需出库：" + inOutRecord.getQuantity());
        }

        // 4. 新增出库记录
        boolean saveSuccess = this.save(inOutRecord);
        if (!saveSuccess) {
            return R.fail("出库记录新增失败");
        }

        // 5. 更新商品库存（出库：库存减少）
        goods.setStock(goods.getStock() - inOutRecord.getQuantity());
        boolean updateGoodsSuccess = goodsMapper.updateById(goods) > 0;
        if (!updateGoodsSuccess) {
            return R.fail("商品库存更新失败，出库操作回滚");
        }

        return R.success("商品出库成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> goodsTransfer(InOutRecord inOutRecord) {
        // 1. 基础参数校验（新增：原仓库≠目标仓库）
        if (!StringUtils.hasText(inOutRecord.getGoodsId())) {
            return R.fail("商品ID不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getWarehouseId())) {
            return R.fail("原操作仓库ID不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getRelatedWarehouseId())) {
            return R.fail("调货目标仓库ID不能为空");
        }
        // 新增：原仓库和目标仓库不能相同，避免无效调货
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
        if (!StringUtils.hasText(inOutRecord.getOperatorId())) {
            return R.fail("操作人ID不能为空");
        }

        // 2. 强制设置操作类型为调货（3），设置操作时间
        inOutRecord.setType(3);
        inOutRecord.setOperateTime(new Date());

        // 3. 原仓库商品校验：查询商品是否存在 + 库存是否充足
        // 构造条件：商品ID + 原仓库ID（确保是原仓库的该商品）
        LambdaQueryWrapper<XmutGoods> sourceGoodsWrapper = new LambdaQueryWrapper<>();
        sourceGoodsWrapper.eq(XmutGoods::getId, inOutRecord.getGoodsId())
                .eq(XmutGoods::getWarehouseId, inOutRecord.getWarehouseId());
        XmutGoods sourceGoods = goodsMapper.selectOne(sourceGoodsWrapper);

        if (sourceGoods == null) {
            return R.fail("原仓库不存在该商品，无法调货");
        }
        if (sourceGoods.getStock() < inOutRecord.getQuantity()) {
            return R.fail("原仓库商品库存不足，当前库存：" + sourceGoods.getStock() + "，需调货：" + inOutRecord.getQuantity());
        }

        // 4. 目标仓库商品处理：存在则加库存，不存在则新增
        // 构造条件：商品ID（复用原商品基础信息） + 目标仓库ID
        LambdaQueryWrapper<XmutGoods> targetGoodsWrapper = new LambdaQueryWrapper<>();
        targetGoodsWrapper.eq(XmutGoods::getId, inOutRecord.getGoodsId())
                .eq(XmutGoods::getWarehouseId, inOutRecord.getRelatedWarehouseId());
        XmutGoods targetGoods = goodsMapper.selectOne(targetGoodsWrapper);

        if (targetGoods != null) {
            // 情况1：目标仓库已存在该商品 → 库存增加
            targetGoods.setStock(targetGoods.getStock() + inOutRecord.getQuantity());
            goodsMapper.updateById(targetGoods);
        } else {
            // 情况2：目标仓库不存在该商品 → 新增商品关联目标仓库，库存为调货数量
            targetGoods = new XmutGoods();
            // 复用原商品的核心信息（名称、价格、图片等，从原商品复制）
            targetGoods.setWarehouseId(inOutRecord.getRelatedWarehouseId()); // 关联目标仓库
            targetGoods.setGoodsName(sourceGoods.getGoodsName()); // 商品名称（从原商品复制）
            targetGoods.setPrice(sourceGoods.getPrice()); // 商品单价（从原商品复制）
            targetGoods.setGoodsImg(sourceGoods.getGoodsImg()); // 商品图片（从原商品复制）
            targetGoods.setStock(inOutRecord.getQuantity()); // 初始库存=调货数量
            targetGoods.setCategoryId(sourceGoods.getCategoryId()); // 商品类别（从原商品复制）
            targetGoods.setStatus(sourceGoods.getStatus()); // 上下架状态（从原商品复制）
            targetGoods.setRemark(sourceGoods.getRemark()); // 商品备注（从原商品复制）
            targetGoods.setCreateTime(new Date()); // 创建时间
            goodsMapper.insert(targetGoods);
        }

        // 5. 原仓库商品库存减少（调货出库）
        sourceGoods.setStock(sourceGoods.getStock() - inOutRecord.getQuantity());
        goodsMapper.updateById(sourceGoods);

        // 6. 新增调货记录
        boolean saveSuccess = this.save(inOutRecord);
        if (!saveSuccess) {
            // 记录新增失败，事务自动回滚（库存调整全部撤销）
            return R.fail("调货记录新增失败，调货操作回滚");
        }

        return R.success("商品调货成功（原仓库减库存、目标仓库加库存）");
    }

}
