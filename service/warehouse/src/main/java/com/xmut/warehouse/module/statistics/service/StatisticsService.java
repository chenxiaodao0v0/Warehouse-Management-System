package com.xmut.warehouse.module.statistics.service;

import java.util.Map;

/**
 * 统计服务接口
 */
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
    
    /**
     * 根据时间段统计进出货物数量前十排行
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排行数据
     */
    Map<String, Object> getTopTenInOutData(String startDate, String endDate);
}