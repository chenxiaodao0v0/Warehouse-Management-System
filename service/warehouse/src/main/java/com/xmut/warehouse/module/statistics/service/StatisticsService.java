package com.xmut.warehouse.module.statistics.service;

import java.util.Map;

public interface StatisticsService {
    /**
     * 获取统计信息
     */
    Map<String, Object> getStatistics();

    /**
     * 获取趋势数据
     */
    Map<String, Object> getTrendData(int days);

    /**
     * 获取仓库库存分布
     */
    Map<String, Object> getWarehouseDistribution();
}