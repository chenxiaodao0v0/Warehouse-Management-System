package com.xmut.warehouse.module.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoodsCategory;
import com.xmut.warehouse.module.goods.service.XmutGoodsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类Controller
 */
@RestController
@RequestMapping("/api/goods/category")
public class XmutGoodsCategoryController {

    @Autowired
    private XmutGoodsCategoryService xmutGoodsCategoryService;

    /**
     * 获取所有商品分类列表
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/goods/category/list
     */
    @GetMapping("/list")
    public R<List<XmutGoodsCategory>> getCategoryList() {
        LambdaQueryWrapper<XmutGoodsCategory> wrapper = new LambdaQueryWrapper<>();
        List<XmutGoodsCategory> categories = xmutGoodsCategoryService.list(wrapper);
        return R.success(categories);
    }

    /**
     * 分页查询商品分类
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/goods/category/page?pageNum=1&pageSize=10&name=分类名称
     */
    @GetMapping("/page")
    public R<Page<XmutGoodsCategory>> getCategoryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name
    ) {
        Page<XmutGoodsCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<XmutGoodsCategory> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(XmutGoodsCategory::getName, name);
        }
        Page<XmutGoodsCategory> result = xmutGoodsCategoryService.page(page, wrapper);
        return R.success(result);
    }

    /**
     * 根据ID查询商品分类详情
     * 请求方式：GET
     * 测试地址：http://localhost:8080/api/goods/category/你的分类ID
     */
    @GetMapping("/{id}")
    public R<XmutGoodsCategory> getCategoryById(@PathVariable String id) {
        XmutGoodsCategory category = xmutGoodsCategoryService.getById(id);
        if (category != null) {
            return R.success(category);
        } else {
            return R.fail("分类不存在");
        }
    }

    /**
     * 新增商品分类
     * 请求方式：POST
     * 测试地址：http://localhost:8080/api/goods/category/add
     */
    @PostMapping("/add")
    public R<?> addCategory(@RequestBody XmutGoodsCategory category) {
        return xmutGoodsCategoryService.addCategory(category);
    }

    /**
     * 修改商品分类
     * 请求方式：PUT
     * 测试地址：http://localhost:8080/api/goods/category/update
     */
    @PutMapping("/update")
    public R<?> updateCategory(@RequestBody XmutGoodsCategory category) {
        return xmutGoodsCategoryService.updateCategory(category);
    }

    /**
     * 删除商品分类
     * 请求方式：DELETE
     * 测试地址：http://localhost:8080/api/goods/category/你的分类ID
     */
    @DeleteMapping("/{id}")
    public R<?> deleteCategory(@PathVariable String id) {
        return xmutGoodsCategoryService.deleteCategory(id);
    }
}