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
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event` (
  `id` binary(16) NOT NULL,
  `date_event` date NOT NULL,
  `descrizione` text,
  `manager_id` binary(16) NOT NULL,
  `name_event` varchar(255) DEFAULT NULL,
  `space_id` binary(16) DEFAULT NULL,
  `state_event` enum('ATTESA_CATERING','ATTESA_PUBB','ATTESA_STANZA','CANCELLATO','IN_CREAZIONE','PUBBLICATO') NOT NULL,
  `type_event` enum('ALTRO','CLUB','FIRMACOPIE','USCITA_LIBRO') NOT NULL,
  `catering_id` binary(16) DEFAULT NULL,
  `club_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (_binary ' =\á<\Z‚@¤™+ \ÒQ','2026-01-28','AAAAAAAAAAAAAAAAA',_binary '+/\âCPF>‚\é\Z¸‹8ø','AAAAAAAAAAAAAAAAA',_binary 'JôE*!D%‘\Å#†}\ê>','ATTESA_PUBB','FIRMACOPIE',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',NULL),(_binary '%®\â\Ä\è\ÒI¾µ×¬d>Vlø','2026-01-30','TONNO!',_binary '+/\âCPF>‚\é\Z¸‹8ø','Evento Serata del tonno',_binary '\çüVø˜„JÌºüqKŒ\éfR','PUBBLICATO','CLUB',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary 'š‰\ÏyišNa \å\íl»A~\Ý'),(_binary 'nvmtCV’\ÔM™°ø}','2026-01-19','Discuteremo del personaggio del signorS nella saga di libri dei MeControTe',_binary '+/\âCPF>‚\é\Z¸‹8ø','Serata Club SignorS',_binary '\çüVø˜„JÌºüqKŒ\éfR','CANCELLATO','CLUB',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary 'š‰\ÏyišNa \å\íl»A~\Ý'),(_binary '\Êç£–\ÙRIk¸	öv}','2026-01-19','Amore mio',_binary '+/\âCPF>‚\é\Z¸‹8ø','Patate',_binary 'JôE*!D%‘\Å#†}\ê>','ATTESA_PUBB','FIRMACOPIE',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',NULL),(_binary '\Úü†\ÅôK\ã‰W%“h\é|','2026-01-24','aaaaaaaaaaaaaa',_binary '+/\âCPF>‚\é\Z¸‹8ø','Evento 10',_binary 'JôE*!D%‘\Å#†}\ê>','ATTESA_PUBB','FIRMACOPIE',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0',NULL);
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
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
