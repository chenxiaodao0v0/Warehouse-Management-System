package com.xmut.warehouse.module.goods.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.service.XmutGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商品Controller（与企业、仓库Controller格式一致，接口前缀/api/goods）
 */
@RestController
@RequestMapping("/api/goods")
public class XmutGoodsController {

    @Autowired
    private XmutGoodsService xmutGoodsService;

    /**
     * 分页查询商品列表
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/goods/page?pageNum=1&pageSize=10&goodsName=测试商品
     */
    @GetMapping("/page")
    public R<IPage<XmutGoods>> getGoodsPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String goodsName
    ) {
        Page<XmutGoods> page = new Page<>(pageNum, pageSize);
        return xmutGoodsService.getGoodsPage(page, goodsName);
    }

    /**
     * 根据ID查询商品详情
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/goods/你的商品ID
     */
    @GetMapping("/{id}")
    public R<XmutGoods> getGoodsById(@PathVariable String id) {
        return xmutGoodsService.getGoodsById(id);
    }

    /**
     * 新增商品
     * 请求方式：POST
     * 测试地址：http://localhost:8080/api/goods/add
     * 请求体：JSON格式（与XmutGoods实体字段一致）
     */
    @PostMapping("/add")
    public R<?> addGoods(@RequestBody XmutGoods goods) {
        return xmutGoodsService.addGoods(goods);
    }

    /**
     * 修改商品
     * 请求方式：PUT
     * 测试地址：http://localhost:8080/api/goods/update
     * 请求体：JSON格式（含商品ID，其他字段按需修改）
     */
    @PutMapping("/update")
    public R<?> updateGoods(@RequestBody XmutGoods goods) {
        return xmutGoodsService.updateGoods(goods);
    }

    /**
     * 单个删除商品
     * 请求方式：DELETE
     * 测试地址：http://localhost:8080/api/goods/你的商品ID
     */
    @DeleteMapping("/{id}")
    public R<?> deleteGoods(@PathVariable String id) {
        return xmutGoodsService.deleteGoods(id);
    }

    /**
     * 批量删除商品
     * 请求方式：DELETE
     * 测试地址：http://localhost:8080/api/goods/batch
     * 请求体：JSON数组（如["商品ID1","商品ID2"]）
     */
    @DeleteMapping("/batch")
    public R<?> batchDeleteGoods(@RequestBody List<String> ids) {
        return xmutGoodsService.batchDeleteGoods(ids);
    }

    /**
     * 根据仓库ID查询商品列表
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/goods/warehouse/你的仓库ID
     */
    @GetMapping("/warehouse/{warehouseId}")
    public R<List<XmutGoods>> getGoodsByWarehouseId(@PathVariable String warehouseId) {
        return xmutGoodsService.getGoodsByWarehouseId(warehouseId);
    }

    /**
     * 根据类别ID查询商品列表
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/goods/category/你的类别ID
     */
    @GetMapping("/category/{categoryId}")
    public R<List<XmutGoods>> getGoodsByCategoryId(@PathVariable String categoryId) {
        return xmutGoodsService.getGoodsByCategoryId(categoryId);
    }

    /**
     * 商品图片上传
     * 请求方式：POST
     * 测试地址：http://localhost:8080/api/goods/upload
     * 请求方式：form-data，参数名：file，值：选择图片文件
     */
    @PostMapping("/upload")
    public R<String> uploadGoodsImg(@RequestParam("file") MultipartFile file) {
        return xmutGoodsService.uploadGoodsImg(file);
    }

    /**
     * 商品名称模糊查询
     * 接口：GET /api/goods/name?goodsName=矿泉水
     * 说明：支持模糊匹配，输“泉水”“矿泉”都能搜到
     */
    @GetMapping("/name")
    public R<List<XmutGoods>> getGoodsByNameLike(@RequestParam String goodsName) {
        return xmutGoodsService.getGoodsByNameLike(goodsName);
    }
}
