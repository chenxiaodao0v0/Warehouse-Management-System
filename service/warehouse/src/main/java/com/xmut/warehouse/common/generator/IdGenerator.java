package com.xmut.warehouse.common.generator;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义ID生成器，为不同实体生成不同格式的ID（基于原子计数器）
 */
public class IdGenerator implements IdentifierGenerator, ApplicationContextAware {

    // 商品ID计数器 (G开头的4位数字)
    private static final AtomicLong goodsCounter = new AtomicLong(1000);
    
    // 仓库ID计数器 (W开头的2位数字)
    private static final AtomicLong warehouseCounter = new AtomicLong(10);
    
    // 出入库记录ID计数器 (R开头的6位数字)
    private static final AtomicLong recordCounter = new AtomicLong(100000);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Number nextId(Object entity) {
        // 不使用数字ID，返回默认值
        return 1L;
    }

    /**
     * 覆盖此方法以支持字符串类型的主键生成
     */
    @Override
    public String nextUUID(Object entity) {
        return nextIdByEntity(entity);
    }

    /**
     * 为商品实体生成ID (G开头的4位数字)
     */
    public String nextGoodsId() {
        long value = goodsCounter.getAndIncrement();
        return "G" + String.format("%04d", value);
    }

    /**
     * 为仓库实体生成ID (W开头的2位数字)
     */
    public String nextWarehouseId() {
        long value = warehouseCounter.getAndIncrement();
        return "W" + String.format("%02d", value);
    }

    /**
     * 为出入库记录实体生成ID (R开头的6位数字)
     */
    public String nextRecordId() {
        long value = recordCounter.getAndIncrement();
        return "R" + String.format("%06d", value);
    }

    /**
     * 根据实体类型自动选择合适的ID生成策略
     */
    public String nextIdByEntity(Object entity) {
        if (entity instanceof XmutGoods) {
            return nextGoodsId();
        } else if (entity instanceof XmutWarehouse) {
            return nextWarehouseId();
        } else if (entity instanceof InOutRecord) {
            return nextRecordId();
        } else {
            // 对于未知类型，返回标准UUID
            return java.util.UUID.randomUUID().toString().replace("-", "");
        }
    }
}