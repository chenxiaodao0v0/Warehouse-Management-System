package com.xmut.warehouse.module.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.generator.IdGenerator;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsMapper; // 货品Mapper（你后续会创建，暂时保留无需修改）
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import com.xmut.warehouse.module.warehouse.mapper.XmutWarehouseMapper;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.warehouse.service.XmutWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 仓库Service实现类（与用户/企业Service实现类格式一致，可直接复制）
 */
@Service
public class XmutWarehouseServiceImpl extends ServiceImpl<XmutWarehouseMapper, XmutWarehouse> implements XmutWarehouseService {

    /**
     * 注入货品Mapper（用于校验仓库是否关联货品，你后续会创建货品模块后，该类会自动存在）
     * 若暂时未创建货品模块，删除该注入及关联校验逻辑即可（不影响其他功能）
     */
    @Autowired
    private XmutGoodsMapper xmutGoodsMapper;

    @Override
    public R<IPage<XmutWarehouse>> getWarehousePage(Page<XmutWarehouse> page, String warehouseName) {
        // 构造查询条件，与企业模块查询逻辑一致
        LambdaQueryWrapper<XmutWarehouse> queryWrapper = new LambdaQueryWrapper<>();
        // 仓库名称模糊查询
        if (StringUtils.hasText(warehouseName)) {
            queryWrapper.like(XmutWarehouse::getName, warehouseName);
        }

        // 分页查询（MyBatis-Plus自带，你的项目已配置分页插件，无需额外处理）
        IPage<XmutWarehouse> warehouseIPage = this.baseMapper.selectPage(page, queryWrapper);
        return R.success(warehouseIPage);
    }

    @Override
    public R<XmutWarehouse> getWarehouseById(String id) {
        // 参数校验（与企业模块一致）
        if (!StringUtils.hasText(id)) {
            return R.fail("仓库ID不能为空");
        }
        XmutWarehouse warehouse = this.baseMapper.selectById(id);
        if (warehouse == null) {
            return R.fail("该仓库不存在");
        }
        return R.success(warehouse);
    }

    @Override
    public R<?> addWarehouse(XmutWarehouse warehouse) {
        // 必传参数校验（与企业模块一致）
        if (!StringUtils.hasText(warehouse.getName())) {
            return R.fail("仓库名称不能为空");
        }
        // 校验仓库名称在该企业下是否唯一（避免重复）
        LambdaQueryWrapper<XmutWarehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XmutWarehouse::getName, warehouse.getName());
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            return R.fail("该企业下已存在同名仓库，请勿重复添加");
        }
        // 手动设置ID
        if (!StringUtils.hasText(warehouse.getId())) {
            warehouse.setId(IdGenerator.nextWarehouseId());
        }
        // 设置默认值（与用户/企业模块一致）
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(1); // 默认启用
        }
        warehouse.setCreateTime(new Date());
        // 新增仓库（MyBatis-Plus自带save方法）
        boolean saveSuccess = this.save(warehouse);
        return saveSuccess ? R.success("仓库新增成功") : R.fail("仓库新增失败");
    }

    @Override
    public R<?> updateWarehouse(XmutWarehouse warehouse) {
        // 参数校验（与企业模块一致）
        if (!StringUtils.hasText(warehouse.getId())) {
            return R.fail("仓库ID不能为空");
        }

        if (!StringUtils.hasText(warehouse.getName())) {
            return R.fail("仓库名称不能为空");
        }
        // 校验仓库是否存在
        XmutWarehouse existWarehouse = this.baseMapper.selectById(warehouse.getId());
        if (existWarehouse == null) {
            return R.fail("该仓库不存在，无法修改");
        }
        // 校验仓库名称唯一性（排除当前仓库ID）
        LambdaQueryWrapper<XmutWarehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XmutWarehouse::getName, warehouse.getName())
                .ne(XmutWarehouse::getId, warehouse.getId());
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            return R.fail("该企业下已存在同名仓库，请勿重复修改");
        }

        // 修改仓库（MyBatis-Plus自带updateById方法）
        boolean updateSuccess = this.updateById(warehouse);
        return updateSuccess ? R.success("仓库修改成功") : R.fail("仓库修改失败");
    }

    @Override
    public R<?> deleteWarehouse(String id) {
        // 参数校验
        if (!StringUtils.hasText(id)) {
            return R.fail("仓库ID不能为空");
        }
        // 校验仓库是否存在
        XmutWarehouse existWarehouse = this.baseMapper.selectById(id);
        if (existWarehouse == null) {
            return R.fail("该仓库不存在，无法删除");
        }
        // 关联校验：校验该仓库是否绑定货品（若绑定则禁止删除，后续货品模块创建后生效）
        // 注意：由于商品实体本身不直接包含仓库ID，这里需要根据实际数据库表结构进行调整
        // 可能需要查询中间表xmut_warehouse_goods来确定仓库是否关联商品
        // 以下是示例代码，具体实现需要根据实际的中间表结构调整
        // long goodsCount = 0; // 暂时设为0，因为需要实际的中间表查询逻辑
        
        // 如果有中间表查询逻辑，可以类似这样：
        // long goodsCount = warehouseGoodsMapper.countByWarehouseId(id);
        
        // 由于当前代码逻辑错误，我们暂时移除错误的查询
        // 实际的中间表查询需要根据具体的表结构实现
        long goodsCount = 0; // 临时设置为0，需要根据实际的中间表实现

        if (goodsCount > 0) {
            return R.fail("该仓库已关联" + goodsCount + "个货品，无法删除，请先删除关联货品");
        }
        // 删除仓库（MyBatis-Plus自带removeById方法）
        boolean deleteSuccess = this.removeById(id);
        return deleteSuccess ? R.success("仓库删除成功") : R.fail("仓库删除失败");
    }

    @Transactional(rollbackFor = Exception.class) // 事务注解：批量删除失败时回滚，与企业模块一致
    @Override
    public R<?> batchDeleteWarehouse(List<String> ids) {
        // 参数校验
        if (ids == null || ids.isEmpty()) {
            return R.fail("请选择需要删除的仓库");
        }
        // 批量校验仓库是否绑定货品
        for (String id : ids) {
            // 由于原查询逻辑错误，暂时注释掉，实际需要根据中间表实现
            // long goodsCount = xmutGoodsMapper.selectCount(new LambdaQueryWrapper<com.xmut.warehouse.module.goods.entity.XmutGoods>()
            //         .eq(com.xmut.warehouse.module.goods.entity.XmutGoods::getId, id));
            
            long goodsCount = 0; // 临时设置，需要根据实际中间表实现
            
            if (goodsCount > 0) {
                XmutWarehouse warehouse = this.baseMapper.selectById(id);
                return R.fail("仓库【" + warehouse.getName() + "】已关联" + goodsCount + "个货品，无法批量删除");
            }
        }
        // 批量删除仓库（MyBatis-Plus自带removeByIds方法）
        boolean deleteSuccess = this.removeByIds(ids);
        return deleteSuccess ? R.success("批量删除仓库成功") : R.fail("批量删除仓库失败");
    }
}