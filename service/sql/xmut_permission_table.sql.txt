-- 用户-菜单权限关联表
CREATE TABLE IF NOT EXISTS `xmut_user_menu_permission` (
  `id` varchar(32) NOT NULL COMMENT '主键ID，使用UUID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID，关联xmut_user表',
  `menu_code` varchar(50) NOT NULL COMMENT '菜单编码，如 user_management, warehouse_management 等',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_menu_code` (`menu_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-菜单权限关联表';

-- 用户-仓库权限关联表
CREATE TABLE IF NOT EXISTS `xmut_user_warehouse_permission` (
  `id` varchar(32) NOT NULL COMMENT '主键ID，使用UUID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID，关联xmut_user表',
  `warehouse_id` varchar(32) NOT NULL COMMENT '仓库ID，关联xmut_warehouse表',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-仓库权限关联表';

-- 为超级管理员添加默认权限（可选）
-- 插入超级管理员的所有菜单权限
INSERT INTO `xmut_user_menu_permission` (`id`, `user_id`, `menu_code`) 
SELECT 
  UUID(), 
  u.id, 
  m.menu_code
FROM 
  (SELECT 'user_management' as menu_code 
   UNION SELECT 'permission_management'
   UNION SELECT 'warehouse_management' 
   UNION SELECT 'inventory_overview'
   UNION SELECT 'goods_management'
   UNION SELECT 'goods_list'
   UNION SELECT 'goods_category'
   UNION SELECT 'in_out_management'
   UNION SELECT 'in_out_record'
   UNION SELECT 'statistics_management'
   UNION SELECT 'statistics_overview'
   UNION SELECT 'enterprise_management'
   UNION SELECT 'enterprise_info') m
CROSS JOIN `xmut_user` u
WHERE u.role = 1; -- 超级管理员角色