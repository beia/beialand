-- MySQL dump 10.13  Distrib 8.0.15, for Win64 (x86_64)
--
-- Host: localhost    Database: solomondb
-- ------------------------------------------------------
-- Server version	8.0.15

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
INSERT INTO `beacons` VALUES ('01d1b1ded3ea74f01f464d076f2f8138','Room1',1,'Estimote',NULL,NULL,0,0,0,0),('0Qq2','Sales',2,'Kontakt','33233','43572',44.430258,26.0513068,1,1),('2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,'Estimote',NULL,NULL,0,0,0,0),('44cf736f23ac5add5e8d15b021600d03','Room4',1,'Estimote',NULL,NULL,0,0,0,0),('580a946d72a0aea856aa9e0dd4beda37','Room6',1,'Estimote',NULL,NULL,0,0,0,0),('716cb1ae9008d60e5e40499ec67a772f','Room2',1,'Estimote',NULL,NULL,0,0,0,0),('aaai','Oficiu 1',2,'Kontakt','48319','1456',44.430237,26.0519288,1,1),('ee4d538a36c2bbac622980b351ba9a0a','Room3',1,'Estimote',NULL,NULL,0,0,0,0),('g0MK','Management room',2,'Kontakt','10428','42734',44.42998,26.0516688,1,1),('LKhV','POC',2,'Kontakt','41302','22282',44.430423,26.0516788,1,1),('lZyt','CEO room',2,'Kontakt','51925','21455',44.429936,26.0516957,1,1),('MTFa','Conference room',2,'Kontakt','59730','3532',44.430137,26.0520308,1,1),('PcNy','POC 2',2,'Kontakt','58450','19566',44.430293,26.0513188,2,0),('R4JH','Proiecte',2,'Kontakt','24334','57021',44.430195,26.0514088,1,1),('rrZd','SERE',2,'Kontakt','39824','22135',44.430078,26.0515778,1,1),('tZF7','Scara 1',2,'Kontakt','3204','63655',44.430456,26.0511848,2,0),('v7mz','Scara2',2,'Kontakt','25098','4628',44.430277,26.0518238,1,1),('y0PY','Garaj',2,'Kontakt','9597','46982',44.43067,26.0512978,2,0),('ygmN','Intrare',2,'Kontakt','55561','26706',44.430605,26.0514298,2,0),('zbCe','Secretariat',2,'Kontakt','60181','7706',44.430157,26.0514518,1,1);
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
INSERT INTO `specialofferscategories` VALUES (1,1,1,'Software'),(2,1,1,'Sensors'),(3,1,3,'Electronics'),(4,1,3,'Articles');
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stores`
--

LOCK TABLES `stores` WRITE;
/*!40000 ALTER TABLE `stores` DISABLE KEYS */;
INSERT INTO `stores` VALUES (1,2,'LKhV','Poc'),(2,2,'g0Mk','Management room'),(4,2,'lZyt','CEO room'),(5,2,'MTFa','Conference room'),(6,2,'PcNy','POC 2'),(7,2,'R4JH','Proiecte'),(9,1,'44cf736f23ac5add5e8d15b021600d03','Room4'),(10,1,'580a946d72a0aea856aa9e0dd4beda37','Room6');
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
INSERT INTO `storespecialoffers` VALUES (1,1,'Solomon project is a very interesting and complex project about smart shopping',NULL),(3,1,'POC test offer 1',NULL),(4,2,'Management test offer 1',NULL),(5,2,'Management test offer 2',NULL),(6,2,'Management test offer 3',NULL),(7,4,'CEO test offer 1',NULL),(8,4,'CEO test offer 2',NULL),(9,4,'CEO test offer 3',NULL),(10,4,'CEO test offer 4',NULL),(11,7,'Proiecte test offer 1',NULL),(12,7,'Proiecte test offer 2',NULL),(13,7,'Proiecte test offer 3',NULL),(14,7,'Proiecte test offer 4',NULL),(15,7,'Proiecte test offer 5',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userbeacontime`
--

LOCK TABLES `userbeacontime` WRITE;
/*!40000 ALTER TABLE `userbeacontime` DISABLE KEYS */;
INSERT INTO `userbeacontime` VALUES (241,8,'MTFa','Conference room',2,5103),(242,8,'tZF7','Scara 1',2,0),(243,8,'aaai','Oficiu 1',2,6),(244,8,'R4JH','Proiecte',2,0),(245,8,'g0MK','Management room',2,0),(246,8,'ee4d538a36c2bbac622980b351ba9a0a','Room3',1,0),(247,8,'LKhV','POC',2,0),(248,8,'580a946d72a0aea856aa9e0dd4beda37','Room6',1,0),(249,8,'zbCe','Secretariat',2,0),(250,8,'44cf736f23ac5add5e8d15b021600d03','Room4',1,0),(251,8,'01d1b1ded3ea74f01f464d076f2f8138','Room1',1,0),(252,8,'0Qq2','Sales',2,0),(253,8,'y0PY','Garaj',2,0),(254,8,'ygmN','Intrare',2,420),(255,8,'2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,0),(256,8,'v7mz','Scara2',2,14),(257,8,'lZyt','CEO room',2,0),(258,8,'PcNy','POC 2',2,0),(259,8,'716cb1ae9008d60e5e40499ec67a772f','Room2',1,0),(260,8,'rrZd','SERE',2,0),(261,9,'LKhV','POC',2,84),(262,9,'tZF7','Scara 1',2,0),(263,9,'aaai','Oficiu 1',2,12),(264,9,'R4JH','Proiecte',2,12),(265,9,'MTFa','Conference room',2,0),(266,9,'g0MK','Management room',2,0),(267,9,'ee4d538a36c2bbac622980b351ba9a0a','Room3',1,0),(268,9,'580a946d72a0aea856aa9e0dd4beda37','Room6',1,0),(269,9,'zbCe','Secretariat',2,0),(270,9,'44cf736f23ac5add5e8d15b021600d03','Room4',1,0),(271,9,'01d1b1ded3ea74f01f464d076f2f8138','Room1',1,0),(272,9,'0Qq2','Sales',2,0),(273,9,'y0PY','Garaj',2,0),(274,9,'ygmN','Intrare',2,0),(275,9,'2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,0),(276,9,'v7mz','Scara2',2,0),(277,9,'lZyt','CEO room',2,0),(278,9,'PcNy','POC 2',2,0),(279,9,'716cb1ae9008d60e5e40499ec67a772f','Room2',1,0),(280,9,'rrZd','SERE',2,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=1567 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userlocations`
--

LOCK TABLES `userlocations` WRITE;
/*!40000 ALTER TABLE `userlocations` DISABLE KEYS */;
INSERT INTO `userlocations` VALUES (1289,8,'MTFa','Conference room',2,1,'Thu Sep 05 16:16:44 GMT+03:00 2019'),(1290,8,'MTFa','Conference room',2,0,'Thu Sep 05 16:16:53 GMT+03:00 2019'),(1291,8,'MTFa','Conference room',2,1,'Thu Sep 05 16:16:56 GMT+03:00 2019'),(1292,8,'MTFa','Conference room',2,1,'Thu Sep 05 16:17:47 GMT+03:00 2019'),(1293,8,'ygmN','Intrare',2,1,'Thu Sep 05 16:22:15 GMT+03:00 2019'),(1294,8,'ygmN','Intrare',2,0,'Thu Sep 05 16:22:19 GMT+03:00 2019'),(1295,8,'ygmN','Intrare',2,1,'Thu Sep 05 16:22:22 GMT+03:00 2019'),(1296,8,'ygmN','Intrare',2,0,'Thu Sep 05 16:22:28 GMT+03:00 2019'),(1297,8,'ygmN','Intrare',2,1,'Thu Sep 05 16:22:31 GMT+03:00 2019'),(1298,8,'ygmN','Intrare',2,0,'Thu Sep 05 16:22:34 GMT+03:00 2019'),(1299,8,'ygmN','Intrare',2,1,'Thu Sep 05 16:22:37 GMT+03:00 2019'),(1300,8,'ygmN','Intrare',2,0,'Thu Sep 05 16:22:50 GMT+03:00 2019'),(1301,8,'ygmN','Intrare',2,1,'Thu Sep 05 16:22:56 GMT+03:00 2019'),(1302,8,'ygmN','Intrare',2,0,'Thu Sep 05 16:22:59 GMT+03:00 2019'),(1303,8,'ygmN','Intrare',2,1,'Thu Sep 05 16:23:06 GMT+03:00 2019'),(1304,8,'ygmN','Intrare',2,0,'Thu Sep 05 16:23:13 GMT+03:00 2019'),(1305,8,'MTFa','Conference room',2,1,'Thu Sep 05 17:38:37 GMT+03:00 2019'),(1306,8,'MTFa','Conference room',2,0,'Thu Sep 05 17:38:40 GMT+03:00 2019'),(1307,8,'MTFa','Conference room',2,1,'Thu Sep 12 18:39:19 GMT+03:00 2019'),(1308,8,'aaai','Oficiu 1',2,1,'Thu Sep 12 18:39:23 GMT+03:00 2019'),(1309,8,'v7mz','Scara2',2,1,'Thu Sep 12 18:39:25 GMT+03:00 2019'),(1310,8,'MTFa','Conference room',2,0,'Thu Sep 12 18:39:26 GMT+03:00 2019'),(1311,8,'aaai','Oficiu 1',2,0,'Thu Sep 12 18:39:26 GMT+03:00 2019'),(1312,8,'v7mz','Scara2',2,0,'Thu Sep 12 18:39:32 GMT+03:00 2019'),(1313,8,'LKhV','POC',2,1,'Thu Sep 12 18:39:37 GMT+03:00 2019'),(1314,9,'LKhV','POC',2,1,'Thu Sep 12 18:41:44 GMT+03:00 2019'),(1315,9,'LKhV','POC',2,0,'Thu Sep 12 18:41:47 GMT+03:00 2019'),(1316,9,'LKhV','POC',2,1,'Thu Sep 12 18:41:56 GMT+03:00 2019'),(1317,9,'LKhV','POC',2,0,'Thu Sep 12 18:42:14 GMT+03:00 2019'),(1318,9,'R4JH','Proiecte',2,1,'Thu Sep 12 18:42:20 GMT+03:00 2019'),(1319,9,'R4JH','Proiecte',2,0,'Thu Sep 12 18:42:23 GMT+03:00 2019'),(1320,9,'aaai','Oficiu 1',2,1,'Thu Sep 12 18:42:26 GMT+03:00 2019'),(1321,9,'aaai','Oficiu 1',2,0,'Thu Sep 12 18:42:29 GMT+03:00 2019'),(1322,9,'MTFa','Conference room',2,1,'Thu Sep 12 18:42:31 GMT+03:00 2019'),(1323,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:19:37 GMT+03:00 2019'),(1324,8,'MTFa','Conference room',2,0,'Tue Sep 17 10:19:40 GMT+03:00 2019'),(1325,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:19:46 GMT+03:00 2019'),(1326,8,'MTFa','Conference room',2,0,'Tue Sep 17 10:19:49 GMT+03:00 2019'),(1327,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:19:58 GMT+03:00 2019'),(1328,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:20:33 GMT+03:00 2019'),(1329,8,'MTFa','Conference room',2,0,'Tue Sep 17 10:20:39 GMT+03:00 2019'),(1330,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:26:52 GMT+03:00 2019'),(1331,8,'MTFa','Conference room',2,0,'Tue Sep 17 10:26:58 GMT+03:00 2019'),(1332,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:27:02 GMT+03:00 2019'),(1333,8,'MTFa','Conference room',2,0,'Tue Sep 17 10:27:05 GMT+03:00 2019'),(1334,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:27:12 GMT+03:00 2019'),(1335,8,'MTFa','Conference room',2,0,'Tue Sep 17 10:27:18 GMT+03:00 2019'),(1336,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:27:21 GMT+03:00 2019'),(1337,8,'MTFa','Conference room',2,0,'Tue Sep 17 10:27:30 GMT+03:00 2019'),(1338,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:27:33 GMT+03:00 2019'),(1339,8,'MTFa','Conference room',2,0,'Tue Sep 17 10:27:36 GMT+03:00 2019'),(1340,8,'MTFa','Conference room',2,1,'Tue Sep 17 10:27:40 GMT+03:00 2019'),(1341,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:23:35 GMT+03:00 2019'),(1342,8,'MTFa','Conference room',2,0,'Tue Sep 17 12:23:38 GMT+03:00 2019'),(1343,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:29:58 GMT+03:00 2019'),(1344,8,'MTFa','Conference room',2,0,'Tue Sep 17 12:30:01 GMT+03:00 2019'),(1345,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:34:38 GMT+03:00 2019'),(1346,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:42:24 GMT+03:00 2019'),(1347,8,'MTFa','Conference room',2,0,'Tue Sep 17 12:42:30 GMT+03:00 2019'),(1348,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:49:49 GMT+03:00 2019'),(1349,8,'MTFa','Conference room',2,0,'Tue Sep 17 12:49:53 GMT+03:00 2019'),(1350,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:49:56 GMT+03:00 2019'),(1351,8,'MTFa','Conference room',2,0,'Tue Sep 17 12:50:05 GMT+03:00 2019'),(1352,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:50:21 GMT+03:00 2019'),(1353,8,'MTFa','Conference room',2,0,'Tue Sep 17 12:50:24 GMT+03:00 2019'),(1354,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:59:01 GMT+03:00 2019'),(1355,8,'MTFa','Conference room',2,0,'Tue Sep 17 12:59:04 GMT+03:00 2019'),(1356,8,'MTFa','Conference room',2,1,'Tue Sep 17 12:59:12 GMT+03:00 2019'),(1357,8,'MTFa','Conference room',2,0,'Tue Sep 17 12:59:14 GMT+03:00 2019'),(1358,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:02:47 GMT+03:00 2019'),(1359,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:02:51 GMT+03:00 2019'),(1360,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:03:13 GMT+03:00 2019'),(1361,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:03:16 GMT+03:00 2019'),(1362,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:04:27 GMT+03:00 2019'),(1363,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:04:30 GMT+03:00 2019'),(1364,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:07:16 GMT+03:00 2019'),(1365,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:07:22 GMT+03:00 2019'),(1366,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:09:38 GMT+03:00 2019'),(1367,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:09:47 GMT+03:00 2019'),(1368,9,'MTFa','Conference room',2,1,'Tue Sep 17 13:18:56 GMT+03:00 2019'),(1369,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:26:12 GMT+03:00 2019'),(1370,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:26:15 GMT+03:00 2019'),(1371,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:26:27 GMT+03:00 2019'),(1372,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:26:30 GMT+03:00 2019'),(1373,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:26:33 GMT+03:00 2019'),(1374,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:26:36 GMT+03:00 2019'),(1375,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:26:52 GMT+03:00 2019'),(1376,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:26:58 GMT+03:00 2019'),(1377,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:27:02 GMT+03:00 2019'),(1378,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:27:05 GMT+03:00 2019'),(1379,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:27:08 GMT+03:00 2019'),(1380,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:27:12 GMT+03:00 2019'),(1381,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:27:15 GMT+03:00 2019'),(1382,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:27:18 GMT+03:00 2019'),(1383,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:27:24 GMT+03:00 2019'),(1384,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:27:28 GMT+03:00 2019'),(1385,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:27:31 GMT+03:00 2019'),(1386,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:27:44 GMT+03:00 2019'),(1387,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:27:57 GMT+03:00 2019'),(1388,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:28:03 GMT+03:00 2019'),(1389,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:28:25 GMT+03:00 2019'),(1390,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:28:28 GMT+03:00 2019'),(1391,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:28:31 GMT+03:00 2019'),(1392,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:28:40 GMT+03:00 2019'),(1393,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:28:43 GMT+03:00 2019'),(1394,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:28:46 GMT+03:00 2019'),(1395,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:28:49 GMT+03:00 2019'),(1396,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:28:53 GMT+03:00 2019'),(1397,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:29:02 GMT+03:00 2019'),(1398,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:29:08 GMT+03:00 2019'),(1399,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:29:14 GMT+03:00 2019'),(1400,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:29:21 GMT+03:00 2019'),(1401,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:29:24 GMT+03:00 2019'),(1402,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:29:33 GMT+03:00 2019'),(1403,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:29:37 GMT+03:00 2019'),(1404,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:29:40 GMT+03:00 2019'),(1405,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:29:46 GMT+03:00 2019'),(1406,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:29:49 GMT+03:00 2019'),(1407,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:30:05 GMT+03:00 2019'),(1408,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:30:08 GMT+03:00 2019'),(1409,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:30:17 GMT+03:00 2019'),(1410,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:30:20 GMT+03:00 2019'),(1411,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:30:24 GMT+03:00 2019'),(1412,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:30:27 GMT+03:00 2019'),(1413,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:30:33 GMT+03:00 2019'),(1414,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:30:36 GMT+03:00 2019'),(1415,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:30:45 GMT+03:00 2019'),(1416,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:30:49 GMT+03:00 2019'),(1417,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:31:04 GMT+03:00 2019'),(1418,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:31:07 GMT+03:00 2019'),(1419,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:31:10 GMT+03:00 2019'),(1420,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:31:13 GMT+03:00 2019'),(1421,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:31:16 GMT+03:00 2019'),(1422,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:31:29 GMT+03:00 2019'),(1423,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:31:45 GMT+03:00 2019'),(1424,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:31:48 GMT+03:00 2019'),(1425,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:32:04 GMT+03:00 2019'),(1426,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:32:07 GMT+03:00 2019'),(1427,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:33:19 GMT+03:00 2019'),(1428,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:33:25 GMT+03:00 2019'),(1429,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:33:29 GMT+03:00 2019'),(1430,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:33:35 GMT+03:00 2019'),(1431,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:33:45 GMT+03:00 2019'),(1432,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:33:48 GMT+03:00 2019'),(1433,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:33:51 GMT+03:00 2019'),(1434,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:33:55 GMT+03:00 2019'),(1435,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:33:58 GMT+03:00 2019'),(1436,8,'MTFa','Conference room',2,0,'Tue Sep 17 13:34:04 GMT+03:00 2019'),(1437,8,'MTFa','Conference room',2,1,'Tue Sep 17 13:35:34 GMT+03:00 2019'),(1438,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:05:53 GMT+03:00 2019'),(1439,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:06:02 GMT+03:00 2019'),(1440,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:06:12 GMT+03:00 2019'),(1441,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:06:15 GMT+03:00 2019'),(1442,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:06:21 GMT+03:00 2019'),(1443,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:06:25 GMT+03:00 2019'),(1444,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:06:31 GMT+03:00 2019'),(1445,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:06:35 GMT+03:00 2019'),(1446,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:06:44 GMT+03:00 2019'),(1447,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:06:47 GMT+03:00 2019'),(1448,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:06:50 GMT+03:00 2019'),(1449,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:06:56 GMT+03:00 2019'),(1450,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:07:05 GMT+03:00 2019'),(1451,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:07:12 GMT+03:00 2019'),(1452,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:07:28 GMT+03:00 2019'),(1453,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:07:31 GMT+03:00 2019'),(1454,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:07:34 GMT+03:00 2019'),(1455,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:07:41 GMT+03:00 2019'),(1456,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:07:47 GMT+03:00 2019'),(1457,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:07:50 GMT+03:00 2019'),(1458,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:08:00 GMT+03:00 2019'),(1459,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:08:03 GMT+03:00 2019'),(1460,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:08:53 GMT+03:00 2019'),(1461,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:09:02 GMT+03:00 2019'),(1462,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:09:22 GMT+03:00 2019'),(1463,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:09:31 GMT+03:00 2019'),(1464,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:09:44 GMT+03:00 2019'),(1465,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:09:50 GMT+03:00 2019'),(1466,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:10:16 GMT+03:00 2019'),(1467,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:10:19 GMT+03:00 2019'),(1468,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:10:22 GMT+03:00 2019'),(1469,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:10:28 GMT+03:00 2019'),(1470,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:10:31 GMT+03:00 2019'),(1471,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:10:34 GMT+03:00 2019'),(1472,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:10:38 GMT+03:00 2019'),(1473,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:10:44 GMT+03:00 2019'),(1474,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:10:54 GMT+03:00 2019'),(1475,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:10:57 GMT+03:00 2019'),(1476,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:11:00 GMT+03:00 2019'),(1477,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:11:03 GMT+03:00 2019'),(1478,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:11:06 GMT+03:00 2019'),(1479,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:11:10 GMT+03:00 2019'),(1480,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:11:16 GMT+03:00 2019'),(1481,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:11:19 GMT+03:00 2019'),(1482,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:11:23 GMT+03:00 2019'),(1483,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:11:29 GMT+03:00 2019'),(1484,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:11:32 GMT+03:00 2019'),(1485,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:11:35 GMT+03:00 2019'),(1486,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:11:38 GMT+03:00 2019'),(1487,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:11:41 GMT+03:00 2019'),(1488,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:11:44 GMT+03:00 2019'),(1489,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:12:03 GMT+03:00 2019'),(1490,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:12:06 GMT+03:00 2019'),(1491,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:12:12 GMT+03:00 2019'),(1492,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:12:22 GMT+03:00 2019'),(1493,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:12:25 GMT+03:00 2019'),(1494,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:12:38 GMT+03:00 2019'),(1495,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:12:41 GMT+03:00 2019'),(1496,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:12:44 GMT+03:00 2019'),(1497,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:12:48 GMT+03:00 2019'),(1498,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:13:05 GMT+03:00 2019'),(1499,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:13:08 GMT+03:00 2019'),(1500,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:13:11 GMT+03:00 2019'),(1501,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:13:14 GMT+03:00 2019'),(1502,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:13:26 GMT+03:00 2019'),(1503,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:13:29 GMT+03:00 2019'),(1504,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:13:39 GMT+03:00 2019'),(1505,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:13:42 GMT+03:00 2019'),(1506,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:13:45 GMT+03:00 2019'),(1507,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:13:51 GMT+03:00 2019'),(1508,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:14:04 GMT+03:00 2019'),(1509,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:14:07 GMT+03:00 2019'),(1510,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:15:56 GMT+03:00 2019'),(1511,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:15:59 GMT+03:00 2019'),(1512,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:16:06 GMT+03:00 2019'),(1513,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:16:09 GMT+03:00 2019'),(1514,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:16:15 GMT+03:00 2019'),(1515,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:16:18 GMT+03:00 2019'),(1516,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:16:27 GMT+03:00 2019'),(1517,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:16:34 GMT+03:00 2019'),(1518,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:16:49 GMT+03:00 2019'),(1519,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:16:52 GMT+03:00 2019'),(1520,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:17:02 GMT+03:00 2019'),(1521,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:17:08 GMT+03:00 2019'),(1522,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:17:17 GMT+03:00 2019'),(1523,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:17:21 GMT+03:00 2019'),(1524,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:17:33 GMT+03:00 2019'),(1525,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:30:30 GMT+03:00 2019'),(1526,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:30:45 GMT+03:00 2019'),(1527,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:30:48 GMT+03:00 2019'),(1528,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:33:38 GMT+03:00 2019'),(1529,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:33:58 GMT+03:00 2019'),(1530,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:34:01 GMT+03:00 2019'),(1531,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:34:05 GMT+03:00 2019'),(1532,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:34:08 GMT+03:00 2019'),(1533,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:34:14 GMT+03:00 2019'),(1534,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:34:17 GMT+03:00 2019'),(1535,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:40:10 GMT+03:00 2019'),(1536,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:41:45 GMT+03:00 2019'),(1537,8,'MTFa','Conference room',2,0,'Tue Sep 17 14:41:51 GMT+03:00 2019'),(1538,8,'MTFa','Conference room',2,1,'Tue Sep 17 14:58:32 GMT+03:00 2019'),(1539,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:15:07 GMT+03:00 2019'),(1540,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:15:10 GMT+03:00 2019'),(1541,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:15:14 GMT+03:00 2019'),(1542,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:15:22 GMT+03:00 2019'),(1543,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:26:31 GMT+03:00 2019'),(1544,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:26:34 GMT+03:00 2019'),(1545,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:27:08 GMT+03:00 2019'),(1546,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:27:11 GMT+03:00 2019'),(1547,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:27:27 GMT+03:00 2019'),(1548,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:27:30 GMT+03:00 2019'),(1549,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:27:36 GMT+03:00 2019'),(1550,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:27:39 GMT+03:00 2019'),(1551,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:27:55 GMT+03:00 2019'),(1552,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:27:58 GMT+03:00 2019'),(1553,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:38:18 GMT+03:00 2019'),(1554,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:38:21 GMT+03:00 2019'),(1555,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:38:25 GMT+03:00 2019'),(1556,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:41:44 GMT+03:00 2019'),(1557,8,'MTFa','Conference room',2,0,'Tue Sep 17 15:41:47 GMT+03:00 2019'),(1558,8,'MTFa','Conference room',2,1,'Tue Sep 17 15:42:06 GMT+03:00 2019'),(1559,8,'MTFa','Conference room',2,1,'Tue Sep 17 16:07:54 GMT+03:00 2019'),(1560,8,'MTFa','Conference room',2,0,'Tue Sep 17 16:07:58 GMT+03:00 2019'),(1561,8,'MTFa','Conference room',2,1,'Tue Sep 17 16:08:02 GMT+03:00 2019'),(1562,8,'MTFa','Conference room',2,0,'Tue Sep 17 16:08:12 GMT+03:00 2019'),(1563,8,'MTFa','Conference room',2,1,'Tue Sep 17 16:11:49 GMT+03:00 2019'),(1564,8,'MTFa','Conference room',2,0,'Tue Sep 17 16:11:52 GMT+03:00 2019'),(1565,8,'MTFa','Conference room',2,1,'Tue Sep 17 16:15:30 GMT+03:00 2019'),(1566,8,'MTFa','Conference room',2,0,'Tue Sep 17 16:15:33 GMT+03:00 2019');
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
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userpreferences`
--

LOCK TABLES `userpreferences` WRITE;
/*!40000 ALTER TABLE `userpreferences` DISABLE KEYS */;
INSERT INTO `userpreferences` VALUES (43,8,'shoes'),(44,8,'cofee'),(45,8,'clothes');
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (8,'Bala','1234','Balanean','Cristian','22','C:\\Users\\tehnic\\Desktop\\UsersProfilePictures\\8.jpg'),(9,'spasat','1234','Pasat','Adrian','32','C:\\Users\\tehnic\\Desktop\\UsersProfilePictures\\9.jpg');
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

-- Dump completed on 2019-09-17 17:58:22
