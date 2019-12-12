package Servidor;

import Comum.Constants;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.Resposta;
import Comum.Utilizador;
import Servidor.Interfaces.IComunicacaoServer;
import Servidor.Interfaces.IEvent;
import Servidor.Interfaces.ServerConstants;

import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.Scanner;

public class Servidor implements ServerConstants, Constants, IComunicacaoServer {

    private Connection conn;
    private String DBName;
    private int id;
    private IEvent listener;


    private Servidor() throws SQLException, IOException {
        super();
        this.conn = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void setListener(IEvent listener) {
        this.listener = listener;
    }

    public static void main(String[] args){
        try {
            System.out.println("[INFO] Servidor a arrancar...");
            Servidor servidor = new Servidor();
            new Comunicacao(servidor);

            servidor.requestID();

            System.out.println("[INFO] Atribuido o ID: "+ servidor.id);

            System.out.println("[INFO] A criar a base de dados...");
            servidor.createDatabase();

            System.out.println("[INFO] Servidor pronto");
            servidor.ready();


            Scanner sc = new Scanner(System.in);

            while(!sc.next().equals("sair"));

        } catch (SocketException e) {
            System.out.println("[ERRO] Houve um problema com o Socket:\n"+ e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO] Houve com a base de dados:\n"+ e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERRO] Houve um erro de IO:\n"+ e.getMessage());
        }
    }

    private void ready() {
        listener.serverReady();
    }

    private void requestID() throws IOException {
        listener.needID();
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
        resultSet.close();

        //Cria DB
        Statement s = conn.createStatement();
        s.execute(DB_CREATE_QUERY1+ DBName +DB_CREATE_QUERY2+ DBName +DB_CREATE_QUERY3+ DBName +DB_CREATE_QUERY4+ DBName +DB_CREATE_QUERY5);
        s.close();

    }


    @Override
    public void setID(int id) {
        this.id = id;
        DBName = "Servidor"+id;
    }

    @Override
    public Resposta login(PedidoLogin pedido) {
        try{
            Utilizador utilizador = pedido.getUtilizador();
            Statement s = conn.createStatement();
            ResultSet resultSetUsername = s.executeQuery("SELECT nome FROM utilizadores WHERE nome= \' " + utilizador.getName() + '\'');
            if(!resultSetUsername.next())
                return new Resposta(pedido, false, "Username não foi encontrado");

            ResultSet resultSetPassword = s.executeQuery("SELECT password FROM utilizadores WHERE nome= \'" + utilizador.getName() + '\'');
            resultSetPassword.next();
            if(!resultSetPassword.getString(1).equals(utilizador.getPassword()))
                return new Resposta(pedido, false, "Password Incorreta");

            return new Resposta(pedido, true, "Login concluido");

        } catch (SQLException e) {
            return new Resposta(pedido, false, "Erro no servidor", e);
        }
    }
}
