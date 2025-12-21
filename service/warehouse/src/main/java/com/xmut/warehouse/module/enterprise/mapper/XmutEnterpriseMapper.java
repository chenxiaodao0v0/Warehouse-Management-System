package com.xmut.warehouse.module.enterprise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XmutEnterpriseMapper extends BaseMapper<XmutEnterprise> {
    // 单企业场景，无需复杂查询，BaseMapper足够（selectById、updateById）
}
