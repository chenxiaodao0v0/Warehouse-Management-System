package com.xmut.warehouse.common.service;

import com.xmut.warehouse.common.entity.IdSequence;

/**
 * ID序列服务接口
 */
public interface IdSequenceService {
    /**
     * 根据序列名称获取下一个ID值
     * @param seqName 序列名称
     * @return 格式化的ID字符串
     */
    String getNextId(String seqName);

    /**
     * 初始化序列
     * @param idSequence 序列配置
     */
    void initSequence(IdSequence idSequence);
}