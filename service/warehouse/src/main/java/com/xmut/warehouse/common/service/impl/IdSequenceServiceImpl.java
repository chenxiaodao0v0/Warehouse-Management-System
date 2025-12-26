package com.xmut.warehouse.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xmut.warehouse.common.entity.IdSequence;
import com.xmut.warehouse.common.mapper.IdSequenceMapper;
import com.xmut.warehouse.common.service.IdSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ID序列服务实现
 */
@Service
public class IdSequenceServiceImpl implements IdSequenceService {

    @Autowired
    private IdSequenceMapper idSequenceMapper;

    @Override
    @Transactional
    public String getNextId(String seqName) {
        // 查询序列配置
        IdSequence sequence = idSequenceMapper.selectById(seqName);
        if (sequence == null) {
            throw new RuntimeException("序列不存在: " + seqName);
        }

        // 原子性地增加序列值
        idSequenceMapper.incrementValue(seqName);

        // 重新查询获取最新的值
        sequence = idSequenceMapper.selectById(seqName);

        // 根据前缀和位数格式化ID
        String formattedNumber = String.format("%0" + sequence.getDigits() + "d", sequence.getCurrentValue());
        return sequence.getPrefix() + formattedNumber;
    }

    @Override
    @Transactional
    public void initSequence(IdSequence idSequence) {
        // 检查序列是否已存在
        IdSequence existing = idSequenceMapper.selectById(idSequence.getSeqName());
        if (existing == null) {
            // 如果不存在，则插入新序列
            idSequenceMapper.insert(idSequence);
        }
    }
}