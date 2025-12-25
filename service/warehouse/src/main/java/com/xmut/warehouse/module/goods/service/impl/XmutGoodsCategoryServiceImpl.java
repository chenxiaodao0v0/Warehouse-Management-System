package com.xmut.warehouse.module.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoodsCategory;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsCategoryMapper;
import com.xmut.warehouse.module.goods.service.XmutGoodsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 商品分类Service实现类
 */
@Service
public class XmutGoodsCategoryServiceImpl extends ServiceImpl<XmutGoodsCategoryMapper, XmutGoodsCategory> implements XmutGoodsCategoryService {

    @Autowired
    private XmutGoodsCategoryMapper xmutGoodsCategoryMapper;

    @Override
    public R<?> addCategory(XmutGoodsCategory category) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<XmutGoodsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(XmutGoodsCategory::getName, category.getName());
        if (xmutGoodsCategoryMapper.selectCount(wrapper) > 0) {
            return R.fail("分类名称已存在");
        }

        // 设置创建时间
        category.setCreateTime(new Date());

        // 保存分类
        xmutGoodsCategoryMapper.insert(category);
        return R.success("添加成功");
    }

    @Override
    public R<?> updateCategory(XmutGoodsCategory category) {
        // 检查分类是否存在
        XmutGoodsCategory existingCategory = xmutGoodsCategoryMapper.selectById(category.getId());
        if (existingCategory == null) {
            return R.fail("分类不存在");
        }

        // 检查分类名称是否与其他分类重复
        LambdaQueryWrapper<XmutGoodsCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(XmutGoodsCategory::getName, category.getName());
        wrapper.ne(XmutGoodsCategory::getId, category.getId()); // 排除当前分类
        if (xmutGoodsCategoryMapper.selectCount(wrapper) > 0) {
            return R.fail("分类名称已存在");
        }

        // 更新分类
        xmutGoodsCategoryMapper.updateById(category);
        return R.success("更新成功");
    }

    @Override
    public R<?> deleteCategory(String id) {
        // 检查分类是否存在
        XmutGoodsCategory existingCategory = xmutGoodsCategoryMapper.selectById(id);
        if (existingCategory == null) {
            return R.fail("分类不存在");
        }

        // 检查是否有商品使用该分类
        // TODO: 在实际应用中，可能需要检查是否有商品使用该分类，如果有则不允许删除
        // 这里先简单实现删除功能

        // 删除分类
        xmutGoodsCategoryMapper.deleteById(id);
        return R.success("删除成功");
    }
}