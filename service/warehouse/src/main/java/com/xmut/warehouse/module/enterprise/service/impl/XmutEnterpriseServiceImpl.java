package com.xmut.warehouse.module.enterprise.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;
import com.xmut.warehouse.module.enterprise.mapper.XmutEnterpriseMapper;
import com.xmut.warehouse.module.enterprise.service.XmutEnterpriseService;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class XmutEnterpriseServiceImpl extends ServiceImpl<XmutEnterpriseMapper, XmutEnterprise> implements XmutEnterpriseService {

    @Override
    public R<?> getEnterpriseInfo() {
        // 单企业场景，查询第一条数据（实际数据库只有一条）
        XmutEnterprise enterprise = this.list().get(0);
        return R.success(enterprise);
    }

    @Override
    public R<?> updateEnterprise(XmutEnterprise enterprise) {
        // 更新最后修改时间
        enterprise.setUpdateTime(new Date());
        boolean success = this.updateById(enterprise);
        return success ? R.success("修改成功") : R.fail("修改失败");
    }
}
