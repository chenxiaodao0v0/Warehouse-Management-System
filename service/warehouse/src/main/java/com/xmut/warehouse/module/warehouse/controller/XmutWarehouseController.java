package com.xmut.warehouse.module.warehouse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import com.xmut.warehouse.module.warehouse.service.XmutWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库Controller（与用户/企业Controller格式一致，接口前缀统一为/api/warehouse）
 */
@RestController
@RequestMapping("/api/warehouse") // 接口统一前缀，与用户(/api/user)、企业(/api/enterprise)格式一致
public class XmutWarehouseController {

    @Autowired
    private XmutWarehouseService xmutWarehouseService;

    /**
     * 分页查询仓库列表（前端分页组件直接适配）
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/warehouse/page?pageNum=1&pageSize=10&warehouseName=测试仓库
     */
    @GetMapping("/page")
    public R<IPage<XmutWarehouse>> getWarehousePage(
            @RequestParam(defaultValue = "1") Integer pageNum, // 默认页码1
            @RequestParam(defaultValue = "10") Integer pageSize, // 默认每页10条
            @RequestParam(required = false) String warehouseName // 仓库名称模糊查询，可选
    ) {
        Page<XmutWarehouse> page = new Page<>(pageNum, pageSize);
        return xmutWarehouseService.getWarehousePage(page, warehouseName);
    }

    /**
     * 根据ID查询仓库详情
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/warehouse/你的仓库ID
     */
    @GetMapping("/{id}")
    public R<XmutWarehouse> getWarehouseById(@PathVariable String id) {
        return xmutWarehouseService.getWarehouseById(id);
    }

    /**
     * 新增仓库
     * 请求方式：POST
     * 测试地址：http://localhost:8080/api/warehouse/add
     * 请求体：JSON格式（与XmutWarehouse实体字段一致）
     */
    @PostMapping("/add")
    public R<?> addWarehouse(@RequestBody XmutWarehouse warehouse) {
        return xmutWarehouseService.addWarehouse(warehouse);
    }

    /**
     * 修改仓库
     * 请求方式：PUT
     * 测试地址：http://localhost:8080/api/warehouse/update
     * 请求体：JSON格式（含仓库ID，其他字段按需修改）
     */
    @PutMapping("/update")
    public R<?> updateWarehouse(@RequestBody XmutWarehouse warehouse) {
        return xmutWarehouseService.updateWarehouse(warehouse);
    }

    /**
     * 单个删除仓库
     * 请求方式：DELETE
     * 测试地址：http://localhost:8080/api/warehouse/你的仓库ID
     */
    @DeleteMapping("/{id}")
    public R<?> deleteWarehouse(@PathVariable String id) {
        return xmutWarehouseService.deleteWarehouse(id);
    }

    /**
     * 批量删除仓库
     * 请求方式：DELETE
     * 测试地址：http://localhost:8080/api/warehouse/batch
     * 请求体：JSON数组格式（如["仓库ID1","仓库ID2"]）
     */
    @DeleteMapping("/batch")
    public R<?> batchDeleteWarehouse(@RequestBody List<String> ids) {
        return xmutWarehouseService.batchDeleteWarehouse(ids);
    }
}
