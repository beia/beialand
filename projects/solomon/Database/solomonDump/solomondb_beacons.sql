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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-05 12:06:46
