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
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userbeacontime`
--

LOCK TABLES `userbeacontime` WRITE;
/*!40000 ALTER TABLE `userbeacontime` DISABLE KEYS */;
INSERT INTO `userbeacontime` VALUES (221,5,'MTFa','Conference room',2,47272),(222,5,'tZF7','Scara 1',2,134),(223,5,'aaai','Oficiu 1',2,221206),(224,5,'R4JH','Proiecte',2,-31946),(225,5,'g0MK','Management room',2,-177),(226,5,'ee4d538a36c2bbac622980b351ba9a0a','Room3',1,0),(227,5,'LKhV','POC',2,706),(228,5,'580a946d72a0aea856aa9e0dd4beda37','Room6',1,0),(229,5,'zbCe','Secretariat',2,106),(230,5,'44cf736f23ac5add5e8d15b021600d03','Room4',1,0),(231,5,'01d1b1ded3ea74f01f464d076f2f8138','Room1',1,0),(232,5,'0Qq2','Sales',2,1309),(233,5,'y0PY','Garaj',2,0),(234,5,'ygmN','Intrare',2,-237),(235,5,'2c49b20fa9a9a4c9ee5d64b884811b34','Room5',1,0),(236,5,'v7mz','Scara2',2,127125),(237,5,'lZyt','CEO room',2,0),(238,5,'PcNy','POC 2',2,0),(239,5,'716cb1ae9008d60e5e40499ec67a772f','Room2',1,0),(240,5,'rrZd','SERE',2,-70);
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

-- Dump completed on 2019-09-05 12:06:46
