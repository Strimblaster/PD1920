package Servidor.Interfaces;


public interface ServerConstants {
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://34.77.114.162/?autoReconnect=true&useSSL=false&allowMultiQueries=true";
    String USER = "PD";
    String PASS = "PDcancro";

    String SERVER_STORAGE_PATH = "C://temp/";




    String DB_CREATE_QUERY1 = "/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;" +
            "/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;" +
            "/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;" +
            " SET NAMES utf8mb4 ;" +
            "/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;" +
            "/*!40103 SET TIME_ZONE='+00:00' */;" +
            "/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;" +
            "/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;" +
            "/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;" +
            "/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;" +
            "" +
            "CREATE DATABASE /*!32312 IF NOT EXISTS*/ `";

    String DB_CREATE_QUERY2 = "` /*!40100 DEFAULT CHARACTER SET utf8 */;" +
            "" +
            "USE `";

    String DB_CREATE_QUERY3 = "`;" +
            "" +
            "DROP TABLE IF EXISTS `lt_playlists_musicas`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            " SET character_set_client = utf8mb4 ;" +
            "CREATE TABLE `lt_playlists_musicas` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `idMusicas` bigint(18) DEFAULT NULL," +
            "  `idPlaylists` bigint(18) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)," +
            "  KEY `fk_musicas` (`idMusicas`)," +
            "  KEY `fk_playlists` (`idPlaylists`)," +
            "  CONSTRAINT `fk_musicas` FOREIGN KEY (`idMusicas`) REFERENCES `musicas` (`id`)," +
            "  CONSTRAINT `fk_playlists` FOREIGN KEY (`idPlaylists`) REFERENCES `playlists` (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "" +
            "" +
            "DROP TABLE IF EXISTS `musicas`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            " SET character_set_client = utf8mb4 ;" +
            "CREATE TABLE `musicas` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `autor` varchar(256) DEFAULT NULL," +
            "  `album` varchar(256) DEFAULT NULL," +
            "  `duracao` int(11) DEFAULT NULL," +
            "  `ano` year(4) DEFAULT NULL," +
            "  `genero` varchar(256) DEFAULT NULL," +
            "  `ficheiro` varchar(256) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "" +
            "DROP TABLE IF EXISTS `playlists`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            " SET character_set_client = utf8mb4 ;" +
            "CREATE TABLE `playlists` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `criador` bigint(18) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)," +
            "  KEY `criador` (`criador`)," +
            "  CONSTRAINT `fk_utilizadores_playlists` FOREIGN KEY (`criador`) REFERENCES `utilizadores` (`id`) ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "" +
            "DROP TABLE IF EXISTS `utilizadores`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            " SET character_set_client = utf8mb4 ;" +
            "CREATE TABLE `utilizadores` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `password` varchar(256) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "" +
            "" +
            "CREATE DATABASE /*!32312 IF NOT EXISTS*/ `";

    String DB_CREATE_QUERY4 = "` /*!40100 DEFAULT CHARACTER SET utf8 */;" +
            "" +
            "USE `";

    String DB_CREATE_QUERY5 = "`;" +
            "" +
            "DROP TABLE IF EXISTS `lt_playlists_musicas`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            " SET character_set_client = utf8mb4 ;" +
            "CREATE TABLE `lt_playlists_musicas` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `idMusicas` bigint(18) DEFAULT NULL," +
            "  `idPlaylists` bigint(18) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)," +
            "  KEY `fk_musicas` (`idMusicas`)," +
            "  KEY `fk_playlists` (`idPlaylists`)," +
            "  CONSTRAINT `fk_musicas` FOREIGN KEY (`idMusicas`) REFERENCES `musicas` (`id`)," +
            "  CONSTRAINT `fk_playlists` FOREIGN KEY (`idPlaylists`) REFERENCES `playlists` (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "" +
            "DROP TABLE IF EXISTS `musicas`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            " SET character_set_client = utf8mb4 ;" +
            "CREATE TABLE `musicas` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `autor` varchar(256) DEFAULT NULL," +
            "  `album` varchar(256) DEFAULT NULL," +
            "  `duracao` int(11) DEFAULT NULL," +
            "  `ano` year(4) DEFAULT NULL," +
            "  `genero` varchar(256) DEFAULT NULL," +
            "  `ficheiro` varchar(256) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "" +
            "DROP TABLE IF EXISTS `playlists`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            " SET character_set_client = utf8mb4 ;" +
            "CREATE TABLE `playlists` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `criador` bigint(18) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)," +
            "  KEY `criador` (`criador`)," +
            "  CONSTRAINT `fk_utilizadores_playlists` FOREIGN KEY (`criador`) REFERENCES `utilizadores` (`id`) ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "" +
            "" +
            "DROP TABLE IF EXISTS `utilizadores`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            " SET character_set_client = utf8mb4 ;" +
            "CREATE TABLE `utilizadores` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `password` varchar(256) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "LOCK TABLES `utilizadores` WRITE;" +
            "/*!40000 ALTER TABLE `utilizadores` DISABLE KEYS */;" +
            "/*!40000 ALTER TABLE `utilizadores` ENABLE KEYS */;" +
            "UNLOCK TABLES;" +
            "/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;" +
            "" +
            "/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;" +
            "/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;" +
            "/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;" +
            "/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;" +
            "/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;" +
            "/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;" +
            "/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;";


}
