package com.xmut.warehouse.module.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoodsCategory;

/**
 * 商品分类Service接口
 */
public interface XmutGoodsCategoryService extends IService<XmutGoodsCategory> {
    // 新增商品分类
    R<?> addCategory(XmutGoodsCategory category);

    // 修改商品分类
    R<?> updateCategory(XmutGoodsCategory category);

    // 删除商品分类
    R<?> deleteCategory(String id);
}