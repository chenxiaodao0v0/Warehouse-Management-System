package com.xmut.warehouse.module.inOutRecord.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import com.xmut.warehouse.module.inOutRecord.service.InOutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
