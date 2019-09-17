-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: localhost    Database: solomondb
-- ------------------------------------------------------
-- Server version	8.0.17

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
-- Table structure for table `storespecialoffers`
--

DROP TABLE IF EXISTS `storespecialoffers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `storespecialoffers` (
  `idstoreSpecialOffers` int(11) NOT NULL AUTO_INCREMENT,
  `idStore` int(11) NOT NULL,
  `description` varchar(200) NOT NULL,
  `photo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idstoreSpecialOffers`),
  KEY `idStore_idx` (`idStore`),
  CONSTRAINT `idStore3` FOREIGN KEY (`idStore`) REFERENCES `stores` (`idStores`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storespecialoffers`
--

LOCK TABLES `storespecialoffers` WRITE;
/*!40000 ALTER TABLE `storespecialoffers` DISABLE KEYS */;
INSERT INTO `storespecialoffers` VALUES (1,1,'Solomon project is a very interesting and complex project about smart shopping',NULL),(3,1,'POC test offer 1',NULL),(4,2,'Management test offer 1',NULL),(5,2,'Management test offer 2',NULL),(6,2,'Management test offer 3',NULL),(7,4,'CEO test offer 1',NULL),(8,4,'CEO test offer 2',NULL),(9,4,'CEO test offer 3',NULL),(10,4,'CEO test offer 4',NULL),(11,7,'Proiecte test offer 1',NULL),(12,7,'Proiecte test offer 2',NULL),(13,7,'Proiecte test offer 3',NULL),(14,7,'Proiecte test offer 4',NULL),(15,7,'Proiecte test offer 5',NULL);
/*!40000 ALTER TABLE `storespecialoffers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-17 16:16:50
