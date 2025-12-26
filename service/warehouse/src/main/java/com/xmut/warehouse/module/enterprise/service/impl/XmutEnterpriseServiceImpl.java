package com.xmut.warehouse.module.enterprise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;
import com.xmut.warehouse.module.enterprise.mapper.XmutEnterpriseMapper;
import com.xmut.warehouse.module.enterprise.service.XmutEnterpriseService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class XmutEnterpriseServiceImpl extends ServiceImpl<XmutEnterpriseMapper, XmutEnterprise> implements XmutEnterpriseService {

    @Override
    public R<?> getEnterpriseInfo() {
        // 单企业系统，查询唯一的企业信息
        LambdaQueryWrapper<XmutEnterprise> queryWrapper = new LambdaQueryWrapper<>();
        XmutEnterprise enterprise = this.getOne(queryWrapper);
        
        if (enterprise != null) {
            return R.success(enterprise);
        } else {
            // 如果没有企业信息，返回null
            return R.success(null);
        }
    }

    @Override
    public R<?> updateEnterprise(XmutEnterprise enterprise) {
        // 更新最后修改时间
        enterprise.setUpdateTime(new Date());
        boolean success = this.updateById(enterprise);
        return success ? R.success("修改成功") : R.fail("修改失败");
    }
    
    /**
     * 初始化企业信息（仅在没有企业信息时）
     */
    public R<?> initEnterpriseIfNotExists(XmutEnterprise enterprise) {
        // 检查是否已存在企业信息
        LambdaQueryWrapper<XmutEnterprise> queryWrapper = new LambdaQueryWrapper<>();
        XmutEnterprise existing = this.getOne(queryWrapper);
        
        if (existing != null) {
            return R.fail("企业信息已存在，无需初始化");
        }
        
        // 设置创建时间并保存
        enterprise.setUpdateTime(new Date());
        boolean success = this.save(enterprise);
        return success ? R.success("企业信息初始化成功") : R.fail("企业信息初始化失败");
    }
}