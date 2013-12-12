-- phpMyAdmin SQL Dump
-- version 2.10.2
-- http://www.phpmyadmin.net
-- 
-- Host: localhost
-- Generation Time: Apr 28, 2012 at 01:14 PM
-- Server version: 5.0.45
-- PHP Version: 5.2.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

-- 
-- Database: `db_rawatinap`
-- 

-- --------------------------------------------------------

-- 
-- Table structure for table `anggota`
-- 

CREATE TABLE `anggota` (
  `NoTelp` varchar(20) NOT NULL,
  `NOKTP` varchar(20) NOT NULL,
  `Nama` varchar(160) NOT NULL,
  `Alamat` varchar(20) NOT NULL,
  `Status` int(20) NOT NULL,
  PRIMARY KEY  (`NoTelp`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Dumping data for table `anggota`
-- 

INSERT INTO `anggota` VALUES ('6285640275438', 'KTP001', 'Bagus', 'Jakarta', 1);

-- --------------------------------------------------------

-- 
-- Table structure for table `biaya`
-- 

CREATE TABLE `biaya` (
  `KdBiaya` varchar(20) NOT NULL,
  `NoDaftar` varchar(20) NOT NULL,
  `Jmlh_Harga` varchar(20) NOT NULL,
  PRIMARY KEY  (`KdBiaya`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Dumping data for table `biaya`
-- 

INSERT INTO `biaya` VALUES ('KD001', 'DF001', '200000');

-- --------------------------------------------------------

-- 
-- Table structure for table `daftar`
-- 

CREATE TABLE `daftar` (
  `NoDaftar` varchar(20) NOT NULL,
  `NoPasien` varchar(20) NOT NULL,
  `Tgl_Daftar` date NOT NULL,
  `Tgl_Masuk` date NOT NULL,
  `KdDokter` varchar(20) NOT NULL,
  `KdRuang` varchar(20) NOT NULL,
  PRIMARY KEY  (`NoDaftar`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Dumping data for table `daftar`
-- 

INSERT INTO `daftar` VALUES ('DF001', 'PS001', '2012-04-11', '2012-04-11', 'DK001', 'RG001');

-- --------------------------------------------------------

-- 
-- Table structure for table `dokter`
-- 

CREATE TABLE `dokter` (
  `KdDokter` varchar(20) NOT NULL,
  `NmDokter` varchar(20) NOT NULL,
  `Jns_Kel` varchar(20) NOT NULL,
  `Alamat` varchar(20) NOT NULL,
  PRIMARY KEY  (`KdDokter`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Dumping data for table `dokter`
-- 

INSERT INTO `dokter` VALUES ('DK001', 'Bagusa', 'Pria', 'Jakarta');

-- --------------------------------------------------------

-- 
-- Table structure for table `keluar`
-- 

CREATE TABLE `keluar` (
  `NoKeluar` varchar(20) NOT NULL,
  `NoDaftar` varchar(20) NOT NULL,
  `Tgl_Keluar` date NOT NULL,
  `LamaInap` varchar(20) NOT NULL,
  `Diagnosa` varchar(20) NOT NULL,
  `Tindakan` varchar(20) NOT NULL,
  `Status` varchar(20) NOT NULL,
  PRIMARY KEY  (`NoKeluar`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Dumping data for table `keluar`
-- 

INSERT INTO `keluar` VALUES ('KL001', 'DF001', '2012-04-11', '2', 'Kangen', 'Ketemu', 'Hidup');

-- --------------------------------------------------------

-- 
-- Table structure for table `kwitansi`
-- 

CREATE TABLE `kwitansi` (
  `KdKwitansi` varchar(20) NOT NULL,
  `Tgl_Bayar` date NOT NULL,
  `NoKeluar` varchar(20) NOT NULL,
  `TotalBayar` varchar(20) NOT NULL,
  PRIMARY KEY  (`KdKwitansi`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Dumping data for table `kwitansi`
-- 

INSERT INTO `kwitansi` VALUES ('', '0000-00-00', '', '');
INSERT INTO `kwitansi` VALUES ('KW001', '2012-04-11', 'KL001', '20000');

-- --------------------------------------------------------

-- 
-- Table structure for table `pasien`
-- 

CREATE TABLE `pasien` (
  `NoPasien` varchar(20) NOT NULL,
  `NmPasien` varchar(20) NOT NULL,
  `Tmpt_Lahir` varchar(20) NOT NULL,
  `Tgl_Lahir` date NOT NULL,
  `Umur` varchar(20) NOT NULL,
  `Jns_Kel` varchar(20) NOT NULL,
  `Pekerjaan` varchar(20) NOT NULL,
  `Alamat` varchar(20) NOT NULL,
  PRIMARY KEY  (`NoPasien`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Dumping data for table `pasien`
-- 

INSERT INTO `pasien` VALUES ('PS001', 'Bagus', 'Solo', '2012-04-11', '18', 'Pria', 'Mahasiswa', 'Jakarta');

-- --------------------------------------------------------

-- 
-- Table structure for table `ruang`
-- 

CREATE TABLE `ruang` (
  `KdRuang` varchar(20) NOT NULL,
  `NmRuang` varchar(20) NOT NULL,
  PRIMARY KEY  (`KdRuang`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Dumping data for table `ruang`
-- 

INSERT INTO `ruang` VALUES ('rg001', 'Melati');
