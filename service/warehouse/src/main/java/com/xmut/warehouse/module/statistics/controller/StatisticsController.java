package com.xmut.warehouse.module.statistics.controller;

import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.service.XmutGoodsService;
import com.xmut.warehouse.module.warehouse.service.XmutWarehouseService;
import com.xmut.warehouse.module.inOutRecord.service.InOutRecordService;
import com.xmut.warehouse.module.enterprise.service.XmutEnterpriseService;
import com.xmut.warehouse.module.statistics.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 统计信息控制器
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private XmutGoodsService goodsService;

    @Autowired
    private XmutWarehouseService warehouseService;

    @Autowired
    private InOutRecordService inOutRecordService;

    @Autowired
    private XmutEnterpriseService enterpriseService;

    /**
     * 获取统计信息
     */
    @GetMapping
    public R getStatistics() {
        try {
            logger.info("开始获取统计信息");
            Map<String, Object> statistics = statisticsService.getStatistics();
            logger.info("统计信息查询完成: {}", statistics);
            return R.success(statistics);
        } catch (Exception e) {
            logger.error("获取统计信息失败", e);
            return R.fail("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    public R getTrendData(@RequestParam(defaultValue = "7") int days) {
        try {
            logger.info("开始获取趋势数据，天数: {}", days);
            Map<String, Object> trendData = statisticsService.getTrendData(days);
            logger.info("趋势数据查询完成");
            return R.success(trendData);
        } catch (Exception e) {
            logger.error("获取趋势数据失败", e);
            return R.fail("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取仓库库存分布
     */
    @GetMapping("/warehouse-distribution")
    public R getWarehouseDistribution() {
        try {
            logger.info("开始获取仓库库存分布");
            Map<String, Object> distributionData = statisticsService.getWarehouseDistribution();
            logger.info("仓库库存分布查询完成，仓库数量: {}", 
                distributionData.get("warehouseData") != null ? 
                ((java.util.List) distributionData.get("warehouseData")).size() : 0);
            return R.success(distributionData);
        } catch (Exception e) {
            logger.error("获取仓库库存分布失败", e);
            return R.fail("获取仓库库存分布失败: " + e.getMessage());
        }
    }
}