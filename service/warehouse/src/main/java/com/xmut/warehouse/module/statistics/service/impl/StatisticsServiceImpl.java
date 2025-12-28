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
            // 单企业系统，查询企业信息并返回企业名称
            XmutEnterprise enterprise = enterpriseService.getOne(new QueryWrapper<>(), false);
            if (enterprise != null) {
                statistics.put("enterpriseName", enterprise.getName());
                statistics.put("enterpriseCount", 1); // 单企业系统，存在企业则计数为1
            } else {
                statistics.put("enterpriseName", "暂无企业信息");
                statistics.put("enterpriseCount", 0);
            }
            logger.info("企业信息: {}", enterprise != null ? enterprise.getName() : "暂无企业信息");
        } catch (Exception e) {
            logger.error("查询企业信息失败", e);
            statistics.put("enterpriseName", "暂无企业信息");
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
    
    @Override
    public Map<String, Object> getTopTenInOutData(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> topTenData = new ArrayList<>();
        
        try {
            logger.info("开始查询进出货物数量前十排行，时间段: {} - {}", startDate, endDate);
            
            // 构建查询条件
            QueryWrapper<InOutRecord> queryWrapper = new QueryWrapper<>();
            
            // 如果提供了开始和结束日期，则添加时间范围条件
            if (startDate != null && !startDate.isEmpty()) {
                queryWrapper.ge("operate_time", startDate + " 00:00:00");
            }
            if (endDate != null && !endDate.isEmpty()) {
                queryWrapper.le("operate_time", endDate + " 23:59:59");
            }
            
            // 查询所有记录
            List<InOutRecord> records = inOutRecordService.list(queryWrapper);
            
            // 使用正确的数据类型来存储商品ID，并创建一个Map来存储每种商品的统计信息
            Map<String, Map<String, Object>> goodsStatsMap = new HashMap<>();
            
            for (InOutRecord record : records) {
                String goodsId = record.getGoodsId();
                if (goodsId == null) {
                    continue; // 跳过商品ID为空的记录
                }
                
                if (!goodsStatsMap.containsKey(goodsId)) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("goodsId", goodsId);
                    item.put("totalQuantity", 0);
                    item.put("inQuantity", 0);
                    item.put("outQuantity", 0);
                    item.put("goodsName", "");
                    item.put("enterpriseName", "暂无企业信息");
                    goodsStatsMap.put(goodsId, item);
                }
                
                Map<String, Object> item = goodsStatsMap.get(goodsId);
                
                // 累加总数量
                int quantity = record.getQuantity() != null ? record.getQuantity() : 0;
                
                // 安全地获取并转换数量值
                Integer currentTotalQuantity = getSafeIntegerValue(item.get("totalQuantity"));
                currentTotalQuantity += quantity;
                item.put("totalQuantity", currentTotalQuantity);
                
                // 根据类型分别累加入库/出库数量
                if (record.getType() != null) {
                    if (record.getType() == 1) { // 入库
                        Integer currentInQuantity = getSafeIntegerValue(item.get("inQuantity"));
                        currentInQuantity += quantity;
                        item.put("inQuantity", currentInQuantity);
                    } else if (record.getType() == 2) { // 出库
                        Integer currentOutQuantity = getSafeIntegerValue(item.get("outQuantity"));
                        currentOutQuantity += quantity;
                        item.put("outQuantity", currentOutQuantity);
                    }
                }
            }
            
            // 查询商品信息并填充到汇总数据中
            for (Map.Entry<String, Map<String, Object>> entry : goodsStatsMap.entrySet()) {
                String goodsId = entry.getKey();
                Map<String, Object> item = entry.getValue();
                
                // 查询商品信息
                XmutGoods goods = goodsService.getById(goodsId);
                if (goods != null) {
                    item.put("goodsName", goods.getName());
                    
                    // 由于XmutGoods实体中没有enterpriseId字段，我们无法直接获取企业信息
                    // 但在单企业系统中，我们可以获取企业信息并设置
                    try {
                        XmutEnterprise enterprise = enterpriseService.getOne(new QueryWrapper<>(), false);
                        if (enterprise != null) {
                            item.put("enterpriseName", enterprise.getName());
                        } else {
                            item.put("enterpriseName", "暂无企业信息");
                        }
                    } catch (Exception e) {
                        logger.error("获取企业信息失败", e);
                        item.put("enterpriseName", "暂无企业信息");
                    }
                }
            }
            
            // 转换为列表并按总数量排序
            List<Map<String, Object>> allData = new ArrayList<>(goodsStatsMap.values());
            allData.sort((o1, o2) -> {
                Integer qty1 = getSafeIntegerValue(o1.get("totalQuantity"));
                Integer qty2 = getSafeIntegerValue(o2.get("totalQuantity"));
                return qty2.compareTo(qty1);
            });
            
            // 取前十
            topTenData = allData.stream()
                               .limit(10)
                               .collect(java.util.stream.Collectors.toList());
            
            logger.info("查询到进出货物数量前十排行数据数量: {}", topTenData.size());
        } catch (Exception e) {
            logger.error("查询进出货物数量前十排行失败", e);
        }
        
        result.put("topTenData", topTenData);
        return result;
    }
    
    /**
     * 安全地从Object转换为Integer
     * @param obj 待转换的对象
     * @return Integer值，如果转换失败则返回0
     */
    private Integer getSafeIntegerValue(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else {
            return 0;
        }
    }
}