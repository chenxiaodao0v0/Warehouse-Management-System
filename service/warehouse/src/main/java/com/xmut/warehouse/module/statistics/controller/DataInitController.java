package com.xmut.warehouse.module.statistics.controller;

import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;
import com.xmut.warehouse.module.enterprise.service.XmutEnterpriseService;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.service.XmutGoodsService;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import com.xmut.warehouse.module.warehouse.service.XmutWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 数据初始化控制器（仅用于开发环境添加测试数据）
 */
@RestController
@RequestMapping("/api/init")
public class DataInitController {

    @Autowired
    private XmutGoodsService goodsService;

    @Autowired
    private XmutWarehouseService warehouseService;

    @Autowired
    private XmutEnterpriseService enterpriseService;

    /**
     * 初始化测试数据
     */
    @GetMapping("/test-data")
    public R initTestData() {
        try {
            // 检查是否已经有数据
            long goodsCount = goodsService.count();
            long warehouseCount = warehouseService.count();
            long enterpriseCount = enterpriseService.count();

            if (goodsCount > 0) {
                return R.fail("商品表中已有数据，无法初始化测试数据");
            }

            // 添加测试企业数据（如果不存在）
            if (enterpriseCount == 0) {
                XmutEnterprise enterprise = new XmutEnterprise();
                enterprise.setName("测试企业");
                enterprise.setAddress("测试地址");
                enterprise.setContact("测试联系人");
                enterprise.setPhone("13800138000");
                enterprise.setRemark("测试企业备注");
                enterpriseService.save(enterprise);
            }

            // 添加测试仓库数据（如果不存在）
            if (warehouseCount == 0) {
                XmutWarehouse warehouse = new XmutWarehouse();
                warehouse.setId("W01");
                warehouse.setName("测试仓库1");
                warehouse.setAddress("测试地址1");
                warehouse.setContact("测试联系人1");
                warehouse.setPhone("13800138001");
                warehouse.setStatus(1);
                warehouse.setEnterpriseId(enterpriseService.getOne(null).getId());
                warehouse.setCreateTime(new Date());
                warehouseService.save(warehouse);

                XmutWarehouse warehouse2 = new XmutWarehouse();
                warehouse2.setId("W02");
                warehouse2.setName("测试仓库2");
                warehouse2.setAddress("测试地址2");
                warehouse2.setContact("测试联系人2");
                warehouse2.setPhone("13800138002");
                warehouse2.setStatus(1);
                warehouse2.setEnterpriseId(enterpriseService.getOne(null).getId());
                warehouse2.setCreateTime(new Date());
                warehouseService.save(warehouse2);
            }

            // 添加测试商品数据
            for (int i = 1; i <= 5; i++) {
                XmutGoods goods = new XmutGoods();
                goods.setId("G00" + i);
                goods.setName("测试商品" + i);
                goods.setPrice(10.0 + i);
                goods.setCategoryId("C001");
                goods.setPic("");
                goods.setStatus(1);
                goods.setRemark("测试商品备注" + i);
                goods.setCreateTime(new Date());
                goodsService.save(goods);
            }

            return R.success("测试数据初始化成功");
        } catch (Exception e) {
            return R.fail("测试数据初始化失败: " + e.getMessage());
        }
    }
}