package com.xmut.warehouse.module.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsMapper;
import com.xmut.warehouse.module.goods.service.XmutGoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 商品Service实现类（与企业、仓库Service实现类格式一致，可直接复用）
 */
@Service
public class XmutGoodsServiceImpl extends ServiceImpl<XmutGoodsMapper, XmutGoods> implements XmutGoodsService {

    /**
     * 商品图片上传根路径（本地存储，可根据你的项目路径调整）
     * 前端访问路径：http://localhost:8080/upload/goods/xxx.png
     */
    private static final String UPLOAD_PATH = System.getProperty("user.dir") + "/src/main/resources/static/upload/goods/";

    @Override
    public R<IPage<XmutGoods>> getGoodsPage(Page<XmutGoods> page, String goodsName) {
        // 构造查询条件，与仓库、企业模块一致
        LambdaQueryWrapper<XmutGoods> queryWrapper = new LambdaQueryWrapper<>();
        // 商品名称模糊查询
        if (StringUtils.hasText(goodsName)) {
            queryWrapper.like(XmutGoods::getGoodsName, goodsName);
        }
        // 按更新时间倒序排序
        queryWrapper.orderByDesc(XmutGoods::getUpdateTime);
        // 分页查询
        IPage<XmutGoods> goodsIPage = this.baseMapper.selectPage(page, queryWrapper);
        return R.success(goodsIPage);
    }

    @Override
    public R<XmutGoods> getGoodsById(String id) {
        // 参数校验
        if (!StringUtils.hasText(id)) {
            return R.fail("商品ID不能为空");
        }
        XmutGoods goods = this.baseMapper.selectById(id);
        if (goods == null) {
            return R.fail("该商品不存在");
        }
        return R.success(goods);
    }

    @Override
    public R<?> addGoods(XmutGoods goods) {
        // 必传参数校验
        if (!StringUtils.hasText(goods.getGoodsName())) {
            return R.fail("商品名称不能为空");
        }
        if (!StringUtils.hasText(goods.getCategoryId())) {
            return R.fail("货品类别ID不能为空");
        }
        if (!StringUtils.hasText(goods.getWarehouseId())) {
            return R.fail("仓库ID不能为空");
        }
        // 校验：类别ID+仓库ID+商品名称 组合唯一，避免重复商品
        LambdaQueryWrapper<XmutGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XmutGoods::getCategoryId, goods.getCategoryId())
                .eq(XmutGoods::getWarehouseId, goods.getWarehouseId())
                .eq(XmutGoods::getGoodsName, goods.getGoodsName());
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            return R.fail("该仓库该类别下已存在同名商品，请勿重复添加");
        }
        // 设置默认值
        if (goods.getStatus() == null) {
            goods.setStatus(1); // 默认启用
        }
        if (goods.getStock() == null) {
            goods.setStock(0); // 默认库存0
        }
        if (goods.getPrice() == null) {
            goods.setPrice(new BigDecimal("0.00")); // 默认单价0.00
        }
        goods.setCreateTime(new Date());
        goods.setUpdateTime(new Date());
        // 新增商品
        boolean saveSuccess = this.save(goods);
        return saveSuccess ? R.success("商品新增成功") : R.fail("商品新增失败");
    }

    @Override
    public R<?> updateGoods(XmutGoods goods) {
        // 必传参数校验
        if (!StringUtils.hasText(goods.getId())) {
            return R.fail("商品ID不能为空");
        }
        if (!StringUtils.hasText(goods.getGoodsName())) {
            return R.fail("商品名称不能为空");
        }
        if (!StringUtils.hasText(goods.getCategoryId())) {
            return R.fail("货品类别ID不能为空");
        }
        if (!StringUtils.hasText(goods.getWarehouseId())) {
            return R.fail("仓库ID不能为空");
        }
        // 校验商品是否存在
        XmutGoods existGoods = this.baseMapper.selectById(goods.getId());
        if (existGoods == null) {
            return R.fail("该商品不存在，无法修改");
        }
        // 校验：类别ID+仓库ID+商品名称 组合唯一（排除当前商品ID）
        LambdaQueryWrapper<XmutGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XmutGoods::getCategoryId, goods.getCategoryId())
                .eq(XmutGoods::getWarehouseId, goods.getWarehouseId())
                .eq(XmutGoods::getGoodsName, goods.getGoodsName())
                .ne(XmutGoods::getId, goods.getId());
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            return R.fail("该仓库该类别下已存在同名商品，请勿重复修改");
        }
        // 设置更新时间
        goods.setUpdateTime(new Date());
        // 修改商品
        boolean updateSuccess = this.updateById(goods);
        return updateSuccess ? R.success("商品修改成功") : R.fail("商品修改失败");
    }

    @Override
    public R<?> deleteGoods(String id) {
        // 参数校验
        if (!StringUtils.hasText(id)) {
            return R.fail("商品ID不能为空");
        }
        // 校验商品是否存在
        XmutGoods existGoods = this.baseMapper.selectById(id);
        if (existGoods == null) {
            return R.fail("该商品不存在，无法删除");
        }
        // 【可选】后续开发出入库模块后，添加出入库关联校验
        // long inOutCount = xmutInOutMapper.selectCount(new LambdaQueryWrapper<>().eq(XmutInOut::getGoodsId, id));
        // if (inOutCount > 0) { return R.fail("该商品已关联出入库记录，无法删除"); }

        // 删除商品
        boolean deleteSuccess = this.removeById(id);
        return deleteSuccess ? R.success("商品删除成功") : R.fail("商品删除失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<?> batchDeleteGoods(List<String> ids) {
        // 参数校验
        if (ids == null || ids.isEmpty()) {
            return R.fail("请选择需要删除的商品");
        }
        // 批量校验商品是否存在（可选，可省略）
        for (String id : ids) {
            XmutGoods existGoods = this.baseMapper.selectById(id);
            if (existGoods == null) {
                return R.fail("商品ID【" + id + "】不存在，无法批量删除");
            }
        }
        // 批量删除商品
        boolean deleteSuccess = this.removeByIds(ids);
        return deleteSuccess ? R.success("批量删除商品成功") : R.fail("批量删除商品失败");
    }

    @Override
    public R<List<XmutGoods>> getGoodsByWarehouseId(String warehouseId) {
        if (!StringUtils.hasText(warehouseId)) {
            return R.fail("仓库ID不能为空");
        }
        List<XmutGoods> goodsList = this.baseMapper.selectByWarehouseId(warehouseId);
        return R.success(goodsList);
    }

    @Override
    public R<List<XmutGoods>> getGoodsByCategoryId(String categoryId) {
        if (!StringUtils.hasText(categoryId)) {
            return R.fail("类别ID不能为空");
        }
        List<XmutGoods> goodsList = this.baseMapper.selectByCategoryId(categoryId);
        return R.success(goodsList);
    }

    @Override
    public R<String> uploadGoodsImg(MultipartFile file) {
        // 1. 校验文件是否为空
        if (file == null || file.isEmpty()) {
            return R.fail("请选择要上传的商品图片");
        }
        // 2. 校验文件格式（仅允许jpg、png、jpeg）
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!".jpg".equalsIgnoreCase(suffix) && !".png".equalsIgnoreCase(suffix) && !".jpeg".equalsIgnoreCase(suffix)) {
            return R.fail("仅支持jpg、png、jpeg格式的图片");
        }
        // 3. 创建上传目录（若不存在则自动创建）
        File uploadDir = new File(UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        // 4. 生成唯一文件名（避免重名覆盖）
        String fileName = UUID.randomUUID().toString() + suffix;
        File destFile = new File(UPLOAD_PATH + fileName);
        // 5. 上传文件到本地
        try {
            file.transferTo(destFile);
            // 6. 返回图片访问URL（前端可直接存储到goodsImg字段）
            String imgUrl = "/upload/goods/" + fileName;
            return R.success(imgUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return R.fail("商品图片上传失败");
        }
    }
}
