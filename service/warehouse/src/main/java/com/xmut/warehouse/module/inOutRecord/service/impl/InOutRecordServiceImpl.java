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
        // 1. 参数校验（调货特有：目标仓库ID必填）
        if (!StringUtils.hasText(inOutRecord.getGoodsId())) {
            return R.fail("商品ID不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getWarehouseId())) {
            return R.fail("原操作仓库ID不能为空");
        }
        if (!StringUtils.hasText(inOutRecord.getRelatedWarehouseId())) {
            return R.fail("调货目标仓库ID不能为空");
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

        // 3. 校验商品是否存在（基础校验，后续可优化：跨仓库库存调整）
        XmutGoods goods = goodsMapper.selectById(inOutRecord.getGoodsId());
        if (goods == null) {
            return R.fail("该商品不存在，无法调货");
        }
        // 基础版：仅新增调货记录，后续可根据业务需求优化「原仓库减库存、目标仓库加库存」及商品仓库关联调整
        boolean saveSuccess = this.save(inOutRecord);
        if (!saveSuccess) {
            return R.fail("调货记录新增失败");
        }

        return R.success("商品调货记录新增成功（基础版，可后续优化库存跨仓库调整逻辑）");
    }
}
