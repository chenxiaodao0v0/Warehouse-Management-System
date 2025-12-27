package com.xmut.warehouse.module.inOutRecord.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 出入库及调货记录Mapper（继承BaseMapper，自带基础CRUD，适配你的表）
 */
@Mapper
public interface InOutRecordMapper extends BaseMapper<InOutRecord> {
    
    /**
     * 获取最大的出入库记录ID，用于ID序列生成
     */
    @Select("SELECT MAX(id) FROM xmut_inout_record WHERE id LIKE 'R%'")
    String getMaxRecordId();
}