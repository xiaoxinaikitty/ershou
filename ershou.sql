/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80035 (8.0.35)
 Source Host           : localhost:3306
 Source Schema         : ershou

 Target Server Type    : MySQL
 Target Server Version : 80035 (8.0.35)
 File Encoding         : 65001

 Date: 12/05/2025 17:22:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
  `conversation_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `product_id` bigint NOT NULL COMMENT '关联商品ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '普通用户ID',
  `seller_id` bigint UNSIGNED NOT NULL COMMENT '卖家用户ID',
  `last_message_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后消息内容预览',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `user_unread_count` int NULL DEFAULT 0 COMMENT '用户未读消息数',
  `seller_unread_count` int NULL DEFAULT 0 COMMENT '卖家未读消息数',
  `status` tinyint NULL DEFAULT 1 COMMENT '会话状态(0已关闭 1活跃)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`conversation_id`) USING BTREE,
  UNIQUE INDEX `uniq_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_seller`(`seller_id` ASC) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE,
  INDEX `idx_updated_time`(`updated_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of conversation
-- ----------------------------
INSERT INTO `conversation` VALUES (1, 53, 3, 4, '哈喽', '2025-05-09 18:31:10', 0, 2, 1, '2025-05-09 18:11:33', '2025-05-09 18:31:10');

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `coupon_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `promotion_id` bigint UNSIGNED NOT NULL COMMENT '关联的营销活动ID',
  `coupon_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '优惠券名称',
  `coupon_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优惠券码',
  `discount_type` tinyint NOT NULL COMMENT '优惠类型(1固定金额 2折扣百分比)',
  `discount_value` decimal(10, 2) NOT NULL COMMENT '优惠值(固定金额或折扣百分比)',
  `min_purchase` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低消费金额',
  `quantity` int NULL DEFAULT NULL COMMENT '优惠券数量(NULL表示无限)',
  `issued_count` int NULL DEFAULT 0 COMMENT '已发放数量',
  `used_count` int NULL DEFAULT 0 COMMENT '已使用数量',
  `valid_days` int NULL DEFAULT NULL COMMENT '有效天数(从领取时开始计算)',
  `usage_limit_per_user` int NULL DEFAULT 1 COMMENT '每用户可领取数量限制',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`coupon_id`) USING BTREE,
  INDEX `idx_promotion`(`promotion_id` ASC) USING BTREE,
  CONSTRAINT `fk_coupon_promotion` FOREIGN KEY (`promotion_id`) REFERENCES `promotion` (`promotion_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '买家用户ID',
  `seller_id` bigint NOT NULL COMMENT '卖家用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `order_amount` decimal(10, 2) NOT NULL COMMENT '订单金额',
  `payment_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `order_status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态(0待付款 1待发货 2待收货 3已完成 4已取消 5退款中 6已退款)',
  `payment_type` tinyint NULL DEFAULT NULL COMMENT '支付方式(1在线支付 2线下交易)',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_type` tinyint NULL DEFAULT NULL COMMENT '配送方式(1自提 2快递)',
  `delivery_fee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '运费',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `received_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_seller`(`seller_id` ASC) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, '20250414191851403812', 3, 1, 4, 999.00, 999.00, 4, 1, NULL, 2, 15.00, NULL, NULL, '希望尽快发货', '2025-04-14 19:18:51', '2025-04-15 10:21:41');
INSERT INTO `order` VALUES (2, '20250414202440744002', 3, 1, 5, 2000.00, 6000.00, 1, 1, '2025-04-15 19:04:48', 2, 15.00, NULL, NULL, '希望尽快发货', '2025-04-14 20:24:40', '2025-04-15 19:04:48');
INSERT INTO `order` VALUES (3, '20250415113924199943', 3, 1, 5, 2000.00, 6000.00, 1, 1, '2025-04-15 11:58:43', 2, 15.00, NULL, NULL, '希望尽快发货', '2025-04-15 11:39:24', '2025-04-15 11:58:43');
INSERT INTO `order` VALUES (4, '20250415142358358020', 3, 1, 2, 7999.00, 6000.00, 1, 1, '2025-04-15 14:24:49', 2, 15.00, NULL, NULL, '希望尽快发货', '2025-04-15 14:23:58', '2025-04-15 14:24:48');
INSERT INTO `order` VALUES (5, '20250415145844414129', 3, 1, 2, 7999.00, 6000.00, 1, 1, '2025-04-15 14:59:21', 2, 15.00, NULL, NULL, '希望尽快发货', '2025-04-15 14:58:44', '2025-04-15 14:59:20');
INSERT INTO `order` VALUES (6, '20250415154518801421', 3, 1, 2, 7999.00, 6000.00, 0, 1, NULL, 2, 15.00, NULL, NULL, '希望尽快发货', '2025-04-15 15:45:18', '2025-04-15 15:45:18');
INSERT INTO `order` VALUES (7, '20250415183545326847', 3, 3, 6, 99.00, 6000.00, 0, 1, NULL, 2, 15.00, NULL, NULL, '希望尽快发货', '2025-04-15 18:35:45', '2025-04-15 18:35:45');
INSERT INTO `order` VALUES (8, '20250415185219730424', 3, 3, 8, 99.00, 6000.00, 0, 1, NULL, 2, 15.00, NULL, NULL, '希望尽快发货', '2025-04-15 18:52:19', '2025-04-15 18:52:19');
INSERT INTO `order` VALUES (9, '20250512125836691362', 3, 4, 53, 1.00, 1.00, 0, 1, NULL, 1, 0.00, NULL, NULL, '', '2025-05-12 12:58:36', '2025-05-12 12:58:36');

-- ----------------------------
-- Table structure for order_address
-- ----------------------------
DROP TABLE IF EXISTS `order_address`;
CREATE TABLE `order_address`  (
  `address_id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区/县',
  `detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`address_id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_address
-- ----------------------------
INSERT INTO `order_address` VALUES (1, 1, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号', '2025-04-14 19:18:51');
INSERT INTO `order_address` VALUES (2, 2, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号', '2025-04-14 20:24:40');
INSERT INTO `order_address` VALUES (3, 3, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号', '2025-04-15 11:39:24');
INSERT INTO `order_address` VALUES (4, 4, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号', '2025-04-15 14:23:58');
INSERT INTO `order_address` VALUES (5, 5, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号', '2025-04-15 14:58:44');
INSERT INTO `order_address` VALUES (6, 6, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号', '2025-04-15 15:45:18');
INSERT INTO `order_address` VALUES (7, 7, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号', '2025-04-15 18:35:45');
INSERT INTO `order_address` VALUES (8, 8, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号', '2025-04-15 18:52:19');
INSERT INTO `order_address` VALUES (9, 9, '在线支付', '13800000000', '在线支付', '在线支付', '在线支付', '在线支付无需地址', '2025-05-12 12:58:36');

-- ----------------------------
-- Table structure for order_message
-- ----------------------------
DROP TABLE IF EXISTS `order_message`;
CREATE TABLE `order_message`  (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读(0未读 1已读)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_sender`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver`(`receiver_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_message
-- ----------------------------

-- ----------------------------
-- Table structure for order_review
-- ----------------------------
DROP TABLE IF EXISTS `order_review`;
CREATE TABLE `order_review`  (
  `review_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '评价用户ID',
  `seller_id` bigint NOT NULL COMMENT '卖家用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `rating` tinyint NOT NULL DEFAULT 5 COMMENT '评分(1-5)',
  `is_anonymous` tinyint NULL DEFAULT 0 COMMENT '是否匿名(0否 1是)',
  `has_reply` tinyint NULL DEFAULT 0 COMMENT '是否回复(0否 1是)',
  `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '回复内容',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`review_id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE,
  INDEX `idx_seller`(`seller_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_review
-- ----------------------------

-- ----------------------------
-- Table structure for order_status_history
-- ----------------------------
DROP TABLE IF EXISTS `order_status_history`;
CREATE TABLE `order_status_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `previous_status` tinyint NULL DEFAULT NULL COMMENT '前一状态',
  `current_status` tinyint NOT NULL COMMENT '当前状态',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operator_type` tinyint NULL DEFAULT 1 COMMENT '操作人类型(1买家 2卖家 3系统 4管理员)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变更备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单状态变更记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_status_history
-- ----------------------------
INSERT INTO `order_status_history` VALUES (1, 1, NULL, 0, 3, 1, '创建订单', '2025-04-14 19:18:51');
INSERT INTO `order_status_history` VALUES (2, 2, NULL, 0, 3, 1, '创建订单', '2025-04-14 20:24:40');
INSERT INTO `order_status_history` VALUES (3, 1, 0, 4, 3, 1, '商品不符合预期，取消订单', '2025-04-15 10:21:41');
INSERT INTO `order_status_history` VALUES (4, 3, NULL, 0, 3, 1, '创建订单', '2025-04-15 11:39:24');
INSERT INTO `order_status_history` VALUES (5, 3, 0, 1, 3, 1, '用户完成支付', '2025-04-15 11:58:43');
INSERT INTO `order_status_history` VALUES (6, 4, NULL, 0, 3, 1, '创建订单', '2025-04-15 14:23:58');
INSERT INTO `order_status_history` VALUES (7, 4, 0, 1, 3, 1, '用户完成支付', '2025-04-15 14:24:48');
INSERT INTO `order_status_history` VALUES (8, 5, NULL, 0, 3, 1, '创建订单', '2025-04-15 14:58:44');
INSERT INTO `order_status_history` VALUES (9, 5, 0, 1, 3, 1, '用户完成支付', '2025-04-15 14:59:20');
INSERT INTO `order_status_history` VALUES (10, 6, NULL, 0, 3, 1, '创建订单', '2025-04-15 15:45:18');
INSERT INTO `order_status_history` VALUES (11, 7, NULL, 0, 3, 1, '创建订单', '2025-04-15 18:35:45');
INSERT INTO `order_status_history` VALUES (12, 8, NULL, 0, 3, 1, '创建订单', '2025-04-15 18:52:19');
INSERT INTO `order_status_history` VALUES (13, 2, 0, 1, 3, 1, '用户完成支付', '2025-04-15 19:04:48');
INSERT INTO `order_status_history` VALUES (14, 9, NULL, 0, 3, 1, '创建订单', '2025-05-12 12:58:36');

-- ----------------------------
-- Table structure for payment_password
-- ----------------------------
DROP TABLE IF EXISTS `payment_password`;
CREATE TABLE `payment_password`  (
  `password_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '支付密码记录ID（唯一主键）',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '所属用户ID，关联user表的user_id',
  `hashed_payment_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '经过加密处理后的支付密码，使用如bcrypt、argon2等算法加密',
  `last_update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新支付密码的时间',
  PRIMARY KEY (`password_id`) USING BTREE,
  UNIQUE INDEX `uniq_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_payment_password_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储用户支付密码信息的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_password
-- ----------------------------
INSERT INTO `payment_password` VALUES (1, 3, '$2a$10$fcBFCSHYsn2p4CspUVO9sOYlCfqFIuEcRCs7QLdIlZSKGLaBL.pCy', '2025-04-23 15:43:47');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `product_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品详细描述',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '物品原价',
  `category_id` int NOT NULL COMMENT '商品分类ID',
  `user_id` bigint NOT NULL COMMENT '发布用户ID',
  `condition_level` tinyint NULL DEFAULT NULL COMMENT '物品成色(1-10级)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '商品状态(0下架 1在售 2已售)',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品所在地',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '苹果手机', '用三个月，九成新，无划痕，国行全网通', 99.99, 199.99, 1, 3, 9, 1, '北京市海淀区', 4, '2025-04-11 17:02:30', '2025-04-14 19:03:37');
INSERT INTO `product` VALUES (2, 'iPhone 15 Pro Max 256GB 深空黑', '使用三个月，九成新，无划痕，国行全网通', 7999.00, 9999.00, 1, 3, 9, 1, '上海市浦东新区', 1, '2025-04-14 18:58:45', NULL);
INSERT INTO `product` VALUES (3, '水杯', '使用三个月，九成新，无划痕', 99.00, 999.00, 2, 3, 9, 1, '合肥市包河区', 0, '2025-04-14 19:08:27', NULL);
INSERT INTO `product` VALUES (4, '水杯', '使用三个月，九成新，无划痕', 999.00, 999.00, 2, 3, 9, 1, '合肥市包河区', 0, '2025-04-14 19:09:31', NULL);
INSERT INTO `product` VALUES (5, '联想电脑小新16pro', '使用一年，六成新，电池状态良好，不卡顿', 2000.00, 6000.00, 3, 5, 9, 1, '合肥市包河区', 1, '2025-04-14 19:27:13', NULL);
INSERT INTO `product` VALUES (6, '鼠标', '使用三个月，九成新，无划痕', 99.00, 999.00, 1, 3, 9, 1, '合肥市包河区', 0, '2025-04-15 18:29:04', NULL);
INSERT INTO `product` VALUES (7, '键盘', '使用三个月，九成新，无划痕', 99.00, 999.00, 1, 3, 9, 1, '合肥市包河区', 0, '2025-04-15 18:29:37', NULL);
INSERT INTO `product` VALUES (8, '显示屏', '使用三个月，九成新，无划痕', 99.00, 999.00, 1, 3, 9, 1, '合肥市包河区', 0, '2025-04-15 18:29:46', NULL);
INSERT INTO `product` VALUES (9, '主机', '使用三个月，九成新，无划痕', 99.00, 999.00, 1, 3, 9, 1, '合肥市包河区', 0, '2025-04-15 19:50:29', NULL);
INSERT INTO `product` VALUES (10, '水杯', '无划痕，保留完好', 99.00, 199.00, 7, 3, 9, 1, '北京市', 0, '2025-04-17 17:16:16', NULL);
INSERT INTO `product` VALUES (11, '111', '1111111111', 12.00, 111.00, 1, 3, 9, 1, '北京市', 0, '2025-04-17 17:16:46', NULL);
INSERT INTO `product` VALUES (12, '222', '22222222222', 2.00, 22.00, 1, 3, 9, 1, '北京市', 0, '2025-04-17 17:34:09', NULL);
INSERT INTO `product` VALUES (13, '33333333', '33333333333333', 33.00, 33333.00, 1, 3, 9, 1, '北京市', 0, '2025-04-17 17:38:28', NULL);
INSERT INTO `product` VALUES (14, '水杯', '使用三个月，九成新，无划痕', 99.00, 999.00, 2, 3, 9, 1, '合肥市包河区', 0, '2025-04-21 17:41:17', NULL);
INSERT INTO `product` VALUES (15, '手机iphone8', '手机使用时间短，性能好，适合打游戏', 1999.00, 5999.00, 1, 3, 9, 1, '合肥市', 0, '2025-04-22 13:58:53', NULL);
INSERT INTO `product` VALUES (16, '手机', '111111111', 2999.00, 5999.00, 1, 3, 9, 0, '合肥市', 0, '2025-04-22 16:08:49', '2025-04-22 16:31:05');
INSERT INTO `product` VALUES (17, '1', '1', 1.00, 1.00, 1, 3, 9, 0, '北京市', 0, '2025-04-22 16:13:37', '2025-04-22 16:26:43');
INSERT INTO `product` VALUES (18, '2', '2', 2.00, 2.00, 1, 3, 9, 0, '北京市', 0, '2025-04-22 16:19:57', '2025-04-22 16:26:40');
INSERT INTO `product` VALUES (19, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-04-22 16:26:58', NULL);
INSERT INTO `product` VALUES (20, '2', '2', 2.00, 2.00, 1, 3, 9, 0, '北京市', 0, '2025-04-22 16:34:45', '2025-04-22 16:45:39');
INSERT INTO `product` VALUES (21, '3', '3', 3.00, 3.00, 1, 3, 9, 1, '北京市', 0, '2025-04-22 16:45:27', NULL);
INSERT INTO `product` VALUES (22, '4', '4', 4.00, 4.00, 1, 3, 9, 1, '北京市', 0, '2025-04-22 16:46:01', NULL);
INSERT INTO `product` VALUES (23, '5', '5', 5.00, 5.00, 1, 3, 9, 1, '北京市', 0, '2025-04-22 16:49:19', NULL);
INSERT INTO `product` VALUES (24, '6', '6', 6.00, 6.00, 1, 3, 9, 0, '北京市', 0, '2025-04-22 17:39:45', '2025-04-29 18:04:57');
INSERT INTO `product` VALUES (25, '7', '7', 7.00, 7.00, 1, 3, 9, 0, '北京市', 0, '2025-04-22 18:12:37', '2025-04-29 18:04:32');
INSERT INTO `product` VALUES (26, '电脑', '使用三个月，九成新，无划痕', 2999.00, 5999.00, 1, 1, 9, 1, '合肥市包河区', 0, '2025-04-23 17:31:51', NULL);
INSERT INTO `product` VALUES (27, '手机', '使用三个月，九成新，无划痕', 999.00, 3999.00, 1, 1, 9, 0, '合肥市包河区', 0, '2025-04-23 17:39:33', '2025-04-29 17:58:39');
INSERT INTO `product` VALUES (28, '手机', '去年才买，电量几乎没掉，还在90以上，电池良好', 999.00, 3999.00, 1, 1, 9, 0, '合肥市包河区', 0, '2025-04-23 17:42:21', '2025-04-29 17:51:39');
INSERT INTO `product` VALUES (29, '相机wide300', '无损伤，镜头保持干净，无划痕', 1499.00, 3000.00, 2, 1, 9, 0, '合肥市包河区', 3, '2025-04-23 17:43:49', '2025-05-06 11:20:03');
INSERT INTO `product` VALUES (30, '非卖品（只展示）', '类似于大熊猫', 1.00, 2.00, 8, 3, 10, 1, '合肥市', 0, '2025-04-27 11:07:43', NULL);
INSERT INTO `product` VALUES (31, '篮球', '可室内也可室外，训练比赛专用篮球', 69.00, 199.00, 4, 4, 7, 1, '合肥市', 2, '2025-04-27 11:59:49', NULL);
INSERT INTO `product` VALUES (32, '机械键盘', 'rgb20种灯光，支持有线和无线蓝牙连接，磁轴支持游戏办公', 159.00, 330.00, 2, 4, 9, 1, '合肥市', 0, '2025-04-27 15:19:38', NULL);
INSERT INTO `product` VALUES (33, '相机wide300', '镜头完整无划痕，送内存卡和转接器支持手机连接', 1499.00, 2500.00, 1, 4, 9, 1, '合肥市', 3, '2025-04-27 15:21:24', NULL);
INSERT INTO `product` VALUES (34, '水果榨汁机', '商品质量好，适合各种水果榨汁吗，家庭的必需电器', 79.00, 236.00, 3, 6, 9, 1, '北京市', 3, '2025-04-27 15:53:49', NULL);
INSERT INTO `product` VALUES (35, '智能手表', '支持接打电话，微信qq聊天，心率步数展示，手机互联等等', 59.00, 399.00, 1, 6, 6, 1, '杭州市', 35, '2025-04-27 15:56:35', NULL);
INSERT INTO `product` VALUES (36, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-08 11:47:48', NULL);
INSERT INTO `product` VALUES (37, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 10:29:22', NULL);
INSERT INTO `product` VALUES (38, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 10:38:11', NULL);
INSERT INTO `product` VALUES (39, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 10:44:55', NULL);
INSERT INTO `product` VALUES (40, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 10:54:54', NULL);
INSERT INTO `product` VALUES (41, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 11:27:07', NULL);
INSERT INTO `product` VALUES (42, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 14:11:55', NULL);
INSERT INTO `product` VALUES (43, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 14:16:48', NULL);
INSERT INTO `product` VALUES (44, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 14:31:14', NULL);
INSERT INTO `product` VALUES (45, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 14:47:44', NULL);
INSERT INTO `product` VALUES (46, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 15:17:21', NULL);
INSERT INTO `product` VALUES (47, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 15:29:24', NULL);
INSERT INTO `product` VALUES (48, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 16:46:07', NULL);
INSERT INTO `product` VALUES (49, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 16:49:25', NULL);
INSERT INTO `product` VALUES (50, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 16:52:30', NULL);
INSERT INTO `product` VALUES (51, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 16:59:17', NULL);
INSERT INTO `product` VALUES (52, '1', '1', 1.00, 11.00, 1, 3, 9, 1, '北京市', 0, '2025-05-09 17:26:19', NULL);
INSERT INTO `product` VALUES (53, '1', '1', 1.00, 1.00, 1, 4, 9, 1, '北京市', 4, '2025-05-09 17:27:17', '2025-05-09 18:30:39');
INSERT INTO `product` VALUES (54, '1', '1', 1.00, 1.00, 1, 3, 9, 1, '北京市', 0, '2025-05-12 16:56:47', '2025-05-12 16:56:47');

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` int NULL DEFAULT 0 COMMENT '父分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_category
-- ----------------------------

-- ----------------------------
-- Table structure for product_favorite
-- ----------------------------
DROP TABLE IF EXISTS `product_favorite`;
CREATE TABLE `product_favorite`  (
  `favorite_id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`favorite_id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_favorite
-- ----------------------------
INSERT INTO `product_favorite` VALUES (3, 5, 3, '2025-04-14 19:41:25');
INSERT INTO `product_favorite` VALUES (4, 35, 5, '2025-04-27 16:39:28');

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `image_id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `is_main` tinyint NULL DEFAULT 0 COMMENT '是否主图(0否 1是)',
  `sort_order` int NULL DEFAULT 0 COMMENT '图片排序',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`image_id`) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_image
-- ----------------------------
INSERT INTO `product_image` VALUES (1, 13, 'http://localhost:8080/files/ec0fe772-6ee0-44ba-b06d-5a0d6aad2417.jpg', 1, 0, '2025-04-17 17:38:28');
INSERT INTO `product_image` VALUES (2, 7, 'https://pic.52112.com/180824/EPS-180824_327/J1EwJuHlI3_small.jpg', 1, 0, '2025-04-22 11:47:18');
INSERT INTO `product_image` VALUES (3, 15, 'http://localhost:8080/files/29043c9e-5d21-4c7b-9ee0-cfa42455dca0.png', 1, 0, '2025-04-22 13:58:53');
INSERT INTO `product_image` VALUES (4, 16, 'http://localhost:8080/files/efb720c3-6ed4-4854-b078-58e689f5f05f.png', 1, 0, '2025-04-22 16:08:49');
INSERT INTO `product_image` VALUES (5, 17, 'http://localhost:8080/files/bcb9cc2e-af75-445d-8908-45faddca06d1.png', 0, 0, '2025-04-22 16:13:37');
INSERT INTO `product_image` VALUES (6, 1, 'https://pic.52112.com/180824/EPS-180824_327/J1EwJuHlI3_small.jpg', 1, 0, '2025-04-22 16:16:21');
INSERT INTO `product_image` VALUES (7, 18, 'http://localhost:8080/files/2fdf7714-73bd-49d6-9bce-8d46d31928a6.png', 0, 0, '2025-04-22 16:19:57');
INSERT INTO `product_image` VALUES (8, 19, 'http://localhost:8080/files/a6ba92ff-b8fc-4210-9062-f8ac4654c4ae.png', 0, 0, '2025-04-22 16:26:58');
INSERT INTO `product_image` VALUES (9, 20, 'http://localhost:8080/files/8e83edf2-c9f5-4bd0-9d73-62bba5255ea4.png', 0, 0, '2025-04-22 16:34:45');
INSERT INTO `product_image` VALUES (10, 21, 'http://localhost:8080/files/ab2342e7-af8d-4c1f-b36a-185260117132.jpg', 0, 0, '2025-04-22 16:45:27');
INSERT INTO `product_image` VALUES (11, 22, 'http://localhost:8080/files/8e30e4e4-851b-4ee2-bb0d-ce84e762e969.png', 0, 0, '2025-04-22 16:46:00');
INSERT INTO `product_image` VALUES (12, 23, 'http://localhost:8080/files/2a6ca430-c03c-4e42-9f40-c447f5ad3edb.png', 0, 0, '2025-04-22 16:49:19');
INSERT INTO `product_image` VALUES (13, 24, 'http://localhost:8080/files/04edad83-4111-4115-b6d8-61b6c169ba36.png', 0, 0, '2025-04-22 17:39:44');
INSERT INTO `product_image` VALUES (14, 25, 'http://localhost:8080/files/a9d30c35-337f-4be4-8939-1e92b9f137a3.png', 0, 0, '2025-04-22 18:12:37');
INSERT INTO `product_image` VALUES (15, 26, 'https://pic.52112.com/180824/EPS-180824_327/J1EwJuHlI3_small.jpg', 1, 0, '2025-04-23 17:55:07');
INSERT INTO `product_image` VALUES (16, 27, 'https://imgse.com/i/pEIHVbQ', 1, 0, '2025-04-23 18:06:52');
INSERT INTO `product_image` VALUES (17, 29, 'https://imgse.com/i/pEIHYVJ', 1, 0, '2025-04-23 18:19:24');
INSERT INTO `product_image` VALUES (18, 30, 'http://localhost:8080/files/484c4cd0-544f-45b4-b8cb-2d8b63c1fa06.png', 0, 0, '2025-04-27 11:07:43');
INSERT INTO `product_image` VALUES (19, 31, 'http://localhost:8080/files/1eb85ed9-9786-4fff-be53-6135af66297d.png', 0, 0, '2025-04-27 11:59:48');
INSERT INTO `product_image` VALUES (20, 32, 'http://localhost:8080/files/15fd2ce2-ce1e-4ef3-b2a2-7e37060ebf60.png', 0, 0, '2025-04-27 15:19:37');
INSERT INTO `product_image` VALUES (21, 33, 'http://localhost:8080/files/60ae5e74-692e-4d49-94ed-3725e8afe28f.png', 0, 0, '2025-04-27 15:21:24');
INSERT INTO `product_image` VALUES (22, 34, 'http://localhost:8080/files/bc924fc7-a334-4647-84f7-9fdbf664036a.png', 0, 0, '2025-04-27 15:53:48');
INSERT INTO `product_image` VALUES (23, 35, 'http://localhost:8080/files/e6a4a384-1690-49dd-94eb-d40724e28dd6.png', 0, 0, '2025-04-27 15:56:35');
INSERT INTO `product_image` VALUES (24, 36, 'http://192.168.200.30:8080/images/8bbccb9e-9d44-4de6-9ee4-ce5938e56106.png', 0, 0, '2025-05-08 11:47:47');
INSERT INTO `product_image` VALUES (25, 37, 'http://192.168.200.30:8080/images/ab619d96-52d4-401a-ad5c-8e3de9688c7b.png', 0, 0, '2025-05-09 10:29:22');
INSERT INTO `product_image` VALUES (26, 38, 'http://192.168.200.30:8080/images/73606fc9-59f3-48d6-afb8-c8b9b8bafa9d.png', 0, 0, '2025-05-09 10:38:11');
INSERT INTO `product_image` VALUES (27, 39, 'http://192.168.200.30:8080/images/bf7d171b-69e5-4059-937a-153054f55483.png', 0, 0, '2025-05-09 10:44:55');
INSERT INTO `product_image` VALUES (28, 40, 'http://192.168.200.30:8080/images/e9c13b3a-b98d-44a0-bddb-c953560dc5e5.png', 0, 0, '2025-05-09 10:54:54');
INSERT INTO `product_image` VALUES (29, 41, 'http://192.168.200.30:8080/images/cc39410b-4483-47e2-a8f9-007397813429.png', 0, 0, '2025-05-09 11:27:07');
INSERT INTO `product_image` VALUES (30, 42, 'http://192.168.200.30:8080/images/1960b969-fb65-4f67-95d2-6e6fe0563d52.png', 0, 0, '2025-05-09 14:11:55');
INSERT INTO `product_image` VALUES (31, 43, 'http://192.168.200.30:8080/images/9bec7de7-cd6b-4d81-8b71-0f77268b06d8.png', 0, 0, '2025-05-09 14:16:48');
INSERT INTO `product_image` VALUES (32, 44, 'http://192.168.200.30:8080/images/71f5df8c-dc63-48cc-b553-52da9a179069.png', 0, 0, '2025-05-09 14:31:13');
INSERT INTO `product_image` VALUES (33, 45, 'http://192.168.200.30:8080/images/11ffdb66-ded8-4ffe-aedc-8a0b03b492cd.png', 0, 0, '2025-05-09 14:47:44');
INSERT INTO `product_image` VALUES (34, 46, 'http://192.168.200.30:8080/images/0d52b0f1-872b-4d52-b1d8-fa46896f0f9f.png', 0, 0, '2025-05-09 15:17:21');
INSERT INTO `product_image` VALUES (35, 47, 'http://192.168.200.30:8080/images/72aceb98-bbc6-4c3f-82d0-c5dd2632519f.png', 0, 0, '2025-05-09 15:29:24');
INSERT INTO `product_image` VALUES (36, 48, 'http://192.168.200.30:8080/images/f073bc26-4e45-4477-ad42-6ce154fc632b.png', 0, 0, '2025-05-09 16:46:07');
INSERT INTO `product_image` VALUES (37, 49, 'http://192.168.200.30:8080/images/9954168a-aefa-4749-805c-f1bb8ca8428e.png', 0, 0, '2025-05-09 16:49:25');
INSERT INTO `product_image` VALUES (38, 50, 'http://192.168.200.30:8080/images/72c22002-3ed3-4124-a844-7f1f8fe3c410.png', 0, 0, '2025-05-09 16:52:30');
INSERT INTO `product_image` VALUES (39, 51, 'http://192.168.200.30:8080/images/f227d22f-5eec-421c-90d8-61ce15510540.png', 0, 0, '2025-05-09 16:59:18');
INSERT INTO `product_image` VALUES (40, 52, 'http://192.168.200.30:8080/images/1121db3f-b491-48a5-a900-918b415e67c8.png', 0, 0, '2025-05-09 17:26:19');
INSERT INTO `product_image` VALUES (41, 53, 'http://192.168.200.30:8080/images/195e3c22-3972-4b9a-bb86-86e72508ec44.png', 0, 0, '2025-05-09 17:27:17');
INSERT INTO `product_image` VALUES (42, 54, 'http://192.168.200.30:8080/images/07ed67db-00a9-4460-90a3-08e432f05525.png', 0, 0, '2025-05-12 16:56:48');

-- ----------------------------
-- Table structure for product_report
-- ----------------------------
DROP TABLE IF EXISTS `product_report`;
CREATE TABLE `product_report`  (
  `report_id` bigint NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '举报用户ID',
  `report_type` tinyint NOT NULL COMMENT '举报类型(1虚假商品 2违禁品 3侵权等)',
  `report_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '举报内容',
  `status` tinyint NULL DEFAULT 0 COMMENT '处理状态(0未处理 1已处理)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品举报表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_report
-- ----------------------------
INSERT INTO `product_report` VALUES (1, 25, 3, 2, '该商品涉嫌销售违禁品', 0, '2025-04-29 13:55:35', NULL);
INSERT INTO `product_report` VALUES (2, 26, 3, 1, '该商品涉嫌销售违禁品', 0, '2025-04-29 14:03:37', NULL);

-- ----------------------------
-- Table structure for promotion
-- ----------------------------
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion`  (
  `promotion_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '营销活动ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动描述',
  `promotion_type` tinyint NOT NULL COMMENT '活动类型(1促销活动 2折扣 3满减 4优惠券)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0下线 1上线)',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号，值越大越靠前',
  `url_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '点击跳转链接',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建人ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`promotion_id`) USING BTREE,
  INDEX `idx_type`(`promotion_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_time`(`start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '营销活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of promotion
-- ----------------------------
INSERT INTO `promotion` VALUES (1, '618年中大促', '全场商品最低5折起，家电数码好物推荐', 1, '2025-06-01 00:00:00', '2025-06-18 23:59:59', 0, 100, '/pages/promotion/618', 3, '2025-05-12 15:23:08', '2025-05-12 16:44:06');
INSERT INTO `promotion` VALUES (5, '1', '1', 1, '2025-05-12 00:00:00', '2025-06-09 00:00:00', 1, 1, '', 3, '2025-05-12 15:45:20', '2025-05-12 15:45:20');

-- ----------------------------
-- Table structure for promotion_image
-- ----------------------------
DROP TABLE IF EXISTS `promotion_image`;
CREATE TABLE `promotion_image`  (
  `image_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `promotion_id` bigint UNSIGNED NOT NULL COMMENT '关联的营销活动ID',
  `image_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `image_type` tinyint NULL DEFAULT 1 COMMENT '图片类型(1轮播图 2展示图)',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号，值越大越靠前',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`image_id`) USING BTREE,
  INDEX `idx_promotion`(`promotion_id` ASC) USING BTREE,
  CONSTRAINT `fk_promotion_image` FOREIGN KEY (`promotion_id`) REFERENCES `promotion` (`promotion_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '营销活动图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of promotion_image
-- ----------------------------
INSERT INTO `promotion_image` VALUES (1, 1, 'https://example.com/images/banner1.jpg', 1, 10, '2025-05-12 15:23:08');
INSERT INTO `promotion_image` VALUES (2, 1, 'https://example.com/images/banner2.jpg', 1, 9, '2025-05-12 15:23:08');
INSERT INTO `promotion_image` VALUES (3, 5, 'http://192.168.200.30:8080/images/promotion/20250512/6e03a594d13d4fde901a26cf479170d7.png', 1, 0, '2025-05-12 15:45:19');
INSERT INTO `promotion_image` VALUES (4, 5, 'http://192.168.200.30:8080/images/promotion/20250512/7335ec9877b542f890e94e676418701e.png', 1, 0, '2025-05-12 15:45:19');

-- ----------------------------
-- Table structure for recycle_method
-- ----------------------------
DROP TABLE IF EXISTS `recycle_method`;
CREATE TABLE `recycle_method`  (
  `method_id` tinyint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '方式ID',
  `method_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方式名称（如：上门回收）',
  `method_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方式描述',
  PRIMARY KEY (`method_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '回收方式表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_method
-- ----------------------------
INSERT INTO `recycle_method` VALUES (1, '上门回收', '由回收人员上门进行商品回收交易');

-- ----------------------------
-- Table structure for refund_apply
-- ----------------------------
DROP TABLE IF EXISTS `refund_apply`;
CREATE TABLE `refund_apply`  (
  `refund_id` bigint NOT NULL AUTO_INCREMENT COMMENT '退款申请ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `refund_reason` tinyint NOT NULL COMMENT '退款原因(1质量问题 2描述不符 3卖家未发货 4不想要了 5其他)',
  `refund_reason_detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因详情',
  `refund_status` tinyint NULL DEFAULT 0 COMMENT '退款状态(0处理中 1已同意 2已拒绝)',
  `admin_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员处理备注',
  `processed_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`refund_id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退款申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refund_apply
-- ----------------------------

-- ----------------------------
-- Table structure for release_location
-- ----------------------------
DROP TABLE IF EXISTS `release_location`;
CREATE TABLE `release_location`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of release_location
-- ----------------------------
INSERT INTO `release_location` VALUES (1, '广东省', '深圳市', '南山区');

-- ----------------------------
-- Table structure for shipping_address
-- ----------------------------
DROP TABLE IF EXISTS `shipping_address`;
CREATE TABLE `shipping_address`  (
  `address_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '所属用户ID',
  `shipper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发货人姓名',
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所在地区',
  `detail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系电话',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址（0-否 1-是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`address_id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_shipping_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '发货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shipping_address
-- ----------------------------
INSERT INTO `shipping_address` VALUES (3, 3, '徐超', '北京市朝阳区', '建国路88号', '15255133789', 1, '2025-05-06 14:28:26');

-- ----------------------------
-- Table structure for transaction_record
-- ----------------------------
DROP TABLE IF EXISTS `transaction_record`;
CREATE TABLE `transaction_record`  (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `transaction_type` tinyint NOT NULL COMMENT '交易类型(1支付 2退款)',
  `transaction_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方交易流水号',
  `amount` decimal(10, 2) NOT NULL COMMENT '交易金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '交易状态(0处理中 1成功 2失败)',
  `payment_channel` tinyint NULL DEFAULT NULL COMMENT '支付渠道(1支付宝 2微信 3银行卡)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`transaction_id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '交易流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transaction_record
-- ----------------------------
INSERT INTO `transaction_record` VALUES (1, 3, 3, 1, 'wx202504141528120123456', 6000.00, 1, 2, '订单支付', '2025-04-15 11:58:43', '2025-04-15 11:58:43');
INSERT INTO `transaction_record` VALUES (2, 4, 3, 1, 'wx202504141528120123456', 6000.00, 1, 2, '订单支付', '2025-04-15 14:24:48', '2025-04-15 14:24:48');
INSERT INTO `transaction_record` VALUES (3, 5, 3, 1, 'wx202504141528120123456', 6000.00, 1, 2, '订单支付', '2025-04-15 14:59:20', '2025-04-15 14:59:20');
INSERT INTO `transaction_record` VALUES (4, 2, 3, 1, 'ALI1744715087621', 6000.00, 1, 1, '订单支付', '2025-04-15 19:04:48', '2025-04-15 19:04:48');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID（唯一主键）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（必须唯一）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码（明文存储）',
  `role` enum('系统管理员','普通用户') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '普通用户' COMMENT '用户角色（枚举类型）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号码（可选）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电子邮箱（可选）',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'default.png' COMMENT '头像路径（默认图片）',
  `balance` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '账户余额（默认0）',
  `is_locked` tinyint(1) NULL DEFAULT 0 COMMENT '账户状态（0-正常 1-锁定）',
  `ban_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封禁原因（可选）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uniq_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户核心表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'sbxcl', '123456789', '普通用户', NULL, NULL, 'default.png', 0.00, 1, NULL, '2025-04-09 15:49:22');
INSERT INTO `user` VALUES (2, 'sbxcl1', '1234567899', '普通用户', '13800138000', 'user@example.com', 'new_avatar.png', 0.00, 0, '发布黄色违规内容', '2025-04-09 16:39:17');
INSERT INTO `user` VALUES (3, 'xiaoxinaikitty', '010920', '系统管理员', '13695516211', '2071162373@qq.com', 'http://192.168.200.30:8080/images/cc590304-644c-4cae-b418-eec506a13965.png', 0.00, 0, NULL, '2025-04-09 19:16:25');
INSERT INTO `user` VALUES (4, 'admin', 'admin', '系统管理员', NULL, NULL, 'default.png', 0.00, 0, NULL, '2025-04-09 19:21:13');
INSERT INTO `user` VALUES (5, 'sbxclsb', '123456', '普通用户', NULL, NULL, 'default.png', 0.00, 0, NULL, '2025-04-14 11:14:07');
INSERT INTO `user` VALUES (6, 'xiaoxin', '123456', '普通用户', NULL, NULL, 'default.png', 0.00, 0, NULL, '2025-04-17 15:26:16');
INSERT INTO `user` VALUES (7, '张三', '123456', '普通用户', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (8, '李四', '123456', '普通用户', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (9, '王五', '123456', '普通用户', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (10, '溪溪', '123456', '普通用户', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (11, '冰冰', '123456', '普通用户', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (12, '小燕', '123456', '普通用户', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (13, '小杰', '123456', '普通用户', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (14, '小飞子', '123456', '普通用户', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (15, '小周', '123456', '普通用户', '13800138000', 'zhangsan@example.com', NULL, 0.00, 0, NULL, NULL);
INSERT INTO `user` VALUES (16, '小超', '123456', '普通用户', '13800138000', 'zhangsan@example.com', NULL, 0.00, 0, NULL, '2025-04-27 11:11:22');
INSERT INTO `user` VALUES (17, '小明', '123456', '普通用户', '13800138000', 'zhangsan@example.com', NULL, 0.00, 0, NULL, '2025-04-27 15:40:33');
INSERT INTO `user` VALUES (18, 'kitty', '2001920xuchao', '普通用户', '15255133789', NULL, NULL, 0.00, 0, NULL, '2025-05-07 19:38:34');

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `address_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '所属用户ID',
  `consignee` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所在地区',
  `detail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系电话',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址（0-否 1-是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`address_id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (1, 2, '小徐', '合肥', '包河区', '110', 1, '2025-04-10 11:49:39');
INSERT INTO `user_address` VALUES (2, 3, '小徐', '合肥', '包河区', '110', 0, '2025-04-30 17:52:53');
INSERT INTO `user_address` VALUES (3, 3, '张三', '广东省深圳市南山区', '科技园南区8栋101', '13800138000', 0, '2025-05-06 10:45:35');
INSERT INTO `user_address` VALUES (4, 3, 'xiaoxin', '安徽省合肥市', '安百苑小区', '13695516211', 1, '2025-05-06 10:54:56');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `user_coupon_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `coupon_id` bigint UNSIGNED NOT NULL COMMENT '优惠券ID',
  `receive_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint NULL DEFAULT NULL COMMENT '使用的订单ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态(0未使用 1已使用 2已过期)',
  PRIMARY KEY (`user_coupon_id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_coupon`(`coupon_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`coupon_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_coupon_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for user_feedback
-- ----------------------------
DROP TABLE IF EXISTS `user_feedback`;
CREATE TABLE `user_feedback`  (
  `feedback_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '反馈ID（唯一主键）',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID，关联user表的user_id',
  `feedback_type` tinyint NOT NULL COMMENT '反馈类型(1功能建议 2体验问题 3商品相关 4物流相关 5其他)',
  `feedback_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈标题',
  `feedback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈内容详情',
  `contact_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式（电话或邮箱，可选）',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '反馈附带的图片URL，多个URL使用逗号分隔',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '处理状态(0未处理 1处理中 2已处理)',
  `priority_level` tinyint NULL DEFAULT 0 COMMENT '优先级(0普通 1重要 2紧急)',
  `admin_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '处理人员ID，关联user表中role为系统管理员的user_id',
  `admin_reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '管理员回复内容',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '反馈提交时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`feedback_id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  CONSTRAINT `fk_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_feedback
-- ----------------------------
INSERT INTO `user_feedback` VALUES (1, 3, 1, '建议增加筛选功能', '希望能增加按照商品成色的筛选功能，方便查找', 'example@example.com', 'https://imgse.com/i/pELgNXF', 0, 0, NULL, NULL, NULL, '2025-05-08 15:42:24', NULL);
INSERT INTO `user_feedback` VALUES (2, 3, 1, '建议增加筛选功能', '希望能增加按照商品成色的筛选功能，方便查找', 'example@example.com', 'https://imgse.com/i/pELgNXF', 0, 0, NULL, NULL, NULL, '2025-05-08 16:36:41', NULL);
INSERT INTO `user_feedback` VALUES (3, 3, 1, '建议增加筛选功能', '希望能增加按照商品成色的筛选功能，方便查找', 'example@example.com', 'https://imgse.com/i/pELgNXF', 2, 0, 3, '我已知晓情况', '2025-05-08 16:52:35', '2025-05-08 16:37:32', NULL);
INSERT INTO `user_feedback` VALUES (4, 3, 2, '建议提供微信支付', '购买商品付款时希望可以提供微信支付，可以变得更加方便', '2071162373@qq.com', NULL, 0, 0, NULL, NULL, NULL, '2025-05-08 17:44:17', NULL);
INSERT INTO `user_feedback` VALUES (5, 3, 3, '违规商品', '该商品没有任何的使用价值，影响用户体验', '13695516211', 'http://192.168.200.30:8080/images/f4a5f067-6f52-43ea-8694-a550a64415d9.png', 0, 0, NULL, NULL, NULL, '2025-05-08 17:57:42', NULL);

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `message_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `product_id` bigint NOT NULL COMMENT '关联商品ID',
  `sender_id` bigint UNSIGNED NOT NULL COMMENT '发送者用户ID',
  `receiver_id` bigint UNSIGNED NOT NULL COMMENT '接收者用户ID（商品发布者）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息附带图片URL',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读(0未读 1已读)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE,
  INDEX `idx_sender`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_message
-- ----------------------------
INSERT INTO `user_message` VALUES (1, 53, 3, 4, '您好，请问这个商品还在吗？', 'http://example.com/image.jpg', 1, '2025-05-09 18:11:33');
INSERT INTO `user_message` VALUES (2, 53, 3, 4, '哈喽', NULL, 0, '2025-05-09 18:31:10');

-- ----------------------------
-- Table structure for user_product
-- ----------------------------
DROP TABLE IF EXISTS `user_product`;
CREATE TABLE `user_product`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品标题',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '物品原价',
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品主图URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '商品状态(0下架 1在售 2已售)',
  `category_id` int NOT NULL COMMENT '商品分类ID',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `favorite_count` int NULL DEFAULT 0 COMMENT '收藏数量',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE COMMENT '用户商品唯一索引',
  INDEX `idx_user_time`(`user_id` ASC, `created_time` DESC) USING BTREE COMMENT '用户发布时间索引',
  INDEX `idx_product`(`product_id` ASC) USING BTREE COMMENT '商品ID索引',
  INDEX `idx_category`(`category_id` ASC) USING BTREE COMMENT '分类索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '状态索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户发布商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_product
-- ----------------------------

-- ----------------------------
-- Table structure for verification_code_record
-- ----------------------------
DROP TABLE IF EXISTS `verification_code_record`;
CREATE TABLE `verification_code_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID（唯一主键）',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收验证码的手机号码',
  `verification_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送的验证码，一般为6位数字',
  `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '验证码发送时间',
  `expiration_time` datetime NOT NULL COMMENT '验证码过期时间，根据业务设定合理的过期时长',
  `is_used` tinyint(1) NOT NULL DEFAULT 0 COMMENT '验证码是否已使用，0表示未使用，1表示已使用',
  `attempt_count` int NOT NULL DEFAULT 0 COMMENT '验证码验证尝试次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储手机验证码发送记录的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of verification_code_record
-- ----------------------------
INSERT INTO `verification_code_record` VALUES (1, '13695516211', '582766', '2025-04-24 18:17:55', '2025-04-24 18:22:55', 0, 0);

-- ----------------------------
-- Table structure for verification_code_verification_history
-- ----------------------------
DROP TABLE IF EXISTS `verification_code_verification_history`;
CREATE TABLE `verification_code_verification_history`  (
  `history_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '验证历史记录ID（唯一主键）',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '进行验证码验证的手机号码',
  `verification_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用于验证的验证码',
  `verification_result` tinyint(1) NOT NULL COMMENT '验证结果，0表示验证失败，1表示验证成功',
  `verification_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '验证码验证时间',
  `related_record_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联的验证码记录表中的id，方便追溯原始验证码发送记录',
  PRIMARY KEY (`history_id`) USING BTREE,
  INDEX `idx_phone_number`(`phone_number` ASC) USING BTREE,
  INDEX `idx_related_record_id`(`related_record_id` ASC) USING BTREE,
  CONSTRAINT `fk_verification_history_record` FOREIGN KEY (`related_record_id`) REFERENCES `verification_code_record` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储手机验证码验证历史记录的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of verification_code_verification_history
-- ----------------------------
INSERT INTO `verification_code_verification_history` VALUES (1, '13695516211', '123456', 0, '2025-04-24 18:30:40', 1);

-- ----------------------------
-- Table structure for wallet_account
-- ----------------------------
DROP TABLE IF EXISTS `wallet_account`;
CREATE TABLE `wallet_account`  (
  `account_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '钱包账户ID（唯一主键）',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '所属用户ID，关联user表的user_id',
  `balance` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '钱包余额',
  `frozen_balance` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '冻结余额，如在交易处理中被冻结的金额',
  `last_update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`account_id`) USING BTREE,
  UNIQUE INDEX `uniq_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_wallet_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '钱包账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wallet_account
-- ----------------------------
INSERT INTO `wallet_account` VALUES (1, 3, 200010100.00, 200.00, '2025-04-23 15:19:12');
INSERT INTO `wallet_account` VALUES (2, 5, 10000.00, 0.00, '2025-04-27 18:53:50');

-- ----------------------------
-- Table structure for wallet_transaction
-- ----------------------------
DROP TABLE IF EXISTS `wallet_transaction`;
CREATE TABLE `wallet_transaction`  (
  `transaction_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '交易ID（唯一主键）',
  `account_id` bigint UNSIGNED NOT NULL COMMENT '钱包账户ID，关联wallet_account表的account_id',
  `transaction_type` tinyint NOT NULL COMMENT '交易类型(1充值 2消费 3退款 4其他)',
  `transaction_amount` decimal(12, 2) NOT NULL COMMENT '交易金额',
  `before_balance` decimal(12, 2) NOT NULL COMMENT '交易前余额',
  `after_balance` decimal(12, 2) NOT NULL COMMENT '交易后余额',
  `transaction_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易备注',
  PRIMARY KEY (`transaction_id`) USING BTREE,
  INDEX `idx_account`(`account_id` ASC) USING BTREE,
  CONSTRAINT `fk_wallet_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `wallet_account` (`account_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '钱包交易记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wallet_transaction
-- ----------------------------
INSERT INTO `wallet_transaction` VALUES (1, 1, 1, 1000.00, 500.00, 1500.00, '2025-04-23 14:28:27', '用户充值');
INSERT INTO `wallet_transaction` VALUES (2, 1, 1, 1000.00, 200000100.00, 1500.00, '2025-04-23 14:30:48', '用户充值');
INSERT INTO `wallet_transaction` VALUES (3, 1, 1, 10000.00, 200000100.00, 200010100.00, '2025-04-23 15:19:12', '用户充值');
INSERT INTO `wallet_transaction` VALUES (4, 2, 1, 10000.00, 0.00, 10000.00, '2025-04-27 18:53:50', '用户充值');

SET FOREIGN_KEY_CHECKS = 1;
