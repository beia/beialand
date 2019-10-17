-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: localhost    Database: solomondb
-- ------------------------------------------------------
-- Server version	8.0.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `beacons`
--

DROP TABLE IF EXISTS `beacons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `beacons` (
  `id` varchar(45) NOT NULL,
  `label` varchar(45) NOT NULL,
  `idMall` int(11) NOT NULL,
  `company` varchar(45) NOT NULL,
  `major` varchar(45) DEFAULT NULL,
  `minor` varchar(45) DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `layer` int(11) NOT NULL,
  `floor` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idMall3_idx` (`idMall`),
  CONSTRAINT `idMall3` FOREIGN KEY (`idMall`) REFERENCES `malls` (`idMalls`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beacons`
--

LOCK TABLES `beacons` WRITE;
/*!40000 ALTER TABLE `beacons` DISABLE KEYS */;
INSERT INTO `beacons` VALUES ('01d1b1ded3ea74f01f464d076f2f8138','Room1',1,'Estimote',NULL,NULL,0,0,0,0),('0Qq2','Zara',2,'Kontakt','33233','43572',44.430258,26.0513068,1,1),('2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,'Estimote',NULL,NULL,0,0,0,0),('44cf736f23ac5add5e8d15b021600d03','Room4',1,'Estimote',NULL,NULL,0,0,0,0),('580a946d72a0aea856aa9e0dd4beda37','Room6',1,'Estimote',NULL,NULL,0,0,0,0),('716cb1ae9008d60e5e40499ec67a772f','Room2',1,'Estimote',NULL,NULL,0,0,0,0),('aaai','McDonald\'s',2,'Kontakt','48319','1456',44.430237,26.0519288,1,1),('ee4d538a36c2bbac622980b351ba9a0a','Room3',1,'Estimote',NULL,NULL,0,0,0,0),('g0MK','Altex',2,'Kontakt','10428','42734',44.42998,26.0516688,1,1),('LKhV','Media Galaxy',2,'Kontakt','41302','22282',44.430423,26.0516788,1,1),('lZyt','Diverta',2,'Kontakt','51925','21455',44.429936,26.0516957,1,1),('MTFa','Starbucks',2,'Kontakt','59730','3532',44.430137,26.0520308,1,1),('PcNy','Auchan',2,'Kontakt','58450','19566',44.430293,26.0513188,2,0),('R4JH','Carturesti',2,'Kontakt','24334','57021',44.430195,26.0514088,1,1),('rrZd','Florarie',2,'Kontakt','39824','22135',44.430078,26.0515778,1,1),('tZF7','Stairs 1',2,'Kontakt','3204','63655',44.430456,26.0511848,2,0),('v7mz','Stairs 2',2,'Kontakt','25098','4628',44.430277,26.0518238,1,1),('y0PY','Gloria',2,'Kontakt','9597','46982',44.43067,26.0512978,2,0),('ygmN','Entrance',2,'Kontakt','55561','26706',44.430605,26.0514298,2,0),('zbCe','Pull&Bear',2,'Kontakt','60181','7706',44.430157,26.0514518,1,1);
/*!40000 ALTER TABLE `beacons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `malls`
--

DROP TABLE IF EXISTS `malls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `malls` (
  `idMalls` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  PRIMARY KEY (`idMalls`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `malls`
--

LOCK TABLES `malls` WRITE;
/*!40000 ALTER TABLE `malls` DISABLE KEYS */;
INSERT INTO `malls` VALUES (1,'VirtualMall',0,0),(2,'Beia',0,0);
/*!40000 ALTER TABLE `malls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specialofferscategories`
--

DROP TABLE IF EXISTS `specialofferscategories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
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
INSERT INTO `specialofferscategories` VALUES (1,1,1,'Electronics'),(2,1,1,'Vehicle'),(3,1,3,'Electronics'),(4,1,3,'Smartphone');
/*!40000 ALTER TABLE `specialofferscategories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storecategories`
--

DROP TABLE IF EXISTS `storecategories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `storecategories` (
  `idstoreCategories` int(11) NOT NULL AUTO_INCREMENT,
  `idStore` int(11) NOT NULL,
  `category` varchar(45) NOT NULL,
  PRIMARY KEY (`idstoreCategories`),
  KEY `idStore_idx` (`idStore`),
  CONSTRAINT `idStore` FOREIGN KEY (`idStore`) REFERENCES `stores` (`idStores`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storecategories`
--

LOCK TABLES `storecategories` WRITE;
/*!40000 ALTER TABLE `storecategories` DISABLE KEYS */;
INSERT INTO `storecategories` VALUES (19,1,'Electronics'),(20,1,'Games'),(21,1,'Laptops'),(22,1,'Monitors'),(23,1,'Smartphones'),(24,2,'Electronics'),(25,2,'Games'),(26,2,'Electric scooters'),(28,4,'Books'),(29,4,'Tea'),(30,4,'Coffee'),(31,5,'Coffee'),(32,5,'Tea'),(33,5,'Food'),(34,5,'Cakes'),(35,6,'Clothes'),(36,6,'Food'),(37,6,'Electronics'),(38,7,'Books'),(39,11,'Clothes'),(40,11,'Shoes'),(41,12,'Food'),(42,12,'Drinks'),(43,13,'Flowers'),(44,14,'Coffee'),(45,14,'Tea'),(46,14,'Cakes'),(47,15,'Clothes'),(48,15,'Shoes'),(49,6,'Sports'),(50,7,'Tea');
/*!40000 ALTER TABLE `storecategories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stores`
--

DROP TABLE IF EXISTS `stores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `stores` (
  `idStores` int(11) NOT NULL AUTO_INCREMENT,
  `idMall` int(11) NOT NULL,
  `idBeacon` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`idStores`),
  KEY `idMall_idx` (`idMall`),
  CONSTRAINT `idMall` FOREIGN KEY (`idMall`) REFERENCES `malls` (`idMalls`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stores`
--

LOCK TABLES `stores` WRITE;
/*!40000 ALTER TABLE `stores` DISABLE KEYS */;
INSERT INTO `stores` VALUES (1,2,'LKhV','Media Galaxy'),(2,2,'g0Mk','Altex'),(4,2,'lZyt','Diverta'),(5,2,'MTFa','Starbucks'),(6,2,'PcNy','Auchan'),(7,2,'R4JH','Carturesti'),(9,1,'44cf736f23ac5add5e8d15b021600d03','Room4'),(10,1,'580a946d72a0aea856aa9e0dd4beda37','Room6'),(11,2,'0Qq2','Zara'),(12,2,'aaai','McDonald\'s'),(13,2,'rrZd','Florarie'),(14,2,'y0PY','Gloria'),(15,2,'zbCe','Pull&Bear');
/*!40000 ALTER TABLE `stores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storespecialoffers`
--

DROP TABLE IF EXISTS `storespecialoffers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
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
INSERT INTO `storespecialoffers` VALUES (1,1,'Electric scooter 3500 lei',NULL),(3,1,'Iphone 11 pro 5500 lei',NULL),(4,2,'Samsung Galaxy Note 10 Plus 5000 lei',NULL),(5,2,'LG smart tv 4000 lei',NULL),(6,2,'Portable battery 200 lei',NULL),(7,4,'Book 1 50 lei',NULL),(8,4,'Book 2 50 lei',NULL),(9,4,'Book 3 40 lei',NULL),(10,4,'Book 4 30 lei',NULL),(11,7,'Twinings black tea 45 lei',NULL),(12,7,'Twinings green tea 45 lei',NULL),(13,7,'Twinings lemon tea 45 lei',NULL),(14,7,'Book 1 70 lei',NULL),(15,7,'Book 2 60 lei',NULL);
/*!40000 ALTER TABLE `storespecialoffers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userbeacontime`
--

DROP TABLE IF EXISTS `userbeacontime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `userbeacontime` (
  `idBeaconTime` int(11) NOT NULL AUTO_INCREMENT,
  `idUser` int(11) NOT NULL,
  `idBeacon` varchar(45) NOT NULL,
  `beaconLabel` varchar(45) NOT NULL,
  `idMall` int(11) NOT NULL,
  `timeSeconds` bigint(20) NOT NULL,
  PRIMARY KEY (`idBeaconTime`),
  KEY `idUser_idx` (`idUser`),
  KEY `idMall_idx` (`idMall`),
  KEY `beaconLabel2_idx` (`idBeacon`),
  CONSTRAINT `idBeacon2` FOREIGN KEY (`idBeacon`) REFERENCES `beacons` (`id`) ON DELETE CASCADE,
  CONSTRAINT `idMall2` FOREIGN KEY (`idMall`) REFERENCES `malls` (`idMalls`) ON DELETE CASCADE,
  CONSTRAINT `idUser2` FOREIGN KEY (`idUser`) REFERENCES `users` (`idusers`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=481 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userbeacontime`
--

LOCK TABLES `userbeacontime` WRITE;
/*!40000 ALTER TABLE `userbeacontime` DISABLE KEYS */;
INSERT INTO `userbeacontime` VALUES (381,15,'MTFa','Starbucks',2,56),(382,15,'tZF7','Stairs 1',2,28),(383,15,'aaai','McDonald\'s',2,0),(384,15,'R4JH','Carturesti',2,18),(385,15,'g0MK','Altex',2,0),(386,15,'ee4d538a36c2bbac622980b351ba9a0a','Room3',1,0),(387,15,'LKhV','Media Galaxy',2,1164),(388,15,'580a946d72a0aea856aa9e0dd4beda37','Room6',1,0),(389,15,'zbCe','Pull&Bear',2,54),(390,15,'44cf736f23ac5add5e8d15b021600d03','Room4',1,0),(391,15,'01d1b1ded3ea74f01f464d076f2f8138','Room1',1,0),(392,15,'0Qq2','Zara',2,52),(393,15,'y0PY','Gloria',2,0),(394,15,'ygmN','Entrance',2,0),(395,15,'2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,0),(396,15,'v7mz','Stairs 2',2,12),(397,15,'lZyt','Diverta',2,0),(398,15,'PcNy','Auchan',2,0),(399,15,'716cb1ae9008d60e5e40499ec67a772f','Room2',1,0),(400,15,'rrZd','Florarie',2,0),(401,16,'LKhV','Media Galaxy',2,5082),(402,16,'tZF7','Stairs 1',2,0),(403,16,'aaai','McDonald\'s',2,0),(404,16,'R4JH','Carturesti',2,0),(405,16,'MTFa','Starbucks',2,0),(406,16,'g0MK','Altex',2,0),(407,16,'ee4d538a36c2bbac622980b351ba9a0a','Room3',1,0),(408,16,'580a946d72a0aea856aa9e0dd4beda37','Room6',1,0),(409,16,'zbCe','Pull&Bear',2,0),(410,16,'44cf736f23ac5add5e8d15b021600d03','Room4',1,0),(411,16,'01d1b1ded3ea74f01f464d076f2f8138','Room1',1,0),(412,16,'0Qq2','Zara',2,0),(413,16,'y0PY','Gloria',2,0),(414,16,'ygmN','Entrance',2,0),(415,16,'2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,0),(416,16,'v7mz','Stairs 2',2,0),(417,16,'lZyt','Diverta',2,0),(418,16,'PcNy','Auchan',2,0),(419,16,'716cb1ae9008d60e5e40499ec67a772f','Room2',1,0),(420,16,'rrZd','Florarie',2,0),(421,17,'MTFa','Starbucks',2,6),(422,17,'tZF7','Stairs 1',2,0),(423,17,'aaai','McDonald\'s',2,0),(424,17,'R4JH','Carturesti',2,81),(425,17,'g0MK','Altex',2,0),(426,17,'ee4d538a36c2bbac622980b351ba9a0a','Room3',1,0),(427,17,'LKhV','Media Galaxy',2,0),(428,17,'580a946d72a0aea856aa9e0dd4beda37','Room6',1,0),(429,17,'zbCe','Pull&Bear',2,0),(430,17,'44cf736f23ac5add5e8d15b021600d03','Room4',1,0),(431,17,'01d1b1ded3ea74f01f464d076f2f8138','Room1',1,0),(432,17,'0Qq2','Zara',2,0),(433,17,'y0PY','Gloria',2,0),(434,17,'ygmN','Entrance',2,0),(435,17,'2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,0),(436,17,'v7mz','Stairs 2',2,14),(437,17,'lZyt','Diverta',2,0),(438,17,'PcNy','Auchan',2,0),(439,17,'716cb1ae9008d60e5e40499ec67a772f','Room2',1,0),(440,17,'rrZd','Florarie',2,226),(441,18,'rrZd','Florarie',2,60),(442,18,'tZF7','Stairs 1',2,57),(443,18,'aaai','McDonald\'s',2,0),(444,18,'R4JH','Carturesti',2,0),(445,18,'MTFa','Starbucks',2,0),(446,18,'g0MK','Altex',2,99),(447,18,'ee4d538a36c2bbac622980b351ba9a0a','Room3',1,0),(448,18,'LKhV','Media Galaxy',2,0),(449,18,'580a946d72a0aea856aa9e0dd4beda37','Room6',1,0),(450,18,'zbCe','Pull&Bear',2,0),(451,18,'44cf736f23ac5add5e8d15b021600d03','Room4',1,0),(452,18,'01d1b1ded3ea74f01f464d076f2f8138','Room1',1,0),(453,18,'0Qq2','Zara',2,0),(454,18,'y0PY','Gloria',2,37),(455,18,'ygmN','Entrance',2,283),(456,18,'2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,0),(457,18,'v7mz','Stairs 2',2,0),(458,18,'lZyt','Diverta',2,0),(459,18,'PcNy','Auchan',2,0),(460,18,'716cb1ae9008d60e5e40499ec67a772f','Room2',1,0),(461,19,'LKhV','Media Galaxy',2,443),(462,19,'tZF7','Stairs 1',2,0),(463,19,'aaai','McDonald\'s',2,0),(464,19,'R4JH','Carturesti',2,0),(465,19,'MTFa','Starbucks',2,0),(466,19,'g0MK','Altex',2,0),(467,19,'ee4d538a36c2bbac622980b351ba9a0a','Room3',1,0),(468,19,'580a946d72a0aea856aa9e0dd4beda37','Room6',1,0),(469,19,'zbCe','Pull&Bear',2,0),(470,19,'44cf736f23ac5add5e8d15b021600d03','Room4',1,0),(471,19,'01d1b1ded3ea74f01f464d076f2f8138','Room1',1,0),(472,19,'0Qq2','Zara',2,0),(473,19,'y0PY','Gloria',2,0),(474,19,'ygmN','Entrance',2,0),(475,19,'2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,0),(476,19,'v7mz','Stairs 2',2,0),(477,19,'lZyt','Diverta',2,0),(478,19,'PcNy','Auchan',2,0),(479,19,'716cb1ae9008d60e5e40499ec67a772f','Room2',1,0),(480,19,'rrZd','Florarie',2,0);
/*!40000 ALTER TABLE `userbeacontime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userlocations`
--

DROP TABLE IF EXISTS `userlocations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `userlocations` (
  `iduserLocations` int(11) NOT NULL AUTO_INCREMENT,
  `idUser` int(11) NOT NULL,
  `idBeacon` varchar(45) NOT NULL,
  `beaconLabel` varchar(45) NOT NULL,
  `idMall` int(11) NOT NULL,
  `zoneEntered` tinyint(4) NOT NULL,
  `time` varchar(45) NOT NULL,
  PRIMARY KEY (`iduserLocations`),
  KEY `idUser_idx` (`idUser`),
  KEY `idMall1_idx` (`idMall`),
  KEY `idBeacon1_idx` (`idBeacon`),
  CONSTRAINT `idBeacon1` FOREIGN KEY (`idBeacon`) REFERENCES `beacons` (`id`) ON DELETE CASCADE,
  CONSTRAINT `idMall1` FOREIGN KEY (`idMall`) REFERENCES `malls` (`idMalls`) ON DELETE CASCADE,
  CONSTRAINT `idUser1` FOREIGN KEY (`idUser`) REFERENCES `users` (`idusers`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1939 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userlocations`
--

LOCK TABLES `userlocations` WRITE;
/*!40000 ALTER TABLE `userlocations` DISABLE KEYS */;
INSERT INTO `userlocations` VALUES (1798,15,'MTFa','Starbucks',2,1,'Tue Oct 15 13:31:58 GMT+03:00 2019'),(1799,15,'MTFa','Starbucks',2,0,'Tue Oct 15 13:32:04 GMT+03:00 2019'),(1800,15,'v7mz','Stairs 2',2,1,'Tue Oct 15 13:38:05 GMT+03:00 2019'),(1801,15,'v7mz','Stairs 2',2,0,'Tue Oct 15 13:38:08 GMT+03:00 2019'),(1802,15,'MTFa','Starbucks',2,1,'Tue Oct 15 13:38:31 GMT+03:00 2019'),(1803,15,'MTFa','Starbucks',2,0,'Tue Oct 15 13:38:34 GMT+03:00 2019'),(1804,15,'MTFa','Starbucks',2,1,'Tue Oct 15 13:48:03 GMT+03:00 2019'),(1805,15,'MTFa','Starbucks',2,0,'Tue Oct 15 13:48:09 GMT+03:00 2019'),(1806,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 13:56:25 GMT+03:00 2019'),(1807,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 13:57:20 GMT+03:00 2019'),(1808,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 13:57:24 GMT+03:00 2019'),(1809,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 13:58:41 GMT+03:00 2019'),(1810,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 13:58:44 GMT+03:00 2019'),(1811,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 13:59:43 GMT+03:00 2019'),(1812,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 13:59:52 GMT+03:00 2019'),(1813,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:00:02 GMT+03:00 2019'),(1814,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:00:05 GMT+03:00 2019'),(1815,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:01:32 GMT+03:00 2019'),(1816,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:01:35 GMT+03:00 2019'),(1817,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:01:50 GMT+03:00 2019'),(1818,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:01:53 GMT+03:00 2019'),(1819,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:02:08 GMT+03:00 2019'),(1820,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:02:12 GMT+03:00 2019'),(1821,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:02:58 GMT+03:00 2019'),(1822,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:03:01 GMT+03:00 2019'),(1823,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:04:26 GMT+03:00 2019'),(1824,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:04:32 GMT+03:00 2019'),(1825,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:04:42 GMT+03:00 2019'),(1826,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:05:16 GMT+03:00 2019'),(1827,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:06:23 GMT+03:00 2019'),(1828,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:06:26 GMT+03:00 2019'),(1829,15,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:07:22 GMT+03:00 2019'),(1830,15,'MTFa','Starbucks',2,1,'Tue Oct 15 14:07:45 GMT+03:00 2019'),(1831,15,'MTFa','Starbucks',2,0,'Tue Oct 15 14:07:48 GMT+03:00 2019'),(1832,15,'MTFa','Starbucks',2,1,'Tue Oct 15 14:07:52 GMT+03:00 2019'),(1833,15,'MTFa','Starbucks',2,0,'Tue Oct 15 14:07:55 GMT+03:00 2019'),(1834,15,'MTFa','Starbucks',2,1,'Tue Oct 15 14:08:05 GMT+03:00 2019'),(1835,15,'MTFa','Starbucks',2,0,'Tue Oct 15 14:08:08 GMT+03:00 2019'),(1836,15,'MTFa','Starbucks',2,1,'Tue Oct 15 14:08:41 GMT+03:00 2019'),(1837,15,'MTFa','Starbucks',2,0,'Tue Oct 15 14:08:45 GMT+03:00 2019'),(1838,16,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:30:56 GMT+03:00 2019'),(1839,16,'LKhV','Media Galaxy',2,0,'Tue Oct 15 14:31:18 GMT+03:00 2019'),(1840,16,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:31:21 GMT+03:00 2019'),(1841,17,'MTFa','Starbucks',2,1,'Tue Oct 15 14:55:05 GMT+03:00 2019'),(1842,17,'MTFa','Starbucks',2,0,'Tue Oct 15 14:55:08 GMT+03:00 2019'),(1843,17,'aaai','McDonald\'s',2,1,'Tue Oct 15 14:55:18 GMT+03:00 2019'),(1844,17,'aaai','McDonald\'s',2,0,'Tue Oct 15 14:55:21 GMT+03:00 2019'),(1845,17,'aaai','McDonald\'s',2,1,'Tue Oct 15 14:55:24 GMT+03:00 2019'),(1846,17,'aaai','McDonald\'s',2,0,'Tue Oct 15 14:55:31 GMT+03:00 2019'),(1847,17,'R4JH','Carturesti',2,1,'Tue Oct 15 14:55:34 GMT+03:00 2019'),(1848,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:55:35 GMT+03:00 2019'),(1849,17,'ygmN','Entrance',2,1,'Tue Oct 15 14:55:37 GMT+03:00 2019'),(1850,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:55:38 GMT+03:00 2019'),(1851,17,'ygmN','Entrance',2,0,'Tue Oct 15 14:55:40 GMT+03:00 2019'),(1852,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:55:41 GMT+03:00 2019'),(1853,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:55:45 GMT+03:00 2019'),(1854,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:55:48 GMT+03:00 2019'),(1855,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:55:51 GMT+03:00 2019'),(1856,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:55:55 GMT+03:00 2019'),(1857,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:56:04 GMT+03:00 2019'),(1858,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:56:07 GMT+03:00 2019'),(1859,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:56:11 GMT+03:00 2019'),(1860,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:56:17 GMT+03:00 2019'),(1861,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:56:27 GMT+03:00 2019'),(1862,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:56:30 GMT+03:00 2019'),(1863,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:56:36 GMT+03:00 2019'),(1864,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:56:39 GMT+03:00 2019'),(1865,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:56:52 GMT+03:00 2019'),(1866,17,'R4JH','Carturesti',2,0,'Tue Oct 15 14:56:55 GMT+03:00 2019'),(1867,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:56:55 GMT+03:00 2019'),(1868,17,'R4JH','Carturesti',2,1,'Tue Oct 15 14:56:58 GMT+03:00 2019'),(1869,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:57:02 GMT+03:00 2019'),(1870,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:57:08 GMT+03:00 2019'),(1871,17,'R4JH','Carturesti',2,0,'Tue Oct 15 14:57:10 GMT+03:00 2019'),(1872,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:57:11 GMT+03:00 2019'),(1873,17,'rrZd','Florarie',2,1,'Tue Oct 15 14:57:14 GMT+03:00 2019'),(1874,17,'rrZd','Florarie',2,0,'Tue Oct 15 14:57:17 GMT+03:00 2019'),(1875,17,'v7mz','Stairs 2',2,1,'Tue Oct 15 14:57:20 GMT+03:00 2019'),(1876,17,'v7mz','Stairs 2',2,0,'Tue Oct 15 14:57:24 GMT+03:00 2019'),(1877,17,'v7mz','Stairs 2',2,1,'Tue Oct 15 14:57:30 GMT+03:00 2019'),(1878,17,'v7mz','Stairs 2',2,0,'Tue Oct 15 14:57:33 GMT+03:00 2019'),(1879,17,'LKhV','Media Galaxy',2,1,'Tue Oct 15 14:57:48 GMT+03:00 2019'),(1880,18,'ygmN','Entrance',2,1,'Tue Oct 15 15:25:39 GMT+03:00 2019'),(1881,18,'rrZd','Florarie',2,1,'Tue Oct 15 15:27:41 GMT+03:00 2019'),(1882,18,'tZF7','Stairs 1',2,1,'Tue Oct 15 15:27:46 GMT+03:00 2019'),(1883,18,'rrZd','Florarie',2,0,'Tue Oct 15 15:27:48 GMT+03:00 2019'),(1884,18,'tZF7','Stairs 1',2,0,'Tue Oct 15 15:27:56 GMT+03:00 2019'),(1885,18,'tZF7','Stairs 1',2,1,'Tue Oct 15 15:27:59 GMT+03:00 2019'),(1886,18,'y0PY','Gloria',2,1,'Tue Oct 15 15:28:07 GMT+03:00 2019'),(1887,18,'g0MK','Altex',2,1,'Tue Oct 15 15:28:09 GMT+03:00 2019'),(1888,18,'y0PY','Gloria',2,0,'Tue Oct 15 15:28:10 GMT+03:00 2019'),(1889,18,'g0MK','Altex',2,0,'Tue Oct 15 15:28:12 GMT+03:00 2019'),(1890,18,'y0PY','Gloria',2,1,'Tue Oct 15 15:28:14 GMT+03:00 2019'),(1891,18,'tZF7','Stairs 1',2,0,'Tue Oct 15 15:28:14 GMT+03:00 2019'),(1892,18,'y0PY','Gloria',2,0,'Tue Oct 15 15:28:29 GMT+03:00 2019'),(1893,18,'tZF7','Stairs 1',2,1,'Tue Oct 15 15:28:33 GMT+03:00 2019'),(1894,18,'rrZd','Florarie',2,1,'Tue Oct 15 15:28:33 GMT+03:00 2019'),(1895,18,'rrZd','Florarie',2,0,'Tue Oct 15 15:28:37 GMT+03:00 2019'),(1896,18,'tZF7','Stairs 1',2,0,'Tue Oct 15 15:29:01 GMT+03:00 2019'),(1897,18,'ygmN','Entrance',2,0,'Tue Oct 15 15:29:30 GMT+03:00 2019'),(1898,18,'ygmN','Entrance',2,1,'Tue Oct 15 15:29:33 GMT+03:00 2019'),(1899,18,'g0MK','Altex',2,1,'Tue Oct 15 15:29:39 GMT+03:00 2019'),(1900,18,'g0MK','Altex',2,0,'Tue Oct 15 15:29:42 GMT+03:00 2019'),(1901,18,'ygmN','Entrance',2,0,'Tue Oct 15 15:30:07 GMT+03:00 2019'),(1902,18,'ygmN','Entrance',2,1,'Tue Oct 15 15:30:10 GMT+03:00 2019'),(1903,18,'ygmN','Entrance',2,0,'Tue Oct 15 15:30:19 GMT+03:00 2019'),(1904,16,'LKhV','Media Galaxy',2,1,'Tue Oct 15 15:50:21 GMT+03:00 2019'),(1905,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 15:53:10 GMT+03:00 2019'),(1906,16,'LKhV','Media Galaxy',2,1,'Tue Oct 15 15:54:15 GMT+03:00 2019'),(1907,16,'LKhV','Media Galaxy',2,0,'Tue Oct 15 15:54:47 GMT+03:00 2019'),(1908,15,'0Qq2','Zara',2,1,'Tue Oct 15 16:56:22 GMT+03:00 2019'),(1909,15,'0Qq2','Zara',2,0,'Tue Oct 15 16:56:29 GMT+03:00 2019'),(1910,15,'zbCe','Pull&Bear',2,1,'Tue Oct 15 16:56:38 GMT+03:00 2019'),(1911,15,'0Qq2','Zara',2,1,'Tue Oct 15 16:56:39 GMT+03:00 2019'),(1912,15,'zbCe','Pull&Bear',2,0,'Tue Oct 15 16:56:41 GMT+03:00 2019'),(1913,15,'zbCe','Pull&Bear',2,1,'Tue Oct 15 16:56:49 GMT+03:00 2019'),(1914,15,'zbCe','Pull&Bear',2,0,'Tue Oct 15 16:56:52 GMT+03:00 2019'),(1915,15,'zbCe','Pull&Bear',2,1,'Tue Oct 15 16:57:11 GMT+03:00 2019'),(1916,15,'0Qq2','Zara',2,0,'Tue Oct 15 16:57:17 GMT+03:00 2019'),(1917,15,'zbCe','Pull&Bear',2,0,'Tue Oct 15 16:57:35 GMT+03:00 2019'),(1918,15,'R4JH','Carturesti',2,1,'Tue Oct 15 16:57:41 GMT+03:00 2019'),(1919,15,'rrZd','Florarie',2,1,'Tue Oct 15 16:57:44 GMT+03:00 2019'),(1920,15,'R4JH','Carturesti',2,0,'Tue Oct 15 16:57:50 GMT+03:00 2019'),(1921,15,'rrZd','Florarie',2,0,'Tue Oct 15 16:57:53 GMT+03:00 2019'),(1922,15,'v7mz','Stairs 2',2,1,'Tue Oct 15 16:58:01 GMT+03:00 2019'),(1923,15,'v7mz','Stairs 2',2,0,'Tue Oct 15 16:58:04 GMT+03:00 2019'),(1924,15,'tZF7','Stairs 1',2,1,'Tue Oct 15 16:58:14 GMT+03:00 2019'),(1925,15,'tZF7','Stairs 1',2,0,'Tue Oct 15 16:58:24 GMT+03:00 2019'),(1926,15,'PcNy','Auchan',2,1,'Tue Oct 15 16:58:30 GMT+03:00 2019'),(1927,15,'tZF7','Stairs 1',2,1,'Tue Oct 15 16:58:42 GMT+03:00 2019'),(1928,15,'ygmN','Entrance',2,1,'Tue Oct 15 16:58:44 GMT+03:00 2019'),(1929,15,'tZF7','Stairs 1',2,0,'Tue Oct 15 16:58:46 GMT+03:00 2019'),(1930,15,'LKhV','Media Galaxy',2,1,'Tue Oct 15 17:21:04 GMT+03:00 2019'),(1931,19,'LKhV','Media Galaxy',2,1,'Tue Oct 15 17:21:28 GMT+03:00 2019'),(1932,19,'LKhV','Media Galaxy',2,1,'Tue Oct 15 17:21:55 GMT+03:00 2019'),(1933,19,'LKhV','Media Galaxy',2,1,'Tue Oct 15 17:23:20 GMT+03:00 2019'),(1934,19,'LKhV','Media Galaxy',2,1,'Tue Oct 15 17:24:14 GMT+03:00 2019'),(1935,19,'LKhV','Media Galaxy',2,0,'Tue Oct 15 17:24:57 GMT+03:00 2019'),(1936,19,'LKhV','Media Galaxy',2,1,'Tue Oct 15 17:25:00 GMT+03:00 2019'),(1937,19,'LKhV','Media Galaxy',2,0,'Tue Oct 15 17:25:03 GMT+03:00 2019'),(1938,19,'LKhV','Media Galaxy',2,1,'Tue Oct 15 17:25:24 GMT+03:00 2019');
/*!40000 ALTER TABLE `userlocations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userpreferences`
--

DROP TABLE IF EXISTS `userpreferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `userpreferences` (
  `iduserPreferences` int(11) NOT NULL AUTO_INCREMENT,
  `idUser` int(11) NOT NULL,
  `category` varchar(45) NOT NULL,
  PRIMARY KEY (`iduserPreferences`),
  KEY `idUser_idx` (`idUser`),
  CONSTRAINT `idUser` FOREIGN KEY (`idUser`) REFERENCES `users` (`idusers`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userpreferences`
--

LOCK TABLES `userpreferences` WRITE;
/*!40000 ALTER TABLE `userpreferences` DISABLE KEYS */;
INSERT INTO `userpreferences` VALUES (64,15,'electronics'),(65,15,'clothes'),(66,15,'food'),(67,16,'cofee'),(68,16,'food'),(69,16,'clothes'),(70,17,'cofee'),(71,17,'clothes'),(72,17,'food'),(73,18,'electronics'),(74,18,'sports'),(75,18,'food'),(76,19,'shoes'),(77,19,'food'),(78,19,'clothes'),(79,21,'electronics'),(80,21,'shoes'),(81,21,'cofee'),(82,21,'electronics'),(83,21,'shoes'),(84,21,'cofee');
/*!40000 ALTER TABLE `userpreferences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `users` (
  `idusers` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `lastName` varchar(45) NOT NULL,
  `firstName` varchar(45) NOT NULL,
  `age` varchar(45) NOT NULL,
  `picture` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idusers`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (15,'bala','1234','Balanean','Cristian','22','C:\\Users\\Tehnic\\Desktop\\SolomonProfilePictures\\15.jpg'),(16,'apasat','beia','Pasat','Adrian','32','C:\\Users\\Tehnic\\Desktop\\SolomonProfilePictures\\16.jpg'),(17,'IoanaP95','beiasolomon1','Petre','Ioana','24','C:\\Users\\Tehnic\\Desktop\\SolomonProfilePictures\\17.jpg'),(18,'Cris','beia','Istrate','Cristiana','23',NULL),(19,'deni06','deni1997','Deni','Botezatu','22','C:\\Users\\Tehnic\\Desktop\\SolomonProfilePictures\\19.jpg'),(20,'Stefans\n','totaltelecom','Stefanescu','Stefan','60',NULL),(21,'Stefant','totaltelecom1','Stefanescu','Stefan','60',NULL);
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

-- Dump completed on 2019-10-17 12:12:45
