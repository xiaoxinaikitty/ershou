-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` int NULL DEFAULT 0 COMMENT '父分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (1, 0, '电子产品', 1, 1, NOW());
INSERT INTO `product_category` VALUES (2, 0, '生活用品', 2, 1, NOW());
INSERT INTO `product_category` VALUES (3, 0, '电脑办公', 3, 1, NOW());
INSERT INTO `product_category` VALUES (4, 0, '运动户外', 4, 1, NOW());
INSERT INTO `product_category` VALUES (5, 0, '图书音像', 5, 1, NOW());
INSERT INTO `product_category` VALUES (6, 0, '服装鞋包', 6, 1, NOW());
INSERT INTO `product_category` VALUES (7, 0, '家居家装', 7, 1, NOW());
INSERT INTO `product_category` VALUES (8, 0, '其他分类', 8, 1, NOW()); 