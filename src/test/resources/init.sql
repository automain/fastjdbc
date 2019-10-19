CREATE DATABASE `automain`;

USE `automain`;

DROP TABLE IF EXISTS `test`;

CREATE TABLE `test` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gid` char(36) NOT NULL COMMENT '测试GID',
  `create_time` int(10) unsigned NOT NULL COMMENT '创建时间',
  `update_time` int(10) unsigned NOT NULL COMMENT '更新时间',
  `is_valid` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '是否有效(0:否,1:是)',
  `money` decimal(10,2) unsigned DEFAULT NULL COMMENT '金额',
  `remark` varchar(256) DEFAULT NULL COMMENT '备注',
  `test_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '测试名称',
  `test_dictionary` int(11) NOT NULL DEFAULT '0' COMMENT '测试字典(0:字典0,1:字典1,2:字典2)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gid` (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;