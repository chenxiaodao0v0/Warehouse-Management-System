package com.xmut.warehouse.module.enterprise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;
import com.xmut.warehouse.common.result.R;

public interface XmutEnterpriseService extends IService<XmutEnterprise> {
    // 查询企业信息（单企业，返回唯一一条）
    R<?> getEnterpriseInfo();
    // 修改企业信息
    R<?> updateEnterprise(XmutEnterprise enterprise);
}
