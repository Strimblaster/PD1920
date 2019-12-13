package Servidor;

import Comum.Constants;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.Resposta;
import Comum.Utilizador;
import Servidor.Interfaces.IServer;
import Servidor.Interfaces.IEvent;
import Servidor.Interfaces.ServerConstants;

import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.Scanner;

public class Servidor implements ServerConstants, Constants, IServer {

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
    public Resposta login(String username, String password) {
        PedidoLogin pedido = new PedidoLogin(new Utilizador(username, password));
        try{
            Utilizador utilizador = pedido.getUtilizador();
            PreparedStatement s = conn.prepareStatement("SELECT nome,password FROM utilizadores WHERE nome=?");
            s.setString(1, utilizador.getName());
            ResultSet resultSet = s.executeQuery();
            if(!resultSet.next())
                return new Resposta(pedido, false, "Username não foi encontrado");
            if(!resultSet.getString("password").equals(password))
                return new Resposta(pedido, false, "Password Incorreta");

            return new Resposta(pedido, true, "Login concluido");

        } catch (SQLException e) {
            return new Resposta(pedido, false, "Erro no servidor", e);
        }
    }

    @Override
    public Resposta signUp(String username, String password) {
        PedidoSignUp pedidoSignUp = new PedidoSignUp(new Utilizador(username, password));
        try{
            Utilizador utilizador = pedidoSignUp.getUtilizador();
            PreparedStatement s = conn.prepareStatement("SELECT nome FROM utilizadores WHERE nome=?");
            s.setString(1,utilizador.getName());
            ResultSet resultSetUsername = s.executeQuery();
            if(resultSetUsername.next()){
                return new Resposta(pedidoSignUp, false, "Username já está registado");
            }
                int resultSet = s.executeUpdate("INSERT INTO utilizadores(nome, password) VALUES (\'"+utilizador.getName()+"\', \'"+utilizador.getPassword()+"\')");

            return new Resposta(pedidoSignUp, true, "Registo concluido");

        } catch (SQLException e) {
            return new Resposta(pedidoSignUp, false, "Erro no servidor", e);
        }
    }
}
