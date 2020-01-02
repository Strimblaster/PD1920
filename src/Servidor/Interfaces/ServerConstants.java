package Servidor.Interfaces;


import Comum.Constants;

public interface ServerConstants extends Constants {
    String DB_URL = "jdbc:mysql://34.77.114.162/?autoReconnect=true&useSSL=false&allowMultiQueries=true";
    String USER = "PD";
    String PASS = "PDcancro";

    String MULTICAST_ADDR = "239.111.111.111";
    String MULTICAST_ADDR_CONFIRMATION = "239.222.222.222";
    int MULTICAST_PORT =  3033;
    int MULTICAST_PORT_CONFIRMATION =  3034;


    String DB_CREATE_1 =
            "CREATE DATABASE /*!32312 IF NOT EXISTS*/ `";

    String DB_CREATE_2 = "` /*!40100 DEFAULT CHARACTER SET utf8 */;" +
            "USE `";


    String DB_CREATE_3 = "`;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            "/*!50503 SET character_set_client = utf8mb4 */;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            "/*!50503 SET character_set_client = utf8mb4 */;" +
            "" +
            "CREATE TABLE `utilizadores` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `email` varchar(256) DEFAULT NULL," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `password` varchar(256) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "" +
            "CREATE TABLE `musicas` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `autor` bigint(18) DEFAULT NULL," +
            "  `album` varchar(256) DEFAULT NULL," +
            "  `duracao` int(11) DEFAULT NULL," +
            "  `ano` int DEFAULT NULL," +
            "  `genero` varchar(256) DEFAULT NULL," +
            "  `ficheiro` varchar(256) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)," +
            "  KEY `fk_utilizadores_musicas_idx` (`autor`)," +
            "  CONSTRAINT `fk_utilizadores_musicas` FOREIGN KEY (`autor`) REFERENCES `utilizadores` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            "/*!50503 SET character_set_client = utf8mb4 */;" +
            "CREATE TABLE `playlists` (" +
            "  `id` bigint(18) NOT NULL AUTO_INCREMENT," +
            "  `nome` varchar(256) DEFAULT NULL," +
            "  `criador` bigint(18) DEFAULT NULL," +
            "  PRIMARY KEY (`id`)," +
            "  KEY `criador` (`criador`)," +
            "  CONSTRAINT `fk_utilizadores_playlists` FOREIGN KEY (`criador`) REFERENCES `utilizadores` (`id`) ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;" +
            "/*!40101 SET character_set_client = @saved_cs_client */;" +
            "/*!40101 SET @saved_cs_client     = @@character_set_client */;" +
            "/*!50503 SET character_set_client = utf8mb4 */;" +
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
            "/*!40101 SET character_set_client = @saved_cs_client */;";





}
