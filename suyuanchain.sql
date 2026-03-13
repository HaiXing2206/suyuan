/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : suyuanchain

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 12/05/2025 20:30:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for accesscontrol
-- ----------------------------
DROP TABLE IF EXISTS `accesscontrol`;
CREATE TABLE `accesscontrol`  (
  `access_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `resource_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `resource_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `permission` enum('read','write','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `granted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`access_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `accesscontrol_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of accesscontrol
-- ----------------------------

-- ----------------------------
-- Table structure for page_visits
-- ----------------------------
DROP TABLE IF EXISTS `page_visits`;
CREATE TABLE `page_visits`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `page_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `visit_time` datetime(6) NOT NULL,
  `visit_count` int NULL DEFAULT NULL,
  `product_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of page_visits
-- ----------------------------
INSERT INTO `page_visits` VALUES (2, 'trace.html', '2025-05-12 09:57:42.827678', 3, 'qqqsadss');
INSERT INTO `page_visits` VALUES (3, 'trace.html', '2025-05-12 11:12:31.765431', 1, '1');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `product_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `manufacturer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `batch_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `production_date` timestamp NOT NULL,
  `origin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `	product_spec` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `product_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `del_at` timestamp NULL DEFAULT NULL,
  `qr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `product_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `product_spec` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `del` int NULL DEFAULT NULL,
  PRIMARY KEY (`product_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES ('1', '产品1', 'xx有限公司', 'SDS-2', '2025-05-12 07:30:09', '中国', NULL, NULL, NULL, NULL, '0x2aff864af9a992245a24b264f0e2ab694497cf988fac9e2a93a324e8fd575fca', NULL, 1);
INSERT INTO `products` VALUES ('1111111111', '产品B', 'xx有限公司', 'SDS-1', '2025-05-11 13:51:38', '中国', NULL, NULL, NULL, NULL, '0x49f05dea4a996ea67d5833fa2b3c8d1b9088b257dd93e829a82a8d28a7a1b6be', NULL, NULL);
INSERT INTO `products` VALUES ('111111111111', '产品C', 'xx有限公司', 'SDS-2', '2025-05-11 13:58:13', '中国', NULL, NULL, NULL, NULL, '0x68c6fcb6ef5babb8b1d14b2284f3e19d888c6033411f23a7dc1eded2af5ea08c', NULL, NULL);
INSERT INTO `products` VALUES ('111121322', '产品1', 'xx有限公司', 'SDS-2', '2025-05-12 08:02:52', '中国', NULL, NULL, '2025-05-12 09:20:01', NULL, '0x052ab38f1af9f1fbd2d9fada056720e97612cb12c7f4d17b73ee8ad58685a5b6', NULL, 1);
INSERT INTO `products` VALUES ('11112222', '222', '333', '333', '2025-05-10 14:20:22', '131', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `products` VALUES ('111122222', '222', '333', '333', '2025-05-10 14:25:11', '131', NULL, NULL, NULL, NULL, '0x10af3246528656274a9e5a3c6ff91167a4605819c0b3a3de198f794432fdc1cd', NULL, NULL);
INSERT INTO `products` VALUES ('12222', '产品A', 'xx有限公司', 'SDS', '2025-05-11 12:46:34', '中国', NULL, '描述', NULL, NULL, '0x2f3b92242296d24af8e9d3a7b955a601efb68f7240cf277d66d7ee985b5dc8ff', '500ml', NULL);
INSERT INTO `products` VALUES ('122222', '产品A', 'xx有限公司', 'SDS', '2025-05-11 12:47:29', '中国', NULL, '211', NULL, NULL, '0x0a0bc8b90d15279129f42f02693125d62892be82efe06c4f57e0f517d631c5dc', '500ml', NULL);
INSERT INTO `products` VALUES ('122222111', '产品A', 'xx有限公司', 'SDS', '2025-05-11 12:49:07', '中国', NULL, NULL, NULL, NULL, '0xdb631e8dbf41e8719c46dffa7e33d736cab6f005033be5e2b6cc8ae328d07987', NULL, NULL);
INSERT INTO `products` VALUES ('123', 'test', 'test', '1', '2025-05-11 12:40:09', '中国', NULL, NULL, NULL, NULL, '0x1dd1c3dcff411501a78cb5da46955c5dd5b0c201d3f33f2b928736b8aef7b351', NULL, NULL);
INSERT INTO `products` VALUES ('qqqq', '产品D', 'xx有限公司', 'SDS-2', '2025-05-11 15:33:51', '中国', NULL, NULL, NULL, NULL, '0x2dc85d5ec065b30d509d79096f1f6e2ec76a8a813efc0cfc6da56edefc699d95', NULL, 0);
INSERT INTO `products` VALUES ('qqqsadss', '产品D', 'xx有限公司', 'SDS-2', '2025-05-12 05:09:50', '中国', NULL, NULL, NULL, NULL, '0x343ef33c07759a471735ba78c896c8062ad9d20115c64437f9e5a14ca70cdeb8', NULL, 0);
INSERT INTO `products` VALUES ('qqqsadssaa', '产品D', 'xx有限公司', 'SDS-2', '2025-05-12 06:36:51', '中国', NULL, NULL, NULL, NULL, '0xa682a274511d773ed1c0625138cc04ad32073b237c09feaf599c8a218f3c702e', NULL, 0);
INSERT INTO `products` VALUES ('SAFSA', '商品a', 'xx有限公司', 'SDS', '2025-05-11 12:44:26', '中国', NULL, NULL, NULL, NULL, '0xdd03a5e7988c329c264101130bcac30000e91320156c19667050e518929a60c8', NULL, NULL);

-- ----------------------------
-- Table structure for supply_chain_records
-- ----------------------------
DROP TABLE IF EXISTS `supply_chain_records`;
CREATE TABLE `supply_chain_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `timestamp` timestamp NOT NULL,
  `operator_address` varchar(42) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `supply_chain_records_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supply_chain_records
-- ----------------------------

-- ----------------------------
-- Table structure for system_settings
-- ----------------------------
DROP TABLE IF EXISTS `system_settings`;
CREATE TABLE `system_settings`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `system_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `timezone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email_notification_enabled` tinyint(1) NULL DEFAULT NULL,
  `system_notification_enabled` tinyint(1) NULL DEFAULT NULL,
  `sms_notification_enabled` tinyint(1) NULL DEFAULT NULL,
  `two_factor_auth_enabled` tinyint(1) NULL DEFAULT NULL,
  `session_timeout` int NULL DEFAULT NULL,
  `password_policy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `data_retention_days` int NULL DEFAULT NULL,
  `auto_backup_enabled` tinyint(1) NULL DEFAULT NULL,
  `backup_frequency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `last_modified` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_settings
-- ----------------------------
INSERT INTO `system_settings` VALUES ('da4df18b-70cc-4e39-8cda-e43f2255afc1', '产品溯源管理系统', '示例科技有限公司', 'contact@example.com', 'Asia/Shanghai', 1, 1, 0, 1, 30, 'standard', 365, 1, 'weekly', '2025-05-12 06:37:41');

-- ----------------------------
-- Table structure for trace_record
-- ----------------------------
DROP TABLE IF EXISTS `trace_record`;
CREATE TABLE `trace_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `operator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `stage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `timestamp` datetime(6) NOT NULL,
  `transaction_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trace_record
-- ----------------------------
INSERT INTO `trace_record` VALUES (1, 'test', '仓库a', 'test', '11112222', '运输', '2025-05-11 13:31:52.993259', '0x30eeee2014dd40b25fd25e8a955f3c2613d6d7aad3bb76999586d0075f5d1234');
INSERT INTO `trace_record` VALUES (2, '11', '仓库a', 'test', '11112222', '运输', '2025-05-11 13:34:30.602241', '0xf3341ff7e9785c314081ee7f58ee8206b3131b7dc90d049094e9fa4867d2a328');
INSERT INTO `trace_record` VALUES (3, '444', '仓库a', 'test', '11112222', '运输', '2025-05-11 13:37:09.592941', '0xa5f8d6e3df56dc14400a1a23567c33d48c865dc973d8813a4df4030ff339140d');
INSERT INTO `trace_record` VALUES (4, '111', '仓库a', 'test', '11112222', '运输', '2025-05-11 13:38:40.547412', '0x9bdc51d6e31050ac5de6169a39faa2ae59838207112101fe55822eb4e511eec6');

-- ----------------------------
-- Table structure for trace_records
-- ----------------------------
DROP TABLE IF EXISTS `trace_records`;
CREATE TABLE `trace_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品ID',
  `stage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '阶段（如: 生产、运输、销售）',
  `operator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人员或公司',
  `timestamp` datetime NOT NULL COMMENT '操作时间',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详情说明，例如运输单号、温度记录等',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地点',
  `transaction_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区块链交易哈希',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品溯源记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trace_records
-- ----------------------------
INSERT INTO `trace_records` VALUES (1, '11112222', '运输', 'test', '2025-05-11 13:40:06', '1111', '仓库a', '0xfe1ea2977e9ea53a33a6cdebdc3ec8201c4a4f9d78db6835e2eb21fc3f91a25b');
INSERT INTO `trace_records` VALUES (2, '11112222', '运输2', 'test', '2025-05-11 13:46:02', 'erqerqw', '仓库B', '0x3f4db8392964b53d040e92bf5d17d91933e5bc6e8f8982cd0b94a1969dcd3591');
INSERT INTO `trace_records` VALUES (3, '111111111111', '生产', 'xx有限公司', '2025-05-11 13:58:13', '此产品已创建', '中国', '0x68c6fcb6ef5babb8b1d14b2284f3e19d888c6033411f23a7dc1eded2af5ea08c');
INSERT INTO `trace_records` VALUES (4, 'qqqq', '生产', 'xx有限公司', '2025-05-11 15:33:51', '此产品已创建', '中国', '0x2dc85d5ec065b30d509d79096f1f6e2ec76a8a813efc0cfc6da56edefc699d95');
INSERT INTO `trace_records` VALUES (5, 'qqqsadss', '生产', 'xx有限公司', '2025-05-12 05:09:50', '此产品已创建', '中国', '0x343ef33c07759a471735ba78c896c8062ad9d20115c64437f9e5a14ca70cdeb8');
INSERT INTO `trace_records` VALUES (6, 'qqqsadss', '运输2', 'test', '2025-05-12 05:49:28', '测试', '仓库B', '0x24c6d6f61e4dbf19c00f7c6301c40f9e11ed971ab40e74467a72567052ed828e');
INSERT INTO `trace_records` VALUES (7, 'qqqsadssaa', '生产', 'xx有限公司', '2025-05-12 06:36:51', '此产品已创建', '中国', '0xa682a274511d773ed1c0625138cc04ad32073b237c09feaf599c8a218f3c702e');
INSERT INTO `trace_records` VALUES (8, '1', '生产', 'xx有限公司', '2025-05-12 07:30:09', '此产品已创建', '中国', '0x2aff864af9a992245a24b264f0e2ab694497cf988fac9e2a93a324e8fd575fca');
INSERT INTO `trace_records` VALUES (9, '1', '入库', 'test', '2025-05-12 07:32:38', '测试', '仓库C', '0xf7cbf20f1dce92b4c9005a4a8a857951af0da2f1d893c96505096f6b5c748f54');
INSERT INTO `trace_records` VALUES (10, '1', '出库', 'test', '2025-05-12 07:42:24', '测试', '仓库C', '0x6bb02dbd17d036f5c68be24afd8068fe1ce3975e46f73befd1d5cceb0c83759b');
INSERT INTO `trace_records` VALUES (11, '111121322', '生产', 'xx有限公司', '2025-05-12 08:02:52', '此产品已创建', '中国', '0x052ab38f1af9f1fbd2d9fada056720e97612cb12c7f4d17b73ee8ad58685a5b6');
INSERT INTO `trace_records` VALUES (12, '111121322', '完成', 'xx有限公司', '2025-05-12 09:20:01', '产品已完成并归档', '中国', '0x9ff44c6fb234c02f5a95de1317f13d2c77ecc9a2ecb7efa3081b8b9377d044ca');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('consumer','supplier','merchant','regulator') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `userhash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `registered_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('active','suspended') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('06ecb522-3521-4777-85a0-63f6172bc93f', 'admin1', 'Yw103tWMxyZT2W2QKQq5JWvNRkgFJ+hoQ77SvQtP9l0W9T0BIehYE14fhRcwDQ/C', 'regulator', '', '', NULL, '2025-05-12 03:48:23', 'suspended');
INSERT INTO `user` VALUES ('3018a611-233b-4212-a178-8a95cf6c4dce', 'admin2', 'AHHIYIEYBsJZP0Z5vVEYxBSVsgQIAxGmG45Sd1nwZYwaTSNOQ5Ua6439qyGJWhF7', 'merchant', NULL, NULL, NULL, '2025-05-10 13:44:44', 'active');
INSERT INTO `user` VALUES ('38158386-f69b-4776-808a-5ee2b0d665f3', 'admin22', 'pdLhRH+CZGzVMWuuOx0s0W0IuF5nM10ln6i6vgpveQsQSaUyoemR+GE3jQt9tPiy', 'regulator', NULL, NULL, NULL, '2025-05-10 13:45:51', 'active');
INSERT INTO `user` VALUES ('bd1b49b0-b1fe-4ea9-a7c5-6d1880cc269d', 'test', 'xmS8IfWMSxxSCUC8TjEO0etpfu58DS4Cv+c6VIsXCu5IUQWFljAMAS6kUGWN5rrY', 'consumer', '', '', NULL, '2025-05-12 06:00:43', 'active');
INSERT INTO `user` VALUES ('f4bff1cb-97d1-4c57-b4ac-5b00e34dfb6e', 'admin', 'VJc+4KFrIT/Di/1QyfXAHeNtRI4esrsG1/Qt1nmmAt2sDthAdzr+gpGjpfcO13dG', 'regulator', 'adin@qq.com', '12312312311', NULL, '2025-05-12 20:15:05', 'active');

SET FOREIGN_KEY_CHECKS = 1;
