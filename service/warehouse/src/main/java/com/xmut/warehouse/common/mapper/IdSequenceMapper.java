package com.xmut.warehouse.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.common.entity.IdSequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * ID序列Mapper
 */
@Mapper
public interface IdSequenceMapper extends BaseMapper<IdSequence> {
    /**
     * 原子性地增加序列值并返回新值
     * @param seqName 序列名称
     * @return 新的序列值
     */
    @Update("UPDATE xmut_id_sequence SET current_value = current_value + 1 WHERE seq_name = #{seqName}")
    int incrementValue(@Param("seqName") String seqName);
}