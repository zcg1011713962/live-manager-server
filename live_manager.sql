-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: 192.168.127.131    Database: live_manager
-- ------------------------------------------------------
-- Server version	8.0.39-0ubuntu0.20.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `gift_log`
--

DROP TABLE IF EXISTS `gift_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gift_log` (
  `room_id` bigint NOT NULL COMMENT '房间号',
  `anchor_name` varchar(255) NOT NULL COMMENT '主播昵称',
  `sender_id` bigint NOT NULL COMMENT '送礼人唯一ID',
  `sender_name` varchar(255) NOT NULL COMMENT '送礼人昵称',
  `sender_avatar_url` varchar(255) DEFAULT NULL COMMENT '发言人头像',
  `gift_id` bigint NOT NULL COMMENT '道具（礼物）ID',
  `amount` int NOT NULL DEFAULT '1' COMMENT '消费数量',
  `combo_count` int DEFAULT '0' COMMENT '送礼连击数',
  `charge_policy` varchar(50) NOT NULL COMMENT '扣费策略 (1002-钻石, 1003-金币, 1018-免费背包礼物, 3008-金豆)',
  `total_payment` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总支付',
  `total_gems` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '主播总获得宝石',
  `sent_timestamp` bigint NOT NULL COMMENT '发送时间戳 (秒)',
  PRIMARY KEY (`room_id`,`gift_id`,`sender_id`,`sent_timestamp`),
  KEY `idx_anchor_name` (`anchor_name`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_sent_timestamp` (`sent_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='直播礼物记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gift_log`
--

LOCK TABLES `gift_log` WRITE;
/*!40000 ALTER TABLE `gift_log` DISABLE KEYS */;
INSERT INTO `gift_log` VALUES (123456789,'XXX主播',0,'王哥','',0,1,0,'',1.00,1.00,0);
/*!40000 ALTER TABLE `gift_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone_num` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `gender` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `birthdate` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `phone_num` (`phone_num`,`user_type`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2,'root','yx168168','admin','张三','13347310633','abc@qq.com',NULL,NULL,'2024-11-15 11:39:38',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'live_manager'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-12 15:30:29
