package Servidor;

import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

public class Servidor {
    static DatagramSocket udpSocket;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://34.77.114.162/?autoReconnect=true&useSSL=false";
    static final String USER = "PD";
    static final String PASS = "PDcancro";
    static final String IP_DS = "localhost";
    static final int PORT_DS = 5000;
    static final int PKT_SIZE = 4000;

    static Connection conn = null;
    static int id = -1;

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        udpSocket = new DatagramSocket();
        byte[] b = "Server".getBytes();
        DatagramPacket p = new DatagramPacket(b, b.length, InetAddress.getByName(IP_DS), PORT_DS);
        udpSocket.send(p);
        udpSocket.receive(p);
        id = Integer.parseInt(new String(p.getData(), 0, p.getLength()));

        System.out.println("DS disse que eu era o Servidor "+ id);
        if(id == -1)
            return;
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
