package com.xmut.warehouse.module.enterprise.controller;

import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;
import com.xmut.warehouse.module.enterprise.service.XmutEnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enterprise")
public class XmutEnterpriseController {

    @Autowired
    private XmutEnterpriseService enterpriseService;

    // 查询企业信息
    @GetMapping("/info")
    public R<?> getInfo() {
        return enterpriseService.getEnterpriseInfo();
    }

    // 修改企业信息
    @PutMapping("/update")
    public R<?> update(@RequestBody XmutEnterprise enterprise) {
        return enterpriseService.updateEnterprise(enterprise);
    }
    
    // 初始化企业信息
    @PostMapping("/init")
    public R<?> init(@RequestBody XmutEnterprise enterprise) {
        return enterpriseService.initEnterpriseIfNotExists(enterprise);
    }
}