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
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` binary(16) NOT NULL,
  `reservation_date` date NOT NULL,
  `event_id` binary(16) NOT NULL,
  `seats` int DEFAULT NULL,
  `fascia_oraria` enum('GIORNATA','MATTINO','POMERIGGIO','SERA') NOT NULL,
  `state` enum('ACCEPTED','PENDING','REJECTED') NOT NULL,
  `event_name` varchar(255) NOT NULL,
  `manager_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (_binary 'RgéÉπJ¢ñfVã#l1','2026-01-16',_binary 'm]cCII≥ÄNáø{',25,'POMERIGGIO','REJECTED','Evento Prova','SignorS'),(_binary '\"\Œ\’D…≤ú∞Ú\0,','2026-01-19',_binary 'ı\√t:æ†K¿ç˝\‰V†éØ',20,'POMERIGGIO','REJECTED','Evento Prova Notifiche','SignorS'),(_binary 'ù˚\"\‚iLÇïÙ$*ÇøáV','2026-01-25',_binary 'ïß\Õ/F\‚¶\ÿDâ\Ó}\—\—',8,'GIORNATA','REJECTED','Evento Lucy','SignorS'),(_binary ' 7∂\»\ÏíO|∫2%ú@qá','2026-01-19',_binary '\ Á£ñ\ŸRIk∏	ˆv}',7,'GIORNATA','ACCEPTED','Patate','SignorS'),(_binary '*-uàUVM†©SPãx”Å','2026-01-19',_binary '\◊(òß4H•Ωs\Ï«µ\ƒ\ÊP',20,'MATTINO','REJECTED','Evento Test Notifiche Due','SignorS'),(_binary '0CC\ÊCÀØc\…#Gë','2026-01-22',_binary '\Â\ÃÛ\ZIÉ∞TWé\ÔÜ',20,'MATTINO','REJECTED','AAAAAAAAAAAAAAAA','SignorS'),(_binary 'M\ÏXazEr®ib`\"≠â\⁄','2026-01-24',_binary '\⁄¸Ü\≈ÙK\„âW%ìh\È|',7,'MATTINO','ACCEPTED','Evento 10','SignorS'),(_binary 'W`¡OX{J¶≠V˝òà¿T¡','2026-01-14',_binary 'u^N¨ìHà∏áá\Â\œ~X',5,'POMERIGGIO','REJECTED','eeee','SignorS'),(_binary 'Yst\·,\ÂF(Ω\"\’`ëäxˇ','2026-01-24',_binary '\"a˝Gä\‚@~øM#°KÑõ',20,'MATTINO','REJECTED','E¬£¬£¬£¬£¬£¬£¬£¬£¬£','SignorS'),(_binary 'a≠@\Ëy\'A¨_\'êwãK','2026-01-13',_binary '\—8e\Ô\ÈsOCé@<(N(≥4',1,'MATTINO','REJECTED','c','SignorS'),(_binary 'jä\⁄>ÛHJíØ\Â\ iQ/±\Ë','2026-01-12',_binary '$\ÍVl\Á9NÔ∞ÆjêhjÕô',1,'MATTINO','REJECTED','c','SignorS'),(_binary 'mÄ\‘MsLï£©ó˝G0\’','2026-01-18',_binary 'vÚã,ïNvã¶˙\–mWBÙ',15,'SERA','REJECTED','Prova Evento Club','SignorS'),(_binary 'mÅ}CºF@>ì\Î\Í\Â7\‹\‡\„','2026-01-30',_binary '%Æ\‚\ƒ\Ë\“Iæµ◊¨d>Vl¯',4,'SERA','ACCEPTED','Evento Serata del tonno','SignorS'),(_binary 'm©;ökN¢(Ú	F¡','2026-01-26',_binary 'º\◊	h\«E\0ëE\rçæ>bm',20,'MATTINO','REJECTED','club','SignorS'),(_binary 't.∂:\∆\–Fπ©~\ÍÂ≠£','2026-01-14',_binary ';&\Ï˛\∆KDmópˇiÚõïS',20,'MATTINO','REJECTED','evento','SignorS'),(_binary 'vó\…*UJf≤=∑;Aâp','2026-01-18',_binary '≤ÈåìmO\0á\ÎãÀá`=å',3,'GIORNATA','REJECTED','Evento Felice','SignorS'),(_binary '~õª-EvM\„ó \«\Í©','2026-01-16',_binary 'ò\ÂÿîãKäîõí!¶#h',1,'MATTINO','REJECTED','e','SignorS'),(_binary '\‡\ÈB0OLææÒ∫t∑§É','2026-01-19',_binary 'nÅvmtCVí\‘Mô∞¯}ù',20,'SERA','ACCEPTED','Serata Club SignorS','SignorS'),(_binary '\Î˘≈Ω+çJ∏°≠m\’C\“\‘U','2026-01-27',_binary '$1õY\’MUµ\‰\Œzπ&T\0',20,'POMERIGGIO','REJECTED','serata Club','SignorS'),(_binary '˜§i≤z\…D”ò\—5~J\⁄:','2026-01-28',_binary ' =\·<\ZÇ@§ô+è†\“Q',20,'MATTINO','ACCEPTED','AAAAAAAAAAAAAAAAA','SignorS');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-18 14:28:26
