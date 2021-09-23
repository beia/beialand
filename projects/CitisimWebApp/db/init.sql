-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Sep 04, 2020 at 08:33 PM
-- Server version: 8.0.21-0ubuntu0.20.04.4
-- PHP Version: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `libcitisim`
--
CREATE DATABASE IF NOT EXISTS `libcitisim` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `libcitisim`;

-- --------------------------------------------------------

--
-- Table structure for table `BasicOutput`
--

DROP TABLE IF EXISTS `BasicOutput`;
CREATE TABLE `BasicOutput` (
  `OutputID` int NOT NULL,
  `ScenarioID` int DEFAULT NULL,
  `BillName` varchar(50) DEFAULT NULL,
  `BaselineBill` double DEFAULT NULL,
  `CurrentBill` double DEFAULT NULL,
  `SavingsMU` double DEFAULT NULL,
  `SavingsPercent` double DEFAULT NULL,
  `AmountReturned` double DEFAULT NULL,
  `AmountYetToBeReturned` double DEFAULT NULL,
  `ROI` double DEFAULT NULL,
  `IRR` double DEFAULT NULL,
  `NPV` double DEFAULT NULL,
  `ESCO` double DEFAULT NULL,
  `Client` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `EnergySensorsData`
--

DROP TABLE IF EXISTS `EnergySensorsData`;
CREATE TABLE `EnergySensorsData` (
  `ID` int NOT NULL,
  `SubscriptionID` int NOT NULL,
  `SensorID` text NOT NULL,
  `Value` float NOT NULL,
  `Timestamp` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `Rules`
--

DROP TABLE IF EXISTS `Rules`;
CREATE TABLE `Rules` (
  `RuleID` int NOT NULL,
  `ScenarioID` int DEFAULT NULL,
  `RuleName` varchar(100) DEFAULT NULL,
  `RuleMin` double DEFAULT NULL,
  `RuleMax` double DEFAULT NULL,
  `RuleEsco` double DEFAULT NULL,
  `RuleClient` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `Scenarios`
--

DROP TABLE IF EXISTS `Scenarios`;
CREATE TABLE `Scenarios` (
  `ID` int NOT NULL,
  `UserID` int DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Value` double DEFAULT NULL,
  `ValueType` varchar(10) DEFAULT NULL,
  `Duration` int DEFAULT NULL,
  `DurationType` varchar(10) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `StartDate` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `Subscriptions`
--

DROP TABLE IF EXISTS `Subscriptions`;
CREATE TABLE `Subscriptions` (
  `ID` int NOT NULL,
  `UserID` int NOT NULL,
  `SensorID` text NOT NULL,
  `DatasetName` varchar(100) NOT NULL,
  `DatasetDescription` text NOT NULL,
  `Validity` int NOT NULL DEFAULT '1',
  `StartDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `EndDate` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
CREATE TABLE `Users` (
  `userID` int NOT NULL,
  `userName` varchar(50) DEFAULT NULL,
  `userEmail` varchar(50) DEFAULT NULL,
  `userPass` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Indexes for dumped tables
--

--
-- Indexes for table `BasicOutput`
--
ALTER TABLE `BasicOutput`
  ADD PRIMARY KEY (`OutputID`);

--
-- Indexes for table `EnergySensorsData`
--
ALTER TABLE `EnergySensorsData`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `Rules`
--
ALTER TABLE `Rules`
  ADD PRIMARY KEY (`RuleID`);

--
-- Indexes for table `Scenarios`
--
ALTER TABLE `Scenarios`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `Subscriptions`
--
ALTER TABLE `Subscriptions`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`userID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `BasicOutput`
--
ALTER TABLE `BasicOutput`
  MODIFY `OutputID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=483;

--
-- AUTO_INCREMENT for table `EnergySensorsData`
--
ALTER TABLE `EnergySensorsData`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43417;

--
-- AUTO_INCREMENT for table `Rules`
--
ALTER TABLE `Rules`
  MODIFY `RuleID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=93;

--
-- AUTO_INCREMENT for table `Scenarios`
--
ALTER TABLE `Scenarios`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=90;

--
-- AUTO_INCREMENT for table `Subscriptions`
--
ALTER TABLE `Subscriptions`
  MODIFY `ID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `userID` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
