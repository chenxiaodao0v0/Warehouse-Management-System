-- 创建ID序列表
CREATE TABLE IF NOT EXISTS `xmut_id_sequence` (
  `seq_name` VARCHAR(50) NOT NULL COMMENT '序列名称',
  `current_value` BIGINT NOT NULL DEFAULT 1 COMMENT '当前序列值',
  `prefix` VARCHAR(10) NOT NULL COMMENT '序列前缀',
  `digits` INT NOT NULL COMMENT '序列值的位数',
  PRIMARY KEY (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ID序列表';

-- 插入初始序列数据
INSERT INTO `xmut_id_sequence` (`seq_name`, `current_value`, `prefix`, `digits`) VALUES
('goods', 1000, 'G', 4),
('warehouse', 10, 'W', 2),
('record', 100000, 'R', 6)
ON DUPLICATE KEY UPDATE
  `current_value` = `current_value`,
  `prefix` = `prefix`,
  `digits` = `digits`;