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

-- 发货地址表
CREATE TABLE `shipping_address` (
  `address_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint unsigned NOT NULL COMMENT '所属用户ID',
  `shipper_name` varchar(50) NOT NULL COMMENT '发货人姓名',
  `region` varchar(100) NOT NULL COMMENT '所在地区',
  `detail` varchar(200) NOT NULL COMMENT '详细地址',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址（0-否 1-是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`address_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_shipping_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发货地址表';
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

-- 交易方式表（多选回收方式）
-- ----------------------------
CREATE TABLE `recycle_method` (
  `method_id` tinyint unsigned NOT NULL AUTO_INCREMENT COMMENT '方式ID',
  `method_name` varchar(20) NOT NULL COMMENT '方式名称（如：上门回收）',
  `method_desc` varchar(255) NOT NULL COMMENT '方式描述',
  PRIMARY KEY (`method_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收方式表';

-- 订单主表
CREATE TABLE `order` (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(30) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '买家用户ID',
    seller_id BIGINT NOT NULL COMMENT '卖家用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    order_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    payment_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    order_status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态(0待付款 1待发货 2待收货 3已完成 4已取消 5退款中 6已退款)',
    payment_type TINYINT COMMENT '支付方式(1在线支付 2线下交易)',
    payment_time DATETIME COMMENT '支付时间',
    delivery_type TINYINT COMMENT '配送方式(1自提 2快递)',
    delivery_fee DECIMAL(10,2) DEFAULT 0 COMMENT '运费',
    delivery_time DATETIME COMMENT '发货时间',
    received_time DATETIME COMMENT '收货时间',
    remark VARCHAR(255) COMMENT '订单备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_seller (seller_id),
    INDEX idx_product (product_id),
    INDEX idx_order_no (order_no)
) COMMENT '订单主表';

-- 订单收货地址表
CREATE TABLE order_address (
    address_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(30) COMMENT '省份',
    city VARCHAR(30) COMMENT '城市',
    district VARCHAR(30) COMMENT '区/县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order (order_id)
) COMMENT '订单收货地址表';

-- 订单状态变更记录表
CREATE TABLE order_status_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    previous_status TINYINT COMMENT '前一状态',
    current_status TINYINT NOT NULL COMMENT '当前状态',
    operator_id BIGINT COMMENT '操作人ID',
    operator_type TINYINT DEFAULT 1 COMMENT '操作人类型(1买家 2卖家 3系统 4管理员)',
    remark VARCHAR(255) COMMENT '变更备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order (order_id)
) COMMENT '订单状态变更记录表';

-- 订单评价表
CREATE TABLE order_review (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '评价用户ID',
    seller_id BIGINT NOT NULL COMMENT '卖家用户ID',
    content TEXT COMMENT '评价内容',
    rating TINYINT NOT NULL DEFAULT 5 COMMENT '评分(1-5)',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名(0否 1是)',
    has_reply TINYINT DEFAULT 0 COMMENT '是否回复(0否 1是)',
    reply_content TEXT COMMENT '回复内容',
    reply_time DATETIME COMMENT '回复时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    INDEX idx_order (order_id),
    INDEX idx_product (product_id),
    INDEX idx_seller (seller_id)
) COMMENT '订单评价表';

-- 订单消息表
CREATE TABLE order_message (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读(0未读 1已读)',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    INDEX idx_order (order_id),
    INDEX idx_sender (sender_id),
    INDEX idx_receiver (receiver_id)
) COMMENT '订单消息表';

-- 退款申请表
CREATE TABLE refund_apply (
    refund_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退款申请ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '申请用户ID',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    refund_reason TINYINT NOT NULL COMMENT '退款原因(1质量问题 2描述不符 3卖家未发货 4不想要了 5其他)',
    refund_reason_detail VARCHAR(500) COMMENT '退款原因详情',
    refund_status TINYINT DEFAULT 0 COMMENT '退款状态(0处理中 1已同意 2已拒绝)',
    admin_remark VARCHAR(255) COMMENT '管理员处理备注',
    processed_time DATETIME COMMENT '处理时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order (order_id),
    INDEX idx_user (user_id)
) COMMENT '退款申请表';

-- 交易流水表
CREATE TABLE transaction_record (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '交易ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    transaction_type TINYINT NOT NULL COMMENT '交易类型(1支付 2退款)',
    transaction_no VARCHAR(50) COMMENT '第三方交易流水号',
    amount DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    status TINYINT DEFAULT 0 COMMENT '交易状态(0处理中 1成功 2失败)',
    payment_channel TINYINT COMMENT '支付渠道(1支付宝 2微信 3银行卡)',
    remark VARCHAR(255) COMMENT '交易备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order (order_id),
    INDEX idx_user (user_id)
) COMMENT '交易流水表';

-- 发布商品位置表
CREATE TABLE release_location (
    id INT AUTO_INCREMENT PRIMARY KEY,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL
);

-- 钱包账户表
CREATE TABLE `wallet_account` (
                                  `account_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '钱包账户ID（唯一主键）',
                                  `user_id` bigint unsigned NOT NULL COMMENT '所属用户ID，关联user表的user_id',
                                  `balance` decimal(12,2) DEFAULT '0.00' COMMENT '钱包余额',
                                  `frozen_balance` decimal(12,2) DEFAULT '0.00' COMMENT '冻结余额，如在交易处理中被冻结的金额',
                                  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                                  PRIMARY KEY (`account_id`),
                                  UNIQUE KEY `uniq_user` (`user_id`),
                                  CONSTRAINT `fk_wallet_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包账户表';

-- 钱包交易记录表
CREATE TABLE `wallet_transaction` (
                                      `transaction_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '交易ID（唯一主键）',
                                      `account_id` bigint unsigned NOT NULL COMMENT '钱包账户ID，关联wallet_account表的account_id',
                                      `transaction_type` tinyint NOT NULL COMMENT '交易类型(1充值 2消费 3退款 4其他)',
                                      `transaction_amount` decimal(12,2) NOT NULL COMMENT '交易金额',
                                      `before_balance` decimal(12,2) NOT NULL COMMENT '交易前余额',
                                      `after_balance` decimal(12,2) NOT NULL COMMENT '交易后余额',
                                      `transaction_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
                                      `remark` varchar(255) DEFAULT NULL COMMENT '交易备注',
                                      PRIMARY KEY (`transaction_id`),
                                      KEY `idx_account` (`account_id`),
                                      CONSTRAINT `fk_wallet_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `wallet_account` (`account_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包交易记录表';

-- 支付密码表
CREATE TABLE `payment_password` (
                                    `password_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '支付密码记录ID（唯一主键）',
                                    `user_id` bigint unsigned NOT NULL COMMENT '所属用户ID，关联user表的user_id',
                                    `hashed_payment_password` varchar(255) NOT NULL COMMENT '经过加密处理后的支付密码，使用如bcrypt、argon2等算法加密',
                                    `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新支付密码的时间',
                                    PRIMARY KEY (`password_id`),
                                    UNIQUE KEY `uniq_user` (`user_id`),
                                    CONSTRAINT `fk_payment_password_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='存储用户支付密码信息的表';

-- 验证码记录表
CREATE TABLE `verification_code_record` (
                                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID（唯一主键）',
                                            `phone_number` varchar(20) NOT NULL COMMENT '接收验证码的手机号码',
                                            `verification_code` varchar(6) NOT NULL COMMENT '发送的验证码，一般为6位数字',
                                            `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '验证码发送时间',
                                            `expiration_time` datetime NOT NULL COMMENT '验证码过期时间，根据业务设定合理的过期时长',
                                            `is_used` tinyint(1) NOT NULL DEFAULT 0 COMMENT '验证码是否已使用，0表示未使用，1表示已使用',
                                            `attempt_count` int NOT NULL DEFAULT 0 COMMENT '验证码验证尝试次数',
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='存储手机验证码发送记录的表';

-- 验证码验证历史表
CREATE TABLE `verification_code_verification_history` (
                                                          `history_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '验证历史记录ID（唯一主键）',
                                                          `phone_number` varchar(20) NOT NULL COMMENT '进行验证码验证的手机号码',
                                                          `verification_code` varchar(6) NOT NULL COMMENT '用于验证的验证码',
                                                          `verification_result` tinyint(1) NOT NULL COMMENT '验证结果，0表示验证失败，1表示验证成功',
                                                          `verification_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '验证码验证时间',
                                                          `related_record_id` bigint unsigned COMMENT '关联的验证码记录表中的id，方便追溯原始验证码发送记录',
                                                          PRIMARY KEY (`history_id`),
                                                          KEY `idx_phone_number` (`phone_number`),
                                                          KEY `idx_related_record_id` (`related_record_id`),
                                                          CONSTRAINT `fk_verification_history_record` FOREIGN KEY (`related_record_id`) REFERENCES `verification_code_record` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='存储手机验证码验证历史记录的表';

-- 用户反馈表
CREATE TABLE `user_feedback` (
  `feedback_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '反馈ID（唯一主键）',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID，关联user表的user_id',
  `feedback_type` tinyint NOT NULL COMMENT '反馈类型(1功能建议 2体验问题 3商品相关 4物流相关 5其他)',
  `feedback_title` varchar(100) NOT NULL COMMENT '反馈标题',
  `feedback_content` text NOT NULL COMMENT '反馈内容详情',
  `contact_info` varchar(100) DEFAULT NULL COMMENT '联系方式（电话或邮箱，可选）',
  `images` varchar(1000) DEFAULT NULL COMMENT '反馈附带的图片URL，多个URL使用逗号分隔',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '处理状态(0未处理 1处理中 2已处理)',
  `priority_level` tinyint DEFAULT 0 COMMENT '优先级(0普通 1重要 2紧急)',
  `admin_id` bigint unsigned DEFAULT NULL COMMENT '处理人员ID，关联user表中role为系统管理员的user_id',
  `admin_reply` text DEFAULT NULL COMMENT '管理员回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '反馈提交时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`feedback_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_time` (`created_time`),
  CONSTRAINT `fk_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户反馈表';