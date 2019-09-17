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
-- Table structure for table `specialofferscategories`
--

DROP TABLE IF EXISTS `specialofferscategories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialofferscategories` (
  `idspecialofferscategories` int(11) NOT NULL AUTO_INCREMENT,
  `idStore` int(11) NOT NULL,
  `idSpecialOffer` int(11) NOT NULL,
  `category` varchar(45) NOT NULL,
  PRIMARY KEY (`idspecialofferscategories`),
  KEY `idStore4_idx` (`idStore`),
  KEY `idSpecialOffer_idx` (`idSpecialOffer`),
  CONSTRAINT `idSpecialOffer` FOREIGN KEY (`idSpecialOffer`) REFERENCES `storespecialoffers` (`idstoreSpecialOffers`),
  CONSTRAINT `idStore4` FOREIGN KEY (`idStore`) REFERENCES `stores` (`idStores`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specialofferscategories`
--

LOCK TABLES `specialofferscategories` WRITE;
/*!40000 ALTER TABLE `specialofferscategories` DISABLE KEYS */;
INSERT INTO `specialofferscategories` VALUES (1,1,1,'Software'),(2,1,1,'Sensors'),(3,1,3,'Electronics'),(4,1,3,'Articles');
/*!40000 ALTER TABLE `specialofferscategories` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-17 16:16:53
