-- 用户表
CREATE TABLE `user` (
  `user_id`    bigint unsigned NOT NULL AUTO_INCREMENT  COMMENT '用户ID（唯一主键）',
  `username`  varchar(50)     NOT NULL                 COMMENT '用户名（必须唯一）',
  `password`  varchar(100)    NOT NULL                 COMMENT '登录密码（明文存储）',
  `role`      enum('系统管理员','普通用户') 
               NOT NULL DEFAULT '普通用户'             COMMENT '用户角色（枚举类型）',
  `phone`     varchar(20)     DEFAULT NULL            COMMENT '手机号码（可选）',
  `email`     varchar(100)    DEFAULT NULL            COMMENT '电子邮箱（可选）',
  `avatar`    varchar(255)    DEFAULT 'default.png'   COMMENT '头像路径（默认图片）',
  `balance`   decimal(12,2)   DEFAULT 0.00            COMMENT '账户余额（默认0）',
  `is_locked` tinyint(1)      DEFAULT 0               COMMENT '账户状态（0-正常 1-锁定）',
  `create_time`  datetime        DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uniq_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户核心表';

-- 用户地址表
CREATE TABLE `user_address` (
  `address_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint unsigned NOT NULL COMMENT '所属用户ID',
  `consignee` varchar(50) NOT NULL COMMENT '收货人姓名',
  `region` varchar(100) NOT NULL COMMENT '所在地区',
  `detail` varchar(200) NOT NULL COMMENT '详细地址',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址（0-否 1-是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`address_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';