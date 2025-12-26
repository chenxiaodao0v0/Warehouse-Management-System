package com.xmut.warehouse.module.inOutRecord.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import com.xmut.warehouse.module.inOutRecord.service.InOutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 出入库及调货记录Controller（接口前缀：/api/inout，适配你的表结构）
 */
@RestController
@RequestMapping("/api/inout")
public class InOutRecordController {

    @Autowired
    private InOutRecordService inOutRecordService;

    /**
     * 分页查询出入库/调货记录
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/inout/page?pageNum=1&pageSize=10&contactPerson=张三
     */
    @GetMapping("/page")
    public R<IPage<InOutRecord>> getInOutRecordPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String contactPerson
    ) {
        Page<InOutRecord> page = new Page<>(pageNum, pageSize);
        return inOutRecordService.getInOutRecordPage(page, contactPerson);
    }

    /**
     * 商品入库
     * 请求方式：POST
     * 测试地址：http://localhost:8080/api/inout/in
     */
    @PostMapping("/in")
    public R<?> goodsInStock(@RequestBody InOutRecord inOutRecord) {
        return inOutRecordService.goodsInStock(inOutRecord);
    }

    /**
     * 商品出库
     * 请求方式：POST
     * 测试地址：http://localhost:8080/api/inout/out
     */
    @PostMapping("/out")
    public R<?> goodsOutStock(@RequestBody InOutRecord inOutRecord) {
        return inOutRecordService.goodsOutStock(inOutRecord);
    }

    /**
     * 商品调货
     * 请求方式：POST
     * 测试地址：http://localhost:8080/api/inout/transfer
     */
    @PostMapping("/transfer")
    public R<?> goodsTransfer(@RequestBody InOutRecord inOutRecord) {
        return inOutRecordService.goodsTransfer(inOutRecord);
    }

    /**
     * 根据ID查询出入库/调货记录详情
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/inout/你的记录ID
     */
    @GetMapping("/{id}")
    public R<InOutRecord> getInOutRecordById(@PathVariable String id) {
        InOutRecord inOutRecord = inOutRecordService.getById(id);
        if (inOutRecord == null) {
            return R.fail("出入库/调货记录不存在");
        }
        return R.success(inOutRecord);
    }

    /**
     * 按时间范围+仓库ID查出入库记录
     * 接口：GET /api/inout/record?startTime=2025-12-01 00:00:00&endTime=2025-12-31 23:59:59&warehouseId=W1
     * 说明：时间参数可选，仓库ID可选，支持组合查询（比如只传时间、只传仓库）
     */
    @GetMapping("/record")
    public R<List<InOutRecord>> getRecordByTimeAndWarehouse(
            @RequestParam(required = false) String startTimeStr,
            @RequestParam(required = false) String endTimeStr,
            @RequestParam(required = false) String warehouseId) {
        Date startTime = null;
        Date endTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (startTimeStr != null && !startTimeStr.trim().isEmpty()) {
                startTime = sdf.parse(startTimeStr);
            }
            if (endTimeStr != null && !endTimeStr.trim().isEmpty()) {
                endTime = sdf.parse(endTimeStr);
            }
        } catch (ParseException e) {
            return R.fail("日期格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
        }
        return inOutRecordService.getRecordByTimeAndWarehouse(startTime, endTime, warehouseId);
    }

    /**
     * 按商品ID查所有出入库/调货记录
     * 接口：GET /api/inout/record/goods/{goodsId}
     */
    @GetMapping("/record/goods/{goodsId}")
    public R<List<InOutRecord>> getRecordByGoodsId(@PathVariable String goodsId) {
        return inOutRecordService.getRecordByGoodsId(goodsId);
    }

    /**
     * 获取最近的出入库记录
     * 接口：GET /api/inout/recent?pageNum=1&pageSize=5
     */
    @GetMapping("/recent")
    public R<IPage<InOutRecord>> getRecentInOutRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        Page<InOutRecord> page = new Page<>(pageNum, pageSize);
        return inOutRecordService.getRecentInOutRecords(page);
    }
}