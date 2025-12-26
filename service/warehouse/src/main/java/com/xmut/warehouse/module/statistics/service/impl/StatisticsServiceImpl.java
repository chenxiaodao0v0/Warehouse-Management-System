package com.xmut.warehouse.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.service.XmutGoodsService;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import com.xmut.warehouse.module.warehouse.entity.WarehouseGoods;
import com.xmut.warehouse.module.warehouse.service.XmutWarehouseService;
import com.xmut.warehouse.module.warehouse.service.WarehouseGoodsService;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import com.xmut.warehouse.module.inOutRecord.service.InOutRecordService;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;
import com.xmut.warehouse.module.enterprise.service.XmutEnterpriseService;
import com.xmut.warehouse.module.statistics.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private XmutGoodsService goodsService;

    @Autowired
    private XmutWarehouseService warehouseService;

    @Autowired
    private InOutRecordService inOutRecordService;

    @Autowired
    private XmutEnterpriseService enterpriseService;
    
    @Autowired
    private WarehouseGoodsService warehouseGoodsService;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        logger.info("开始统计各实体数量");
        
        try {
            // 查询商品总数前先输出调试信息
            logger.info("准备查询商品总数...");
            
            // 先尝试获取一条商品数据
            List<XmutGoods> goodsList = goodsService.list();
            logger.info("商品列表查询结果数量: {}", goodsList.size());
            if (!goodsList.isEmpty()) {
                logger.info("第一条商品数据: id={}, name={}", goodsList.get(0).getId(), goodsList.get(0).getName());
            }
            
            long goodsCount = goodsService.count();
            logger.info("商品总数查询结果: {}", goodsCount);
            statistics.put("goodsCount", goodsCount);
        } catch (Exception e) {
            logger.error("查询商品数量失败", e);
            statistics.put("goodsCount", 0);
        }
        
        try {
            long warehouseCount = warehouseService.count();
            logger.info("仓库数量: {}", warehouseCount);
            statistics.put("warehouseCount", warehouseCount);
        } catch (Exception e) {
            logger.error("查询仓库数量失败", e);
            statistics.put("warehouseCount", 0);
        }
        
        try {
            long inOutRecordCount = inOutRecordService.count();
            logger.info("出入库记录数量: {}", inOutRecordCount);
            statistics.put("inOutRecordCount", inOutRecordCount);
        } catch (Exception e) {
            logger.error("查询出入库记录数量失败", e);
            statistics.put("inOutRecordCount", 0);
        }
        
        try {
            long enterpriseCount = enterpriseService.count();
            logger.info("企业数量: {}", enterpriseCount);
            statistics.put("enterpriseCount", enterpriseCount);
        } catch (Exception e) {
            logger.error("查询企业数量失败", e);
            statistics.put("enterpriseCount", 0);
        }
        
        logger.info("统计结果: {}", statistics);
        return statistics;
    }

    @Override
    public Map<String, Object> getTrendData(int days) {
        Map<String, Object> trendData = new HashMap<>();
        
        // 生成最近几天的日期数组
        List<String> dates = new ArrayList<>();
        List<Integer> inData = new ArrayList<>();
        List<Integer> outData = new ArrayList<>();
        List<Integer> transferData = new ArrayList<>();
        
        // 使用Java 8的时间API，兼容JDK 11
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d");
        DateTimeFormatter dateStrFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(dateStrFormatter);
            dates.add(date.format(formatter));
            
            logger.info("查询日期: {}", dateStr);
            
            try {
                // 查询当天入库记录数量
                QueryWrapper<InOutRecord> inWrapper = new QueryWrapper<>();
                inWrapper.like("operate_time", dateStr).eq("type", 1);
                int inCount = (int) inOutRecordService.count(inWrapper);
                logger.info("入库记录数量 ({}): {}", dateStr, inCount);
                inData.add(inCount);
            } catch (Exception e) {
                logger.error("查询入库记录数量失败", e);
                inData.add(0);
            }
            
            try {
                // 查询当天出库记录数量
                QueryWrapper<InOutRecord> outWrapper = new QueryWrapper<>();
                outWrapper.like("operate_time", dateStr).eq("type", 2);
                int outCount = (int) inOutRecordService.count(outWrapper);
                logger.info("出库记录数量 ({}): {}", dateStr, outCount);
                outData.add(outCount);
            } catch (Exception e) {
                logger.error("查询出库记录数量失败", e);
                outData.add(0);
            }
            
            try {
                // 查询当天调货记录数量
                QueryWrapper<InOutRecord> transferWrapper = new QueryWrapper<>();
                transferWrapper.like("operate_time", dateStr).eq("type", 3);
                int transferCount = (int) inOutRecordService.count(transferWrapper);
                logger.info("调货记录数量 ({}): {}", dateStr, transferCount);
                transferData.add(transferCount);
            } catch (Exception e) {
                logger.error("查询调货记录数量失败", e);
                transferData.add(0);
            }
        }
        
        trendData.put("dates", dates);
        trendData.put("inData", inData);
        trendData.put("outData", outData);
        trendData.put("transferData", transferData);
        
        logger.info("趋势数据: {}", trendData);
        return trendData;
    }

    @Override
    public Map<String, Object> getWarehouseDistribution() {
        Map<String, Object> distributionData = new HashMap<>();
        List<Map<String, Object>> warehouseData = new ArrayList<>();
        
        try {
            // 查询所有仓库
            List<XmutWarehouse> warehouses = warehouseService.list();
            logger.info("查询到仓库数量: {}", warehouses.size());
            
            for (XmutWarehouse warehouse : warehouses) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", warehouse.getName());
                
                // 查询该仓库的总库存量
                // 通过WarehouseGoodsService获取该仓库的商品库存信息
                Integer totalStock = 0;
                try {
                    // 获取仓库中所有商品的库存信息
                    List<WarehouseGoods> goodsInWarehouse = warehouseGoodsService.getGoodsByWarehouseId(warehouse.getId()).getData();
                    if (goodsInWarehouse != null) {
                        for (WarehouseGoods wg : goodsInWarehouse) {
                            if (wg.getStock() != null) {
                                totalStock += wg.getStock();
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("查询仓库 {} 的库存信息失败", warehouse.getId(), e);
                    totalStock = 0; // 查询失败时设为0
                }
                
                item.put("value", totalStock);
                warehouseData.add(item);
                
                logger.info("仓库: {}, 总库存: {}", warehouse.getName(), totalStock);
            }
        } catch (Exception e) {
            logger.error("查询仓库分布数据失败", e);
            // 如果查询失败，返回空数组
        }
        
        distributionData.put("warehouseData", warehouseData);
        logger.info("仓库分布数据: {}", distributionData);
        return distributionData;
    }
}