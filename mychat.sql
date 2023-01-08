/*
 Navicat Premium Data Transfer

 Source Server         : 本地mysql
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : mychat

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 08/01/2023 17:49:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456');

-- ----------------------------
-- Table structure for ban
-- ----------------------------
DROP TABLE IF EXISTS `ban`;
CREATE TABLE `ban`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `days` int(11) NULL DEFAULT NULL,
  `start_time` timestamp(0) NULL DEFAULT NULL,
  `end_time` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ban
-- ----------------------------
INSERT INTO `ban` VALUES (1, 1, 2, '2022-11-05 03:46:50', '2022-11-07 03:46:50');
INSERT INTO `ban` VALUES (2, 1, 2, '2022-11-05 08:30:45', '2022-11-07 08:30:45');
INSERT INTO `ban` VALUES (3, 3, 2, '2022-11-11 03:36:23', '2022-11-13 03:36:23');

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `friend_id` int(11) NOT NULL,
  `gmt_create` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mul_index`(`user_id`, `friend_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES (1, 1, 2, NULL);
INSERT INTO `friend` VALUES (2, 3, 1, NULL);
INSERT INTO `friend` VALUES (3, 1, 4, NULL);
INSERT INTO `friend` VALUES (4, 1, 5, NULL);
INSERT INTO `friend` VALUES (5, 2, 4, NULL);
INSERT INTO `friend` VALUES (7, 2, 5, NULL);
INSERT INTO `friend` VALUES (9, 3, 2, NULL);

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群组名',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group
-- ----------------------------
INSERT INTO `group` VALUES (1, '小分队');
INSERT INTO `group` VALUES (4, '冲冲冲群');
INSERT INTO `group` VALUES (7, '测试群组');

-- ----------------------------
-- Table structure for group_message_0
-- ----------------------------
DROP TABLE IF EXISTS `group_message_0`;
CREATE TABLE `group_message_0`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL COMMENT '群组id',
  `sender` int(255) NOT NULL COMMENT '发送者id',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群组信息内容',
  `gmt_create` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_message_1
-- ----------------------------
DROP TABLE IF EXISTS `group_message_1`;
CREATE TABLE `group_message_1`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL COMMENT '群组id',
  `sender` int(255) NOT NULL COMMENT '发送者id',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群组信息内容',
  `gmt_create` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1598120560905158690 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_message_1
-- ----------------------------
INSERT INTO `group_message_1` VALUES (1588075651384999936, 1, 3, '冲冲冲', '2022-11-03 07:48:34');
INSERT INTO `group_message_1` VALUES (1588075698914852949, 1, 1, '大家一起加油', '2022-11-03 07:48:46');
INSERT INTO `group_message_1` VALUES (1588081222716751905, 1, 1, '收到，好的', '2022-11-03 08:10:41');
INSERT INTO `group_message_1` VALUES (1598120418378514494, 7, 1, '好好', '2022-12-01 01:02:53');
INSERT INTO `group_message_1` VALUES (1598120453904269379, 7, 1, '测试', '2022-12-01 01:03:02');
INSERT INTO `group_message_1` VALUES (1598120506253377581, 7, 3, '666', '2022-12-01 01:03:14');
INSERT INTO `group_message_1` VALUES (1598120560905158689, 1, 2, '111', '2022-12-01 01:03:28');

-- ----------------------------
-- Table structure for group_message_2
-- ----------------------------
DROP TABLE IF EXISTS `group_message_2`;
CREATE TABLE `group_message_2`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL COMMENT '群组id',
  `sender` int(255) NOT NULL COMMENT '发送者id',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群组信息内容',
  `gmt_create` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_message_ack
-- ----------------------------
DROP TABLE IF EXISTS `group_message_ack`;
CREATE TABLE `group_message_ack`  (
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `last_ack_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_user
-- ----------------------------
DROP TABLE IF EXISTS `group_user`;
CREATE TABLE `group_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL COMMENT '群组id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_user
-- ----------------------------
INSERT INTO `group_user` VALUES (1, 1, 1);
INSERT INTO `group_user` VALUES (2, 1, 4);
INSERT INTO `group_user` VALUES (5, 4, 1);
INSERT INTO `group_user` VALUES (6, 4, 4);
INSERT INTO `group_user` VALUES (7, 1, 2);
INSERT INTO `group_user` VALUES (9, 1, 3);
INSERT INTO `group_user` VALUES (12, 7, 3);
INSERT INTO `group_user` VALUES (15, 1, 6);
INSERT INTO `group_user` VALUES (18, 7, 1);

-- ----------------------------
-- Table structure for message_0
-- ----------------------------
DROP TABLE IF EXISTS `message_0`;
CREATE TABLE `message_0`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sender` int(11) NULL DEFAULT NULL COMMENT '发送者id',
  `receiver` int(11) NULL DEFAULT NULL COMMENT '接收者id',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容',
  `gmt_create` timestamp(0) NULL DEFAULT NULL COMMENT '消息创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message_1
-- ----------------------------
DROP TABLE IF EXISTS `message_1`;
CREATE TABLE `message_1`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sender` int(11) NULL DEFAULT NULL COMMENT '发送者id',
  `receiver` int(11) NULL DEFAULT NULL COMMENT '接收者id',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容',
  `gmt_create` timestamp(0) NULL DEFAULT NULL COMMENT '消息创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1612023569133338695 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_1
-- ----------------------------
INSERT INTO `message_1` VALUES (1588075511903420417, 3, 1, '你好呀', '2022-11-03 07:47:57');
INSERT INTO `message_1` VALUES (1588075614374461526, 1, 3, '你好你好', '2022-11-03 07:48:26');
INSERT INTO `message_1` VALUES (1590266245020647521, 3, 1, '一起加油，好好努力', '2022-11-09 08:53:13');
INSERT INTO `message_1` VALUES (1598120128044597308, 3, 1, 'Kim你好', '2022-12-01 01:01:39');
INSERT INTO `message_1` VALUES (1598120152291868744, 1, 3, '666', '2022-12-01 01:01:50');
INSERT INTO `message_1` VALUES (1598120197980422167, 3, 1, '你好你好', '2022-12-01 01:02:01');
INSERT INTO `message_1` VALUES (1612023523499311121, 1, 3, '2023，新的一年，一起加油', '2023-01-08 09:48:48');
INSERT INTO `message_1` VALUES (1612023569133338694, 3, 1, '勇往直前', '2023-01-08 09:49:03');

-- ----------------------------
-- Table structure for message_2
-- ----------------------------
DROP TABLE IF EXISTS `message_2`;
CREATE TABLE `message_2`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sender` int(11) NULL DEFAULT NULL COMMENT '发送者id',
  `receiver` int(11) NULL DEFAULT NULL COMMENT '接收者id',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容',
  `gmt_create` timestamp(0) NULL DEFAULT NULL COMMENT '消息创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1590266015286034439 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_2
-- ----------------------------
INSERT INTO `message_2` VALUES (1590266015277645887, 3, 2, '早上好，Tao', '2022-11-09 08:52:12');
INSERT INTO `message_2` VALUES (1590266015286034438, 3, 2, '好久不见', '2022-11-09 08:52:14');

-- ----------------------------
-- Table structure for message_ack
-- ----------------------------
DROP TABLE IF EXISTS `message_ack`;
CREATE TABLE `message_ack`  (
  `user_id` int(11) NOT NULL,
  `friend_id` int(11) NOT NULL,
  `last_ack_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`, `friend_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for request
-- ----------------------------
DROP TABLE IF EXISTS `request`;
CREATE TABLE `request`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NULL DEFAULT NULL COMMENT '添加者id',
  `add_id` int(11) NULL DEFAULT NULL COMMENT '被添加者id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of request
-- ----------------------------
INSERT INTO `request` VALUES (1, 1, 5);
INSERT INTO `request` VALUES (3, 1, 4);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'Kim', '123456', 'img/photo.jpg');
INSERT INTO `user` VALUES (2, 'Tao', '123456', 'img/flower.jpg');
INSERT INTO `user` VALUES (3, '小伙子', '123', 'img/th3.jpg');
INSERT INTO `user` VALUES (4, '小明', '123', 'img/car.jpg');
INSERT INTO `user` VALUES (5, '大成', '123', 'img/1.jpg');
INSERT INTO `user` VALUES (6, '路人', '123456', 'img/1.jpg');

SET FOREIGN_KEY_CHECKS = 1;
