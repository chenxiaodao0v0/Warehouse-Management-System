package com.xmut.warehouse.module.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;

import java.util.List;

/**
 * 仓库Service接口（与用户/企业Service格式一致，继承IService）
 */
public interface XmutWarehouseService extends IService<XmutWarehouse> {
    // 分页查询仓库列表（支持模糊查询）
    R<IPage<XmutWarehouse>> getWarehousePage(Page<XmutWarehouse> page, String warehouseName);

    // 根据ID查询仓库详情
    R<XmutWarehouse> getWarehouseById(String id);

    // 新增仓库
    R<?> addWarehouse(XmutWarehouse warehouse);

    // 修改仓库
    R<?> updateWarehouse(XmutWarehouse warehouse);

    // 单个删除仓库（含货品关联校验）
    R<?> deleteWarehouse(String id);

    // 批量删除仓库（含货品关联校验）
    R<?> batchDeleteWarehouse(List<String> ids);
}
