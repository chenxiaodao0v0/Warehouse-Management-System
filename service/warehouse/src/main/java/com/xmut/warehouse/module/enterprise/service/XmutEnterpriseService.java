package com.xmut.warehouse.module.enterprise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;

public interface XmutEnterpriseService extends IService<XmutEnterprise> {

    /**
     * 获取企业信息
     */
    R<?> getEnterpriseInfo();

    /**
     * 更新企业信息
     */
    R<?> updateEnterprise(XmutEnterprise enterprise);

    /**
     * 初始化企业信息（仅在没有企业信息时）
     */
    R<?> initEnterpriseIfNotExists(XmutEnterprise enterprise);
}