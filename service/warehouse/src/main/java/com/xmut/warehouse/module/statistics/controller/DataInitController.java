package com.xmut.warehouse.module.statistics.controller;

import com.xmut.warehouse.common.result.R;
import com.xmut.warehouse.module.user.entity.XmutUser;
import com.xmut.warehouse.module.user.service.XmutUserService;
import com.xmut.warehouse.module.warehouse.entity.XmutWarehouse;
import com.xmut.warehouse.module.warehouse.service.XmutWarehouseService;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import com.xmut.warehouse.module.goods.service.XmutGoodsService;
import com.xmut.warehouse.module.enterprise.entity.XmutEnterprise;
import com.xmut.warehouse.module.enterprise.service.XmutEnterpriseService;
import com.xmut.warehouse.module.user.entity.UserMenuPermission;
import com.xmut.warehouse.module.user.entity.UserWarehousePermission;
import com.xmut.warehouse.module.user.service.UserMenuPermissionService;
import com.xmut.warehouse.module.user.service.UserWarehousePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 数据初始化控制器，用于初始化系统基础数据
 */
@RestController
@RequestMapping("/api/data-init")
public class DataInitController {

    @Autowired
    private XmutUserService userService;

    @Autowired
    private XmutWarehouseService warehouseService;

    @Autowired
    private XmutGoodsService goodsService;

    @Autowired
    private XmutEnterpriseService enterpriseService;

    @Autowired
    private DataSource dataSource;

    /**
     * 初始化系统基础数据
     */
    @GetMapping("/init-data")
    public R<?> initSystemData() {
        try {
            // 检查并创建权限相关表
            initPermissionTables();
            
            // 检查并创建企业信息（单企业系统）
            initEnterprise();
            
            // 检查并创建仓库数据
            initWarehouses();
            
            // 检查并创建用户数据
            initUsers();
            
            // 检查并创建商品数据
            initGoods();
            
            // 初始化超级管理员权限
            initSuperAdminPermissions();
            
            // 初始化普通用户权限
            initNormalUserPermissions();
            
            return R.success("数据初始化完成");
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("数据初始化失败: " + e.getMessage());
        }
    }

    /**
     * 初始化权限相关表
     */
    private void initPermissionTables() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        // 创建用户-菜单权限关联表
        String createUserMenuPermissionTable = 
            "CREATE TABLE IF NOT EXISTS `xmut_user_menu_permission` (" +
            "  `id` varchar(32) NOT NULL COMMENT '主键ID，使用UUID'," +
            "  `user_id` varchar(32) NOT NULL COMMENT '用户ID，关联xmut_user表'," +
            "  `menu_code` varchar(50) NOT NULL COMMENT '菜单编码，如 user_management, warehouse_management 等'," +
            "  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "  PRIMARY KEY (`id`)," +
            "  KEY `idx_user_id` (`user_id`)," +
            "  KEY `idx_menu_code` (`menu_code`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-菜单权限关联表'";
        
        // 创建用户-仓库权限关联表
        String createUserWarehousePermissionTable = 
            "CREATE TABLE IF NOT EXISTS `xmut_user_warehouse_permission` (" +
            "  `id` varchar(32) NOT NULL COMMENT '主键ID，使用UUID'," +
            "  `user_id` varchar(32) NOT NULL COMMENT '用户ID，关联xmut_user表'," +
            "  `warehouse_id` varchar(32) NOT NULL COMMENT '仓库ID，关联xmut_warehouse表'," +
            "  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "  PRIMARY KEY (`id`)," +
            "  KEY `idx_user_id` (`user_id`)," +
            "  KEY `idx_warehouse_id` (`warehouse_id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-仓库权限关联表'";
        
        try {
            jdbcTemplate.execute(createUserMenuPermissionTable);
            System.out.println("用户-菜单权限表创建完成或已存在");
            
            jdbcTemplate.execute(createUserWarehousePermissionTable);
            System.out.println("用户-仓库权限表创建完成或已存在");
        } catch (Exception e) {
            System.err.println("创建权限表时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化企业信息
     */
    private void initEnterprise() {
        // 检查是否已有企业数据
        long count = enterpriseService.count();
        if (count == 0) {
            // 创建默认企业信息
            XmutEnterprise enterprise = new XmutEnterprise();
            enterprise.setName("默认企业");
            enterprise.setAddress("默认地址");
            enterprise.setContact("默认联系人");
            enterprise.setPhone("默认联系电话");
            // XmutEnterprise实体类中没有email字段，所以不设置
            enterprise.setRemark("系统初始化创建的企业信息");
            enterprise.setUpdateTime(new Date());
            enterpriseService.save(enterprise);
            System.out.println("已创建默认企业信息");
        } else {
            System.out.println("企业信息已存在，跳过初始化");
        }
    }

    /**
     * 初始化仓库数据
     */
    private void initWarehouses() {
        // 检查是否已有仓库数据
        long count = warehouseService.count();
        if (count == 0) {
            // 创建默认仓库
            XmutWarehouse warehouse1 = new XmutWarehouse();
            warehouse1.setName("主仓库");
            warehouse1.setAddress("北京市朝阳区仓库路1号");
            warehouse1.setContact("张三");
            warehouse1.setPhone("13800138001");
            warehouseService.save(warehouse1);

            XmutWarehouse warehouse2 = new XmutWarehouse();
            warehouse2.setName("辅仓库");
            warehouse2.setAddress("上海市浦东新区仓库路2号");
            warehouse2.setContact("李四");
            warehouse2.setPhone("13800138002");
            warehouseService.save(warehouse2);

            System.out.println("已创建默认仓库数据");
        } else {
            System.out.println("仓库数据已存在，跳过初始化");
        }
    }

    /**
     * 初始化用户数据
     */
    private void initUsers() {
        // 检查是否已有用户数据
        long count = userService.count();
        if (count == 0) {
            // 创建超级管理员
            XmutUser admin = new XmutUser();
            admin.setUsername("admin");
            admin.setNickname("超级管理员");
            // 密码使用BCrypt加密，这里使用默认密码"admin123"
            admin.setPassword("$2a$10$u34ZIin5JfYeKJsrE/wTC.3N5XLLDksitNQC73Q5W.0Hc2589LkIu"); // admin123的BCrypt加密
            admin.setRole(1); // 超级管理员
            admin.setStatus(1); // 启用
            admin.setPhone("13800138000");
            admin.setEmail("admin@example.com");
            admin.setCreateTime(new Date());
            userService.save(admin);

            System.out.println("已创建超级管理员账号");
        } else {
            System.out.println("用户数据已存在，跳过初始化");
        }
    }

    /**
     * 初始化商品数据
     */
    private void initGoods() {
        // 检查是否已有商品数据
        long count = goodsService.count();
        if (count == 0) {
            // 创建默认商品
            XmutGoods goods = new XmutGoods();
            goods.setId("1"); // 手动设置ID
            goods.setName("示例商品");
            goods.setPrice(100.0);
            goods.setCategoryId("1"); // 示例分类ID
            goods.setPic("示例图片地址");
            goods.setStatus(1); // 上架状态
            goods.setRemark("这是一个示例商品");
            goods.setCreateTime(new Date());
            goodsService.save(goods);

            System.out.println("已创建示例商品");
        } else {
            System.out.println("商品数据已存在，跳过初始化");
        }
    }

    /**
     * 初始化超级管理员权限
     */
    private void initSuperAdminPermissions() {
        // 获取所有超级管理员
        long count = userService.count();
        if (count > 0) {
            // 获取所有用户
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<XmutUser> userWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<XmutUser>()
                    .eq("role", 1); // 超级管理员角色
            List<XmutUser> superAdmins = userService.list(userWrapper);
            
            if (!superAdmins.isEmpty()) {
                // 定义所有菜单权限编码
                String[] menuCodes = {
                    "user_management", "permission_management", "warehouse_management", 
                    "inventory_overview", "goods_management", "goods_list", "goods_category", 
                    "in_out_management", "in_out_record", "statistics_management", 
                    "statistics_overview", "enterprise_management", "enterprise_info"
                };
                
                for (XmutUser admin : superAdmins) {
                    // 为超级管理员添加所有菜单权限
                    for (String menuCode : menuCodes) {
                        // 检查权限是否已存在
                        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserMenuPermission> wrapper =
                            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserMenuPermission>()
                                .eq("user_id", admin.getId())
                                .eq("menu_code", menuCode);
                        
                        long permissionCount = userMenuPermissionService.count(wrapper);
                        if (permissionCount == 0) {
                            // 添加菜单权限
                            UserMenuPermission userMenuPermission = new UserMenuPermission();
                            userMenuPermission.setUserId(admin.getId());
                            userMenuPermission.setMenuCode(menuCode);
                            userMenuPermissionService.save(userMenuPermission);
                        }
                    }
                    
                    // 为超级管理员添加所有仓库权限
                    List<XmutWarehouse> warehouses = warehouseService.list();
                    for (XmutWarehouse warehouse : warehouses) {
                        // 检查仓库权限是否已存在
                        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserWarehousePermission> wrapper = 
                            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserWarehousePermission>()
                                .eq("user_id", admin.getId())
                                .eq("warehouse_id", warehouse.getId());
                        
                        long permissionCount = userWarehousePermissionService.count(wrapper);
                        if (permissionCount == 0) {
                            // 添加仓库权限
                            UserWarehousePermission userWarehousePermission = new UserWarehousePermission();
                            userWarehousePermission.setUserId(admin.getId());
                            userWarehousePermission.setWarehouseId(warehouse.getId());
                            userWarehousePermissionService.save(userWarehousePermission);
                        }
                    }
                }
                
                System.out.println("超级管理员权限初始化完成");
            } else {
                System.out.println("未找到超级管理员账号，跳过权限初始化");
            }
        } else {
            System.out.println("用户数据尚未初始化，跳过权限初始化");
        }
    }
    
    /**
     * 初始化普通用户权限
     */
    private void initNormalUserPermissions() {
        // 获取所有非超级管理员用户
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<XmutUser> userWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<XmutUser>()
                .ne("role", 1); // 非超级管理员
        List<XmutUser> normalUsers = userService.list(userWrapper);
        
        if (!normalUsers.isEmpty()) {
            for (XmutUser user : normalUsers) {
                // 为普通用户添加dashboard权限（首页访问权限）
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserMenuPermission> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserMenuPermission>()
                        .eq("user_id", user.getId())
                        .eq("menu_code", "dashboard");
                
                long permissionCount = userMenuPermissionService.count(wrapper);
                if (permissionCount == 0) {
                    UserMenuPermission userMenuPermission = new UserMenuPermission();
                    userMenuPermission.setUserId(user.getId());
                    userMenuPermission.setMenuCode("dashboard");
                    userMenuPermissionService.save(userMenuPermission);
                }
            }
            
            System.out.println("普通用户权限初始化完成");
        } else {
            System.out.println("未找到普通用户，跳过普通用户权限初始化");
        }
    }
    
    // 添加必要的依赖注入
    @Autowired
    private UserMenuPermissionService userMenuPermissionService;
    
    @Autowired
    private UserWarehousePermissionService userWarehousePermissionService;
}