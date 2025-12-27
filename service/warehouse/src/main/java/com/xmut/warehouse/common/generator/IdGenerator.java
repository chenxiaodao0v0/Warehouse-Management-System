package com.xmut.warehouse.common.generator;

import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成器工具类，为不同实体生成不同格式的ID（基于原子计数器）
 * 注意：商品ID生成已迁移到IdSequenceGenerator，此处不再包含商品ID生成方法
 */
public class IdGenerator {

    // 为不同类型实体维护独立的ID计数器
    private static final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    
    // 初始化计数器
    static {
        // 商品ID生成已迁移到数据库序列，此处不再初始化
        counters.put("warehouse", new AtomicInteger(11));     // W开头2位数
        counters.put("record", new AtomicInteger(100001));    // R开头6位数
    }

    /**
     * 为仓库实体生成ID (W开头的2位数字)
     */
    public static String nextWarehouseId() {
        int nextId = counters.get("warehouse").getAndIncrement();
        // 如果ID超出范围，重置到起始值
        if (nextId > 99) {
            counters.get("warehouse").set(11);
            nextId = counters.get("warehouse").getAndIncrement();
        }
        return "W" + String.format("%02d", nextId);
    }

    /**
     * 为出入库记录实体生成ID (R开头的6位数字)
     */
    public static String nextRecordId() {
        int nextId = counters.get("record").getAndIncrement();
        // 如果ID超出范围，重置到起始值
        if (nextId > 999999) {
            counters.get("record").set(100001);
            nextId = counters.get("record").getAndIncrement();
        }
        return "R" + String.format("%06d", nextId);
    }

    /**
     * 根据实体类型生成对应的ID
     */
    public static String nextIdByEntity(Object entity) {
        if (entity instanceof XmutGoods) {
            // 商品ID生成已迁移到IdSequenceGenerator
            return IdSequenceGenerator.nextGoodsId();
        } else if (entity instanceof XmutWarehouse) {
            return nextWarehouseId();
        } else if (entity instanceof InOutRecord) {
            return nextRecordId();
        } else {
            // 默认返回UUID
            return java.util.UUID.randomUUID().toString().replace("-", "");
        }
    }
}