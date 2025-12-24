package com.xmut.warehouse.module.inOutRecord.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;

/**
 * 出入库及调货记录Service接口（适配你的表结构）
 */
public interface InOutRecordService extends IService<InOutRecord> {
    // 分页查询出入库/调货记录（支持模糊查询对接人）
    R<IPage<InOutRecord>> getInOutRecordPage(Page<InOutRecord> page, String contactPerson);

    // 商品入库（自动增加商品库存）
    R<?> goodsInStock(InOutRecord inOutRecord);

    // 商品出库（校验库存充足，自动减少商品库存）
    R<?> goodsOutStock(InOutRecord inOutRecord);

    // 商品调货（暂支持基础记录新增，后续可优化库存跨仓库调整逻辑）
    R<?> goodsTransfer(InOutRecord inOutRecord);
}
