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
-- Table structure for table `userbeacontime`
--

DROP TABLE IF EXISTS `userbeacontime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-17 16:16:49
