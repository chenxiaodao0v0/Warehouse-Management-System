package com.xmut.warehouse.module.inOutRecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出入库及调货记录Mapper（继承BaseMapper，自带基础CRUD，适配你的表）
 */
@Mapper
public interface InOutRecordMapper extends BaseMapper<InOutRecord> {
    // 基础CRUD已满足需求，后续可按需添加自定义查询（如按商品ID/操作人ID查询）
}
