package com.xmut.warehouse.module.goods.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商品Service接口（与企业、仓库Service格式一致）
 */
public interface XmutGoodsService extends IService<XmutGoods> {
    // 分页查询商品列表（支持商品名称模糊查询）
    R<IPage<XmutGoods>> getGoodsPage(Page<XmutGoods> page, String goodsName);

    // 根据ID查询商品详情
    R<XmutGoods> getGoodsById(String id);

    // 新增商品
    R<?> addGoods(XmutGoods goods);

    // 修改商品
    R<?> updateGoods(XmutGoods goods);

    // 单个删除商品
    R<?> deleteGoods(String id);

    // 批量删除商品
    R<?> batchDeleteGoods(List<String> ids);

    // 自定义：根据仓库ID查询商品
    R<List<XmutGoods>> getGoodsByWarehouseId(String warehouseId);

    // 自定义：根据类别ID查询商品
    R<List<XmutGoods>> getGoodsByCategoryId(String categoryId);

    // 商品图片上传（返回图片URL）
    R<String> uploadGoodsImg(MultipartFile file);

    R<List<XmutGoods>> getGoodsByNameLike(String goodsName);


}
