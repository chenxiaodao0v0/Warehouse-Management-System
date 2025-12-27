package com.xmut.warehouse.common.generator;

import com.xmut.warehouse.module.goods.mapper.XmutGoodsMapper;
import com.xmut.warehouse.module.warehouse.mapper.XmutWarehouseMapper;
import com.xmut.warehouse.module.inOutRecord.mapper.InOutRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于数据库的ID序列生成器
 */
@Component
@DependsOn({"xmutGoodsMapper", "xmutWarehouseMapper", "inOutRecordMapper"})
public class IdSequenceGenerator {

    // 为不同类型实体维护ID序列
    private static final ConcurrentHashMap<String, AtomicInteger> sequenceCounters = new ConcurrentHashMap<>();

    @Autowired
    private XmutGoodsMapper goodsMapper;
    
    @Autowired
    private XmutWarehouseMapper warehouseMapper;
    
    @Autowired
    private InOutRecordMapper recordMapper;

    /**
     * 初始化ID序列计数器，从数据库中获取最大ID值
     */
    @PostConstruct
    public void initSequenceCounters() {
        // 初始化商品ID序列
        initGoodsSequence();
        // 初始化仓库ID序列
        initWarehouseSequence();
        // 初始化记录ID序列
        initRecordSequence();
    }

    /**
     * 初始化商品ID序列
     */
    private void initGoodsSequence() {
        try {
            // 查询当前最大的商品ID，提取数字部分
            String maxId = goodsMapper.getMaxGoodsId();
            int currentNumber = 1000; // 默认起始值
            
            if (maxId != null && maxId.startsWith("G")) {
                try {
                    currentNumber = Integer.parseInt(maxId.substring(1));
                } catch (NumberFormatException e) {
                    // 如果解析失败，使用默认值
                }
            }
            
            sequenceCounters.put("goods", new AtomicInteger(currentNumber));
        } catch (Exception e) {
            // 如果查询失败，使用默认起始值
            sequenceCounters.put("goods", new AtomicInteger(1000));
            e.printStackTrace();
        }
    }

    /**
     * 初始化仓库ID序列
     */
    private void initWarehouseSequence() {
        try {
            // 查询当前最大的仓库ID，提取数字部分
            String maxId = warehouseMapper.getMaxWarehouseId();
            int currentNumber = 10; // 默认起始值
            
            if (maxId != null && maxId.startsWith("W")) {
                try {
                    currentNumber = Integer.parseInt(maxId.substring(1));
                } catch (NumberFormatException e) {
                    // 如果解析失败，使用默认值
                }
            }
            
            sequenceCounters.put("warehouse", new AtomicInteger(currentNumber));
        } catch (Exception e) {
            // 如果查询失败，使用默认起始值
            sequenceCounters.put("warehouse", new AtomicInteger(10));
            e.printStackTrace();
        }
    }

    /**
     * 初始化记录ID序列
     */
    private void initRecordSequence() {
        try {
            // 查询当前最大的记录ID，提取数字部分
            String maxId = recordMapper.getMaxRecordId();
            int currentNumber = 100000; // 默认起始值
            
            if (maxId != null && maxId.startsWith("R")) {
                try {
                    currentNumber = Integer.parseInt(maxId.substring(1));
                } catch (NumberFormatException e) {
                    // 如果解析失败，使用默认值
                }
            }
            
            sequenceCounters.put("record", new AtomicInteger(currentNumber));
        } catch (Exception e) {
            // 如果查询失败，使用默认起始值
            sequenceCounters.put("record", new AtomicInteger(100000));
            e.printStackTrace();
        }
    }

    /**
     * 为商品实体生成ID (G开头的4位数字)
     */
    public static String nextGoodsId() {
        int nextId = sequenceCounters.get("goods").incrementAndGet();
        // 如果ID超出范围，重置到起始值
        if (nextId > 9999) {
            sequenceCounters.get("goods").set(1000);
            nextId = sequenceCounters.get("goods").incrementAndGet();
        }
        return "G" + String.format("%04d", nextId);
    }

    /**
     * 为仓库实体生成ID (W开头的2位数字)
     */
    public static String nextWarehouseId() {
        int nextId = sequenceCounters.get("warehouse").incrementAndGet();
        if (nextId > 99) {
            sequenceCounters.get("warehouse").set(10);
            nextId = sequenceCounters.get("warehouse").incrementAndGet();
        }
        return "W" + String.format("%02d", nextId);
    }

    /**
     * 为出入库记录实体生成ID (R开头的6位数字)
     */
    public static String nextRecordId() {
        int nextId = sequenceCounters.get("record").incrementAndGet();
        if (nextId > 999999) {
            sequenceCounters.get("record").set(100000);
            nextId = sequenceCounters.get("record").incrementAndGet();
        }
        return "R" + String.format("%06d", nextId);
    }
}