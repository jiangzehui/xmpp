/*
SQLyog Ultimate v11.11 (32 bit)
MySQL - 5.0.27-community-nt : Database - news
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`news` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `news`;

/*Table structure for table `luntan_pinglun` */

DROP TABLE IF EXISTS `luntan_pinglun`;
-- 动态评论
CREATE TABLE `luntan_pinglun` (
  `lid` INT(11) NOT NULL AUTO_INCREMENT,
  `plid` INT(11) DEFAULT NULL,
  `USER` VARCHAR(50) DEFAULT NULL,
  `plocation` VARCHAR(50) DEFAULT NULL,
  `ptime` VARCHAR(50) DEFAULT NULL,
  `pcontent` TEXT,
  `pzan` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY  (`lid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `luntan_pinglun` */


/*Table structure for table `luntan_zan` */

DROP TABLE IF EXISTS `luntan_zan`;
-- 动态赞数
CREATE TABLE `luntan_zan` (
  `lid` INT(11) NOT NULL AUTO_INCREMENT,
  `plid` INT(11) DEFAULT NULL,
  `USER` VARCHAR(500) DEFAULT NULL,
  `iszan` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY  (`lid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `luntan_zan` */


/*Table structure for table `news_content` */

DROP TABLE IF EXISTS `news_content`;
-- 新闻内容
CREATE TABLE `news_content` (
  `cid` INT(11) NOT NULL AUTO_INCREMENT,
  `ctype` VARCHAR(50) DEFAULT NULL,
  `ctitle` VARCHAR(100) DEFAULT NULL,
  `czhaiyao` TEXT,
  `ccontent` LONGTEXT,
  `cimage` TEXT,
  `cauthor` VARCHAR(100) DEFAULT NULL,
  `ctime` VARCHAR(100) DEFAULT NULL,
  `cpinglun` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY  (`cid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `news_content` */


/*Table structure for table `news_luntan` */

DROP TABLE IF EXISTS `news_luntan`;
-- 动态
CREATE TABLE `news_luntan` (
  `lid` INT(11) NOT NULL AUTO_INCREMENT,
  `USER` VARCHAR(50) DEFAULT NULL,
  `TIME` VARCHAR(50) DEFAULT NULL,
  `content` TEXT,
  `image` VARCHAR(500) DEFAULT NULL,
  `location` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY  (`lid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `news_luntan` */


/*Table structure for table `news_pinglun` */
-- 新闻评论
DROP TABLE IF EXISTS `news_pinglun`;

CREATE TABLE `news_pinglun` (
  `pid` INT(11) NOT NULL AUTO_INCREMENT,
  `pcid` INT(11) DEFAULT NULL,
  `USER` VARCHAR(2000) DEFAULT NULL,
  `plocation` VARCHAR(50) DEFAULT NULL,
  `ptime` VARCHAR(50) DEFAULT NULL,
  `pcontent` TEXT,
  `pzan` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY  (`pid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `news_pinglun` */


/*Table structure for table `news_type` */
-- 新闻类型
DROP TABLE IF EXISTS `news_type`;

CREATE TABLE `news_type` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `type_name` VARCHAR(50) DEFAULT NULL,
  `type_url` VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `news_type` */


/*Table structure for table `news_zan` */
-- 新闻点赞
DROP TABLE IF EXISTS `news_zan`;

CREATE TABLE `news_zan` (
  `zid` INT(11) NOT NULL AUTO_INCREMENT,
  `pid` INT(11) DEFAULT NULL,
  `USER` VARCHAR(500) DEFAULT NULL,
  `iszan` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY  (`zid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `news_zan` */


/*Table structure for table `users` */
-- 用户表
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `uid` INT(11) NOT NULL AUTO_INCREMENT,
  `USER` VARCHAR(150) DEFAULT NULL,
  `PASSWORD` VARCHAR(60) DEFAULT NULL,
  `qq` VARCHAR(60) DEFAULT NULL,
  `icon` VARCHAR(600) DEFAULT NULL,
  `nickname` VARCHAR(600) DEFAULT NULL,
  `city` VARCHAR(600) DEFAULT NULL,
  `location` VARCHAR(600) DEFAULT NULL,
  `sex` VARCHAR(30) DEFAULT NULL,
  `years` VARCHAR(300) DEFAULT NULL,
  `qianming` VARCHAR(300) DEFAULT NULL,
  PRIMARY KEY  (`uid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*Data for the table `users` */


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
