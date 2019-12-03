package Servidor;

import Comum.Constants;

import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

public class Servidor implements ServerConstants, Constants {
    private static Servidor servidor;

    private DatagramSocket dsSocket;
    private Connection conn;
    private String DBName;
    private int id;

    public Servidor() throws SQLException, SocketException {
        this.dsSocket = new DatagramSocket();
        this.conn = DriverManager.getConnection(DB_URL, USER, PASS);
        this.id = -1;
        DBName = null;
        dsSocket.setSoTimeout(TIMEOUT);
    }

    public static void main(String[] args){
        try {
            System.out.println("[INFO] Servidor a arrancar...");
            servidor = new Servidor();

            System.out.println("[INFO] A comunicar com o DS...");
            int id = servidor.requestServerID();
            System.out.println("[INFO] Atribuido o ID: "+ id);

            System.out.println("[INFO] A criar a base de dados...");
            servidor.createDatabase();


        } catch (SocketException e) {
            System.out.println("[ERRO] Houve um problema com o Socket:\n"+ e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO] Houve com a base de dados:\n"+ e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERRO] Houve um erro de IO:\n"+ e.getMessage());
        }
    }


    private void createDatabase() throws SQLException {
        ResultSet resultSet = conn.getMetaData().getCatalogs();

        //Verifica se a DB já existe e apaga
        while (resultSet.next()) {
            if (resultSet.getString(1).equals(DBName)) {
                Statement s = conn.createStatement();
                s.execute("DROP DATABASE " + DBName);
                s.close();
            }
        }

        //Cria DB
        resultSet.close();
        Statement s = conn.createStatement();
        s.execute("CREATE DATABASE Servidor" + id);
        s.close();
    }

    private int requestServerID() throws IOException {
        byte[] b = "Server".getBytes();

        DatagramPacket p = new DatagramPacket(b, b.length, InetAddress.getByName(IP_DS), PORT_DS);
        dsSocket.send(p);
        dsSocket.receive(p);

        id = Integer.parseInt(new String(p.getData(), 0, p.getLength()));
        if(id < 0) throw new IOException("O ID do DS recebido não é válido");
        DBName = "Servidor"+id;
        return id;
    }
}
