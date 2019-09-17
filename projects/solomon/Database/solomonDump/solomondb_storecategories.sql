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
-- Table structure for table `storecategories`
--

DROP TABLE IF EXISTS `storecategories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `storecategories` (
  `idstoreCategories` int(11) NOT NULL AUTO_INCREMENT,
  `idStore` int(11) NOT NULL,
  `category` varchar(45) NOT NULL,
  PRIMARY KEY (`idstoreCategories`),
  KEY `idStore_idx` (`idStore`),
  CONSTRAINT `idStore` FOREIGN KEY (`idStore`) REFERENCES `stores` (`idStores`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storecategories`
--

LOCK TABLES `storecategories` WRITE;
/*!40000 ALTER TABLE `storecategories` DISABLE KEYS */;
INSERT INTO `storecategories` VALUES (1,1,'R&D'),(2,1,'sensors'),(3,1,'mobile apps'),(4,1,'arduino'),(5,1,'android'),(6,2,'R&D'),(7,2,'Projects'),(8,2,'Sensors'),(9,2,'Laptops'),(10,2,'Software'),(11,4,'CEO'),(12,4,'Management'),(13,4,'Projects'),(14,4,'Money'),(15,4,'Employees'),(16,6,'French'),(17,6,'Sensors'),(18,6,'Electronics');
/*!40000 ALTER TABLE `storecategories` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-17 16:16:51
