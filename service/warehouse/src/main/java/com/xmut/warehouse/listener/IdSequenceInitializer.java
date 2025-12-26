package com.xmut.warehouse.listener;

import com.xmut.warehouse.common.entity.IdSequence;
import com.xmut.warehouse.common.service.IdSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ID序列初始化器
 */
@Component
public class IdSequenceInitializer implements ApplicationRunner {

    @Autowired
    private IdSequenceService idSequenceService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化商品ID序列 (G开头的4位数)
        IdSequence goodsSequence = new IdSequence();
        goodsSequence.setSeqName("goods");
        goodsSequence.setCurrentValue(1000L); // 初始值为1000
        goodsSequence.setPrefix("G");
        goodsSequence.setDigits(4);
        idSequenceService.initSequence(goodsSequence);

        // 初始化仓库ID序列 (W开头的2位数)
        IdSequence warehouseSequence = new IdSequence();
        warehouseSequence.setSeqName("warehouse");
        warehouseSequence.setCurrentValue(10L); // 初始值为10
        warehouseSequence.setPrefix("W");
        warehouseSequence.setDigits(2);
        idSequenceService.initSequence(warehouseSequence);

        // 初始化出入库记录ID序列 (R开头的6位数)
        IdSequence recordSequence = new IdSequence();
        recordSequence.setSeqName("record");
        recordSequence.setCurrentValue(100000L); // 初始值为100000
        recordSequence.setPrefix("R");
        recordSequence.setDigits(6);
        idSequenceService.initSequence(recordSequence);
    }
}