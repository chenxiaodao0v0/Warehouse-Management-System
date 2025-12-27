package com.xmut.warehouse.module.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.generator.IdSequenceGenerator;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.goods.mapper.XmutGoodsMapper; // 货品Mapper（你后续会创建，暂时保留无需修改）
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import com.xmut.warehouse.module.warehouse.mapper.XmutWarehouseMapper;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.warehouse.service.XmutWarehouseService;
import com.xmut.warehouse.module.inOutRecord.entity.InOutRecord;
import com.xmut.warehouse.module.inOutRecord.mapper.InOutRecordMapper;
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
    
    @Autowired
    private InOutRecordMapper inOutRecordMapper;

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
        // 使用统一的 save 方法处理 ID 生成和默认值
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
    @Transactional(rollbackFor = Exception.class)
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
        // 关联校验：校验该仓库是否有出入库记录（若存在则禁止删除）
        long recordCount = inOutRecordMapper.selectCount(new LambdaQueryWrapper<InOutRecord>()
                .eq(InOutRecord::getWarehouseId, id));
        if (recordCount > 0) {
            return R.fail("该仓库已关联" + recordCount + "条出入库记录，无法删除，请先删除关联的出入库记录");
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
        // 批量校验仓库是否有关联的出入库记录
        for (String id : ids) {
            long recordCount = inOutRecordMapper.selectCount(new LambdaQueryWrapper<InOutRecord>()
                    .eq(InOutRecord::getWarehouseId, id));
            if (recordCount > 0) {
                XmutWarehouse warehouse = this.baseMapper.selectById(id);
                return R.fail("仓库【" + warehouse.getName() + "】已关联" + recordCount + "条出入库记录，无法删除");
            }
        }
        // 批量删除仓库（MyBatis-Plus自带removeByIds方法）
        boolean deleteSuccess = this.removeByIds(ids);
        return deleteSuccess ? R.success("批量删除仓库成功") : R.fail("批量删除仓库失败");
    }

    @Override
    public boolean save(XmutWarehouse warehouse) {
        // 生成ID
        warehouse.setId(IdSequenceGenerator.nextWarehouseId());
        // 设置创建时间
        warehouse.setCreateTime(new Date());
        // 保存到数据库
        return super.save(warehouse);
    }

    @Override
    public boolean updateById(XmutWarehouse warehouse) {
        // 更新时不修改ID和创建时间
        warehouse.setCreateTime(null);
        // 更新数据库
        return super.updateById(warehouse);
    }
}