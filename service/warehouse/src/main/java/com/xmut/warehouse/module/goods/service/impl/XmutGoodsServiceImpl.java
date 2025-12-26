package com.xmut.warehouse.module.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.generator.IdGenerator;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 商品基础信息Service实现类（仅负责商品基础信息CRUD，无仓库/库存操作）
 */
@Service
public class XmutGoodsServiceImpl extends ServiceImpl<XmutGoodsMapper, XmutGoods> implements XmutGoodsService {

    // ========== 保留：商品分页查询（仅按商品名称模糊查询，无仓库条件） ==========
    @Override
    public R<IPage<XmutGoods>> getGoodsPage(Page<XmutGoods> page, String goodsName) {
        LambdaQueryWrapper<XmutGoods> queryWrapper = new LambdaQueryWrapper<>();
        // 仅按商品名称模糊查询（移除原有按warehouse_id查询的条件）
        if (StringUtils.hasText(goodsName)) {
            queryWrapper.like(XmutGoods::getName, goodsName);
        }
        queryWrapper.orderByDesc(XmutGoods::getCreateTime);
        IPage<XmutGoods> goodsIPage = this.baseMapper.selectPage(page, queryWrapper);
        return R.success(goodsIPage);
    }

    // ========== 保留：新增商品（仅保存商品基础信息，无仓库/库存字段） ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> addGoods(XmutGoods xmutGoods) {
        // 参数校验（仅校验商品基础信息必填项）
        if (!StringUtils.hasText(xmutGoods.getName())) {
            return R.fail("商品名称不能为空");
        }
        if (xmutGoods.getPrice() == null || xmutGoods.getPrice() < 0) {
            return R.fail("商品价格不能为负数");
        }
        if (!StringUtils.hasText(xmutGoods.getCategoryId())) {
            return R.fail("商品类别ID不能为空");
        }
        // 手动设置ID
        if (!StringUtils.hasText(xmutGoods.getId())) {
            xmutGoods.setId(IdGenerator.nextGoodsId());
        }
        // 设置创建时间（若未手动设置）
        if (xmutGoods.getCreateTime() == null) {
            xmutGoods.setCreateTime(new Date());
        }
        // 保存商品（仅基础信息，无warehouse_id和stock）
        boolean saveSuccess = this.save(xmutGoods);
        if (saveSuccess) {
            return R.success("商品新增成功");
        } else {
            return R.fail("商品新增失败");
        }
    }

    // ========== 保留：修改商品（仅更新商品基础信息，无仓库/库存字段） ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> updateGoods(XmutGoods xmutGoods) {
        // 参数校验
        if (!StringUtils.hasText(xmutGoods.getId())) {
            return R.fail("商品ID不能为空");
        }
        if (!StringUtils.hasText(xmutGoods.getName())) {
            return R.fail("商品名称不能为空");
        }
        if (xmutGoods.getPrice() == null || xmutGoods.getPrice() < 0) {
            return R.fail("商品价格不能为负数");
        }
        if (!StringUtils.hasText(xmutGoods.getCategoryId())) {
            return R.fail("商品类别ID不能为空");
        }
        // 更新商品（仅基础信息，不涉及warehouse_id和stock）
        boolean updateSuccess = this.updateById(xmutGoods);
        if (updateSuccess) {
            return R.success("商品修改成功");
        } else {
            return R.fail("商品修改失败");
        }
    }

    // ========== 保留：删除商品（物理删除，级联删除仓库-商品关联记录） ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> deleteGoods(String goodsId) {
        if (!StringUtils.hasText(goodsId)) {
            return R.fail("商品ID不能为空");
        }
        // 删除商品（外键级联删除xmut_warehouse_goods中的关联记录）
        boolean deleteSuccess = this.removeById(goodsId);
        if (deleteSuccess) {
            return R.success("商品删除成功");
        } else {
            return R.fail("商品删除失败");
        }
    }

    @Override
    public R<?> batchDeleteGoods(List<String> ids) {
        return null;
    }

    @Override
    public R<List<XmutGoods>> getGoodsByWarehouseId(String warehouseId) {
        return null;
    }

    @Override
    public R<List<XmutGoods>> getGoodsByCategoryId(String categoryId) {
        return null;
    }

    @Override
    public R<String> uploadGoodsImg(MultipartFile file) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }

        // 校验文件类型（只允许图片格式）
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return R.fail("文件名不能为空");
        }

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (!Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp").contains(suffix.toLowerCase())) {
            return R.fail("只允许上传jpg、jpeg、png、gif、webp格式的图片");
        }

        try {
            // 生成唯一文件名（使用UUID）
            String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            
            // 设置图片保存路径（可以根据需要修改）
            String filePath = System.getProperty("user.dir") + "/upload/images/" + newFileName;
            
            // 确保目录存在
            File uploadDir = new File(System.getProperty("user.dir") + "/upload/images/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            File targetFile = new File(filePath);
            file.transferTo(targetFile);
            
            // 返回访问路径（可以根据需要修改访问路径）
            String fileUrl = "/upload/images/" + newFileName;
            return R.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return R.fail("图片上传失败：" + e.getMessage());
        }
    }

    // ========== 保留：根据ID查询商品基础信息 ==========
    @Override
    public R<XmutGoods> getGoodsById(String goodsId) {
        if (!StringUtils.hasText(goodsId)) {
            return R.fail("商品ID不能为空");
        }
        XmutGoods xmutGoods = this.getById(goodsId);
        if (xmutGoods != null) {
            return R.success(xmutGoods);
        } else {
            return R.fail("该商品不存在");
        }
    }

    @Override
    public R<List<XmutGoods>> getGoodsByNameLike(String goodsName) {
        // 参数校验：关键词不能为空
        if (!StringUtils.hasText(goodsName)) {
            return R.fail("商品名称关键词不能为空");
        }
        LambdaQueryWrapper<XmutGoods> wrapper = new LambdaQueryWrapper<>();
        // 核心：模糊匹配（like），前后都加%，支持任意位置匹配（比如输"泉水"能搜到"矿泉水"）
        wrapper.like(XmutGoods::getName, goodsName);
        // 按创建时间倒序，最新商品在前
        wrapper.orderByDesc(XmutGoods::getCreateTime);
        List<XmutGoods> goodsList = this.baseMapper.selectList(wrapper);
        return R.success(goodsList);
    }

    // ========== 关键：删除原有涉及warehouse_id和stock的方法 ==========
    // 例如：原有的「按仓库ID查询商品」「更新商品库存」等方法，全部删除
    // 这些逻辑已迁移到 WarehouseGoodsService 中，无需在此保留
}