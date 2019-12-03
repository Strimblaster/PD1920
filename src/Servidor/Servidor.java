package Servidor;

import java.net.*;
import java.sql.*;
import java.util.ArrayList;

public class Servidor {
    DatagramSocket udpSocket;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://34.77.114.162/?autoReconnect=true&useSSL=false";
    static final String USER = "PD";
    static final String PASS = "PDcancro";
    static Connection conn = null;
    static int id = 0;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        ResultSet resultSet = conn.getMetaData().getCatalogs();
        while (resultSet.next()) {
            if(resultSet.getString(1).equals("Servidor"+id)){
                Statement s = conn.createStatement();
                s.execute("DROP DATABASE Servidor"+id );
            }
        }
        resultSet.close();
        Statement s = conn.createStatement();
        s.execute("CREATE DATABASE Servidor"+id);

    }
}
