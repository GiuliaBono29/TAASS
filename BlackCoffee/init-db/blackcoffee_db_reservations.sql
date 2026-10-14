-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: blackcoffee_db
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` binary(16) NOT NULL,
  `reservation_date` date DEFAULT NULL,
  `owner_id` binary(16) NOT NULL,
  `fascia_oraria` enum('GIORNATA','MATTINO','POMERIGGIO','SERA') NOT NULL,
  `table_id` binary(16) DEFAULT NULL,
  `seats` int DEFAULT NULL,
  `event_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKritru50ljg3q6ytxriivjlmq6` (`table_id`),
  CONSTRAINT `FKritru50ljg3q6ytxriivjlmq6` FOREIGN KEY (`table_id`) REFERENCES `tables` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (_binary '»É¦PHÕµ0Ÿ¹>d','2026-01-19',_binary '\ĞS\Âo*FÓºemt\ámÔ»','SERA',_binary '¤\Ã\ÏüB°¿\Å\æˆ\ÚÇ',20,_binary 'nvmtCV’\ÔM™°ø}'),(_binary '\É×‹-\éKô‰\ÜF[Z','2026-01-30',_binary '\ĞS\Âo*FÓºemt\ámÔ»','SERA',_binary '¤\Ã\ÏüB°¿\Å\æˆ\ÚÇ',4,_binary '%®\â\Ä\è\ÒI¾µ×¬d>Vlø'),(_binary '>\æ~M«qN\n’#\åƒ\Ç\ŞcH','2026-01-30',_binary '\ĞS\Âo*FÓºemt\ámÔ»','SERA',_binary 'r}\ÜAP¹š¨&F',4,_binary '%®\â\Ä\è\ÒI¾µ×¬d>Vlø'),(_binary 'ƒğƒSJ¯­\"G\Ñı#','2026-01-30',_binary '\ã³ş¯F\×\ØFöúQ','GIORNATA',_binary ')E3W\åKÄ³\âe\ÃS’\İ',20,NULL),(_binary 'ƒü\äA*´EÃ¾c\Åx9¶','2026-01-14',_binary '~Ñ¥ôyDM‡x ’\Ò','GIORNATA',_binary ')E3W\åKÄ³\âe\ÃS’\İ',8,NULL),(_binary 'œ`šß»:HI¥xù¶','2026-01-14',_binary '~Ñ¥ôyDM‡x ’\Ò','GIORNATA',_binary 'ı‡TµÁ´I\\—¶ªku ',8,NULL),(_binary '\Ê`ZJ3µMsµı‡\äi\í\\¬','2026-01-19',_binary '\ĞS\Âo*FÓºemt\ámÔ»','SERA',_binary 'r}\ÜAP¹š¨&F',20,_binary 'nvmtCV’\ÔM™°ø}'),(_binary '\Ñ&°ø1OI¼Šğ;ôi\\','2026-01-14',_binary '~Ñ¥ôyDM‡x ’\Ò','GIORNATA',_binary 'r}\ÜAP¹š¨&F',8,NULL),(_binary '\ã¤\Ñğ\Ó7IU\ß\"Ò©A@','2026-01-14',_binary '~Ñ¥ôyDM‡x ’\Ò','GIORNATA',_binary '¤\Ã\ÏüB°¿\Å\æˆ\ÚÇ',8,NULL),(_binary 'üÀ!v\ÖI\æ‰+\ÂğlòuE','2026-01-30',_binary '\ã³ş¯F\×\ØFöúQ','GIORNATA',_binary 'ı‡TµÁ´I\\—¶ªku ',10,NULL);
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-18 14:28:25
