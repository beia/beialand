-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: solomondb
-- ------------------------------------------------------
-- Server version	8.0.19

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
-- Table structure for table `beacons`
--

DROP TABLE IF EXISTS `beacons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `beacons` (
  `id` varchar(45) NOT NULL,
  `label` varchar(45) NOT NULL,
  `idMall` int NOT NULL,
  `idCompany` varchar(45) NOT NULL,
  `major` varchar(45) DEFAULT NULL,
  `minor` varchar(45) DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `layer` int NOT NULL,
  `floor` int NOT NULL,
  `manufacturer` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idMall3_idx` (`idMall`),
  KEY `fk_beacons_company_idx` (`idCompany`),
  CONSTRAINT `fk_beacons_company` FOREIGN KEY (`idCompany`) REFERENCES `companies` (`username`),
  CONSTRAINT `idMall3` FOREIGN KEY (`idMall`) REFERENCES `malls` (`idMalls`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beacons`
--

LOCK TABLES `beacons` WRITE;
/*!40000 ALTER TABLE `beacons` DISABLE KEYS */;
INSERT INTO `beacons` VALUES ('01d1b1ded3ea74f01f464d076f2f8138','EstimoteBeacon1',5,'0','0','0',0,0,0,0,'Estimote'),('2c49b20fa9a9a4c9ee5d64b884811b34','EstimoteBeacon5',5,'0','0','0',0,0,0,0,'Estimote'),('44cf736f23ac5add5e8d15b021600d03','EstimoteBeacon4',5,'0','0','0',0,0,0,0,'Estimote'),('580a946d72a0aea856aa9e0dd4beda37','EstimoteBeacon6',5,'0','0','0',0,0,0,0,'Estimote'),('716cb1ae9008d60e5e40499ec67a772f','EstimoteBeacon2',5,'0','0','0',0,0,0,0,'Estimote'),('ee4d538a36c2bbac622980b351ba9a0a','EstimoteBeacon3',5,'0','0','0',0,0,0,0,'Estimote'),('LKhV','Starbucks',1,'3','41302','22282',44.4364283,26.0868782,1,1,'Kontakt'),('R4JH','Zara',1,'2','24334','57021',44.4363632,26.0868611,1,1,'Kontakt'),('rrZd','McDonald\'s',1,'0','39824','22135',44.4364185,26.0869449,1,1,'Kontakt'),('zbCe','Altex',1,'1','60181','7706',44.4363572,26.0869305,1,1,'Kontakt');
/*!40000 ALTER TABLE `beacons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaigns`
--

DROP TABLE IF EXISTS `campaigns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campaigns` (
  `idCampaign` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `idCompany` varchar(45) NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` varchar(300) NOT NULL,
  `category` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `startDate` varchar(100) NOT NULL,
  `endDate` varchar(100) NOT NULL,
  `photoPath` varchar(1000) NOT NULL,
  PRIMARY KEY (`idCampaign`),
  KEY `FK_campain_idCompany_idx` (`idCompany`),
  KEY `fk_campaigns_categories_idx` (`category`),
  CONSTRAINT `fk_campaigns_categories` FOREIGN KEY (`category`) REFERENCES `categories` (`name`),
  CONSTRAINT `FK_campain_idCompany` FOREIGN KEY (`idCompany`) REFERENCES `companies` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaigns`
--

LOCK TABLES `campaigns` WRITE;
/*!40000 ALTER TABLE `campaigns` DISABLE KEYS */;
INSERT INTO `campaigns` VALUES ('00','0','McPuişor 3.8 lei','1 felie de carne de pui, învelită într-un strat crocant de pesmet auriu, prăjită timp de 2 minute în ulei vegetal (un amestec de ulei de rapiţă şi floarea soarelui), însoţită de sos McPuişor, castraveţi muraţi, chiflă.','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/mc_puisor.jpg'),('01','0','Crispy Chicken McWrap','McWrap cu bucăţi crocante din piept de pui picant. Savurează-l în orice moment al zilei!','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/mc_wrap.jpg'),('10','1','Monitor Lenovo Gaming pret 970 lei','cel mai jmeker monitor','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/testCampaign.jpg'),('11','1','Placa de baza GIGABYTE B450M DS3H pret 340 lei','cea mai jmekera placa de baza','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/b450m-ds3h-8589e788ddd6308b521f30a90ddf386e.jpg'),('20','2','Jacheta Casual 90 lei','Jachetă casual-sport, cu croi confortabil, guler ridicat și buzunare stil marsupiu. Model cu dosul moale şi flauşat.','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/jacheta_casual_neagra.jpg'),('30','2','Bluză Casual 65 lei ','Un item ultra tineresc cu o lungime până în talie şi o cromatică sport interesantă.','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/bluza_casual_fete.jpg'),('31','3','Caramel Macchiato 14 lei','Laptele proaspăt aburit cu sirop aromat de vanilie marcat cu espresso și completat cu caramel pentru un finisaj atât de dulce.','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/caramel_macchiato.jpg'),('32','3','Ciocolata Calda 12 lei','Un mix magic de cacao si lapte.','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/hot_chocolate.jpg'),('33','3','Ceai de fructe 14 lei','Un ceai minunat de fructe cu arome incredibile.','Books','2020/03/18 12:00:00','2020/04/30 12:00:00','CampaignsPictures/tea.jpg'),('testCampaign','pcgarage','Monitor Lenovo Gaming 884,99 RON','Monitor LED Lenovo Gaming Legion Y25f-10 24.5 inch 1ms FreeSync 144Hz','Books','2020/03/20 12:00:00','2020/04/30 12:00:00','CampaignsPictures/testCampaign.jpg');
/*!40000 ALTER TABLE `campaigns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campainsreactions`
--

DROP TABLE IF EXISTS `campainsreactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campainsreactions` (
  `idcampainsReactions` int NOT NULL AUTO_INCREMENT,
  `idCampain` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `idUser` int NOT NULL,
  `viewDate` varchar(45) NOT NULL,
  PRIMARY KEY (`idcampainsReactions`),
  KEY `fk_idCampaign_campaigns_reactions_idx` (`idCampain`),
  KEY `fk_campaignsReactions_Users_idx` (`idUser`),
  CONSTRAINT `fk_campaignsReactions_Users` FOREIGN KEY (`idUser`) REFERENCES `users` (`idusers`),
  CONSTRAINT `fk_idCampaign_campaigns_reactions` FOREIGN KEY (`idCampain`) REFERENCES `campaigns` (`idCampaign`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campainsreactions`
--

LOCK TABLES `campainsreactions` WRITE;
/*!40000 ALTER TABLE `campainsreactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `campainsreactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES ('Books'),('Cakes'),('Clothes'),('Coffee'),('Drinks'),('Electric scooters'),('Electronics'),('Flowers'),('Food'),('Games'),('Laptops'),('Monitors'),('Shoes'),('Smartphones'),('Sports'),('Tea');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `companies`
--

DROP TABLE IF EXISTS `companies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `companies` (
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `companies`
--

LOCK TABLES `companies` WRITE;
/*!40000 ALTER TABLE `companies` DISABLE KEYS */;
INSERT INTO `companies` VALUES ('0','0','McDonald\'s'),('1','1','Altex'),('2','2','Zara'),('3','3','Starbucks'),('mega','1234','Mega Image'),('New_User','1234','BEIA'),('New_User_2','1234','Random_Company'),('pcgarage','1234','Pc Garage'),('pcgarage2','1234','Pc Garage2');
/*!40000 ALTER TABLE `companies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `malls`
--

DROP TABLE IF EXISTS `malls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `malls` (
  `idMalls` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  PRIMARY KEY (`idMalls`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `malls`
--

LOCK TABLES `malls` WRITE;
/*!40000 ALTER TABLE `malls` DISABLE KEYS */;
INSERT INTO `malls` VALUES (1,'Conaque le\' Balà',44.436242,26.086765),(2,'Afi Palace Cotroceni',44.43068,26.051923),(3,'Baneasa Shopping City',44.508874,26.087669),(4,'Veranda Mall',44.452251,26.130569),(5,'Promenada Mall',44.478531,26.102645);
/*!40000 ALTER TABLE `malls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `idNotification` int NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  `message` varchar(45) NOT NULL,
  PRIMARY KEY (`idNotification`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (9,'mallAlert','incendiu'),(10,'normalNotification','pana la 50% reducere de Pasti');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parkingdata`
--

DROP TABLE IF EXISTS `parkingdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parkingdata` (
  `idparkingdata` int NOT NULL,
  `idParkingSpace` varchar(45) NOT NULL,
  `carParked` tinyint NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`idparkingdata`),
  KEY `fk_parkingdata_idParkingSpace_idx` (`idParkingSpace`),
  CONSTRAINT `fk_parkingdata_idParkingSpace` FOREIGN KEY (`idParkingSpace`) REFERENCES `parkingspace` (`idParkingSpace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parkingdata`
--

LOCK TABLES `parkingdata` WRITE;
/*!40000 ALTER TABLE `parkingdata` DISABLE KEYS */;
/*!40000 ALTER TABLE `parkingdata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parkingspace`
--

DROP TABLE IF EXISTS `parkingspace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parkingspace` (
  `idParkingSpace` varchar(45) NOT NULL,
  `idMall` int NOT NULL,
  `battery` int NOT NULL,
  PRIMARY KEY (`idParkingSpace`),
  KEY `fk_parkingspace_idMall_idx` (`idMall`),
  CONSTRAINT `fk_parkingspace_idMall` FOREIGN KEY (`idMall`) REFERENCES `malls` (`idMalls`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parkingspace`
--

LOCK TABLES `parkingspace` WRITE;
/*!40000 ALTER TABLE `parkingspace` DISABLE KEYS */;
/*!40000 ALTER TABLE `parkingspace` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userbeacontime`
--

DROP TABLE IF EXISTS `userbeacontime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userbeacontime` (
  `idBeaconTime` int NOT NULL AUTO_INCREMENT,
  `idUser` int NOT NULL,
  `idBeacon` varchar(45) NOT NULL,
  `timeSeconds` bigint NOT NULL,
  PRIMARY KEY (`idBeaconTime`),
  KEY `idUser_idx` (`idUser`),
  KEY `beaconLabel2_idx` (`idBeacon`),
  CONSTRAINT `idBeacon2` FOREIGN KEY (`idBeacon`) REFERENCES `beacons` (`id`) ON DELETE CASCADE,
  CONSTRAINT `idUser2` FOREIGN KEY (`idUser`) REFERENCES `users` (`idusers`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=505 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userbeacontime`
--

LOCK TABLES `userbeacontime` WRITE;
/*!40000 ALTER TABLE `userbeacontime` DISABLE KEYS */;
INSERT INTO `userbeacontime` VALUES (501,22,'R4JH',207),(502,22,'zbCe',58),(503,22,'rrZd',70),(504,22,'LKhV',29);
/*!40000 ALTER TABLE `userbeacontime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userpreferences`
--

DROP TABLE IF EXISTS `userpreferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userpreferences` (
  `iduserPreferences` int NOT NULL AUTO_INCREMENT,
  `idUser` int NOT NULL,
  `category` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`iduserPreferences`),
  KEY `idUser_idx` (`idUser`),
  KEY `fk_users_categories_idx` (`category`),
  CONSTRAINT `fk_users_categories` FOREIGN KEY (`category`) REFERENCES `categories` (`name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idUser` FOREIGN KEY (`idUser`) REFERENCES `users` (`idusers`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userpreferences`
--

LOCK TABLES `userpreferences` WRITE;
/*!40000 ALTER TABLE `userpreferences` DISABLE KEYS */;
/*!40000 ALTER TABLE `userpreferences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `idusers` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `lastName` varchar(45) NOT NULL,
  `firstName` varchar(45) NOT NULL,
  `age` varchar(45) NOT NULL,
  `picture` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idusers`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (15,'bala','1234','Balanean','Cristian','22','C:\\Users\\Tehnic\\Desktop\\SolomonProfilePictures\\15.jpg'),(16,'apasat','beia','Pasat','Adrian','32','C:\\Users\\Tehnic\\Desktop\\SolomonProfilePictures\\16.jpg'),(17,'IoanaP95','beiasolomon1','Petre','Ioana','24','C:\\Users\\Tehnic\\Desktop\\SolomonProfilePictures\\17.jpg'),(18,'Cris','beia','Istrate','Cristiana','23',NULL),(19,'deni06','deni1997','Deni','Botezatu','22','C:\\Users\\Tehnic\\Desktop\\SolomonProfilePictures\\19.jpg'),(20,'Stefans\n','totaltelecom','Stefanescu','Stefan','60',NULL),(21,'Stefant','totaltelecom1','Stefanescu','Stefan','60',NULL),(22,'cristian','�gB��\\v��U�g�6#ȳ��E��x��F�','Balanean','Cristian','23','ProfilePictures\\22.jpg'),(23,'random','�gB��\\v��U�g�6#ȳ��E��x��F�','User','Random','20',NULL);
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

-- Dump completed on 2020-04-21 20:09:09
