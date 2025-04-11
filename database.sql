-- 用户表
CREATE TABLE `user` (
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID（唯一主键）',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（必须唯一）',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码（明文存储）',
  `role` enum('系统管理员','普通用户') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '普通用户' COMMENT '用户角色（枚举类型）',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号码（可选）',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱（可选）',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT 'default.png' COMMENT '头像路径（默认图片）',
  `balance` decimal(12,2) DEFAULT '0.00' COMMENT '账户余额（默认0）',
  `is_locked` tinyint(1) DEFAULT '0' COMMENT '账户状态（0-正常 1-锁定）',
  `ban_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封禁原因（可选）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uniq_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户核心表';

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

-- 商品信息表
CREATE TABLE product (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    title VARCHAR(100) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品详细描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    original_price DECIMAL(10,2) COMMENT '物品原价',
    category_id INT NOT NULL COMMENT '商品分类ID',
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    condition_level TINYINT COMMENT '物品成色(1-10级)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '商品状态(0下架 1在售 2已售)',
    location VARCHAR(200) COMMENT '商品所在地',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category_id),
    INDEX idx_user (user_id)
) COMMENT '商品信息表';

-- 商品图片表
CREATE TABLE product_image (
    image_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    is_main TINYINT DEFAULT 0 COMMENT '是否主图(0否 1是)',
    sort_order INT DEFAULT 0 COMMENT '图片排序',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product (product_id)
) COMMENT '商品图片表';

-- 商品分类表
CREATE TABLE product_category (
    category_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    parent_id INT DEFAULT 0 COMMENT '父分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_parent (parent_id)
) COMMENT '商品分类表';

-- 商品收藏表
CREATE TABLE product_favorite (
    favorite_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_user_product (user_id, product_id)
) COMMENT '商品收藏表';

-- 商品举报表
CREATE TABLE product_report (
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '举报ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '举报用户ID',
    report_type TINYINT NOT NULL COMMENT '举报类型(1虚假商品 2违禁品 3侵权等)',
    report_content TEXT COMMENT '举报内容',
    status TINYINT DEFAULT 0 COMMENT '处理状态(0未处理 1已处理)',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    handle_time DATETIME COMMENT '处理时间',
    INDEX idx_product (product_id)
) COMMENT '商品举报表';