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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` binary(16) NOT NULL,
  `email` varchar(150) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('BARISTA','CLIENTE','LIBRAIO','ORGANIZZATORE') NOT NULL,
  `surname` varchar(100) NOT NULL,
  `tel` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (_binary 'Qö†\–Cà]uÒ®\ ¸˙','giuliabono01@gmail.com','Giulia B.','$2a$10$tlybJxV2e8WPdlru1tQlWuTfK9cM7KazfCeDZRvh9uiIe4dtH4Zj.','CLIENTE','',NULL),(_binary '+/\‚CPF>Ç\È\Z∏ã8¯','Signor.Organizzatore@gmail.com','SignorS','$2a$10$AAM0IC00XfWWbE9YbOy0euK6if44rZU7bP9RH0DDpFkgiVCeBDWXa','ORGANIZZATORE','Organizzatore',NULL),(_binary '{b\–ûøL	Üzµ\'qÆA','giulia@gmail.com','Giulia','$2a$10$pI55GXjlAtndkPSuFw65U.Tci5xUGRJzlAxi.8PQyhjnWFYGyvxzq','CLIENTE','Bono',NULL),(_binary '~—•ÙyùDMáx†í\“','lucyIUM@gmail.com','lucy','$2a$10$LrINnGLJcTcSa1Uwz2a.8O7Ex9XggmoERUqeb77aBHnsiBDJgonbi','CLIENTE','Bono Argentero',NULL),(_binary '∑√ªÜªG:â¿›∫1NÖ\ÿ','argi.andre@gmail.com','Andrea','$2a$10$VtUCHovd5UYbCPYqlwQg2.LUsweRXiNmdoa3Z.IWGSr7ela.62AMi','CLIENTE','Argentero',NULL),(_binary '\ÕuD,G˙≥\√%&\‚–ò]','andrea.argentero@outlook.it','AndreaArgentero','$2a$10$PKaAJthlbSZw8YvTyBcuwOWPcLgQEGiWc7DmgdCXW4KC1FBvshh7S','CLIENTE','',NULL),(_binary '\–S\¬o*F”∫emt\·m‘ª','sig.lib@gmail.com','signora','$2a$10$s7otfH/7AQvN3C5n8dviIeNnLrBsE92PsX0PqHWTqE4Ah6QSaJWx6','LIBRAIO','libraia',NULL),(_binary '\„≥˛ØFé\◊\ÿFˆ˙Q','fred@gmail.com','Fred','$2a$10$CbwIEB5zleP2EORBsGI4B.lbLstVEJ1AS/dUKGwhdyDg7cf3WE3Ga','CLIENTE','Bono',NULL),(_binary '˛≠\Á;G¿M“∂ÄqñØG\Zy','signor.organizzatore2@gmail.com','signorS II','$2a$10$ofD6F6bFytcUPR0iYtt//.mrmDcYFidnVBSyUF17NSwI7cLbFKQcW','ORGANIZZATORE','Organizzatore',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
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
