package Servidor;

import Comum.Constants;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.PedidoUploadFile;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Song;
import Comum.Utilizador;
import Servidor.Interfaces.IServer;
import Servidor.Interfaces.IEvent;
import Servidor.Interfaces.ServerConstants;
import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Servidor implements ServerConstants, Constants, IServer {

    private Connection conn;
    private String DBName;
    private int id;
    private IEvent listener;
    private File musicDir;


    private Servidor() throws SQLException {
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
        s.execute(DB_CREATE_1 + DBName + DB_CREATE_2 + DBName + DB_CREATE_3);
        s.close();

    }

    @Override
    public Resposta login(String username, String password) throws InvalidPasswordException, InvalidUsernameException {
        PedidoLogin pedido = new PedidoLogin(new Utilizador(username, password));
        try{
            Utilizador utilizador = pedido.getUtilizador();
            PreparedStatement s = conn.prepareStatement("SELECT nome,password FROM utilizadores WHERE nome=?");
            s.setString(1, utilizador.getName());
            ResultSet resultSet = s.executeQuery();
            if(!resultSet.next())
                throw new InvalidUsernameException("Username não foi encontrado");
            if(!resultSet.getString("password").equals(password))
                throw new InvalidPasswordException("Password Incorreta");

            return new Resposta(pedido, true, "Login concluido");

        } catch (SQLException e) {
            return new Resposta(pedido, false, "Erro no servidor", TipoExcecao.Exception, e);
        }
    }

    @Override
    public Resposta signUp(String username, String password) throws InvalidUsernameException {
        PedidoSignUp pedidoSignUp = new PedidoSignUp(new Utilizador(username, password));
        try{
            Utilizador utilizador = pedidoSignUp.getUtilizador();
            PreparedStatement s = conn.prepareStatement("SELECT nome FROM utilizadores WHERE nome=?");
            s.setString(1,utilizador.getName());
            ResultSet resultSetUsername = s.executeQuery();
            if(resultSetUsername.next()){
                throw new InvalidUsernameException("Username já está registado");
            }
                s.executeUpdate("INSERT INTO utilizadores(nome, password) VALUES (\'"+utilizador.getName()+"\', \'"+utilizador.getPassword()+"\')");

            return new Resposta(pedidoSignUp, true, "Registo concluido");

        } catch (SQLException e) {
            return new Resposta(pedidoSignUp, false, "Erro no servidor", TipoExcecao.Exception, e);
        }
    }


    @Override
    public void addNewSong(Song musica, byte[] file) {

        try {

            //Vai buscar o ID da musica para saber o nome do ficheiro
            PreparedStatement getIDStatment = conn.prepareStatement("SELECT id FROM musicas WHERE nome = ?");
            getIDStatment.setString(1,musica.getNome());
            ResultSet resultSet = getIDStatment.executeQuery();
            if(!resultSet.next())
                throw new SQLException("Algo correu mal, o nome da musica não existe");
            int id = resultSet.getInt("id");
            getIDStatment.close();

            //Guarda o ficheiro na pasta do servidor
            FileOutputStream fileOutputStream = new FileOutputStream(musicDir.getAbsolutePath() + File.separator + id + ".mp3");
            fileOutputStream.write(file);
            fileOutputStream.flush();
            fileOutputStream.close();

            //Altera a Row da musica na tabela para ter o nome do ficheiro
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE musicas SET ficheiro = ? WHERE id = ?");
            preparedStatement.setString(1, id+".mp3");
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            //Falta mandar o evento para a comunicação aqui para atualizar todos os servidores

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Resposta uploadFile(Utilizador utilizador, Song musica) throws InvalidSongDescriptionException {
        if(musica.getAlbum() == null) throw new InvalidSongDescriptionException("Nome do Album Invalido");
        if(musica.getAlbum().equals("")) throw new InvalidSongDescriptionException("Nome do Album Invalido");

        if(musica.getGenero() == null) throw new InvalidSongDescriptionException("Genero Invalido");
        if(musica.getGenero().equals("")) throw new InvalidSongDescriptionException("Genero Invalido");

        if(musica.getNome() == null) throw new InvalidSongDescriptionException("Nome de Musica Invalido");
        if(musica.getNome().equals("")) throw new InvalidSongDescriptionException("Nome de Musica Invalido");

        PedidoUploadFile pedido = new PedidoUploadFile(utilizador,musica);

        try {

            //Verificar se o nome da musica já existe
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM musicas WHERE nome = ?");
            preparedStatement.setString(1, musica.getNome());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                throw new InvalidSongDescriptionException("Nome da Musica já existe!");
            preparedStatement.close();

            //Buscar o ID do utilizador que está a enviar a musica
            int id = getIDUtilizador(utilizador);

            //Adicionar a musica à BD
            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO musicas(nome, autor, album, duracao, ano, genero) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1,  musica.getNome());
            insertStatement.setInt(2,  id);
            insertStatement.setString(3,  musica.getAlbum());
            insertStatement.setInt(4,  musica.getDuracao());
            insertStatement.setInt(5,  musica.getAno());
            insertStatement.setString(6,  musica.getGenero());
            insertStatement.executeUpdate();
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            generatedKeys.next();
            int id_musica = generatedKeys.getInt(1);
            insertStatement.close();

            pedido.getMusica().setFilename(id_musica + ".mp3");

            return new Resposta(pedido, true, "OK");

        } catch (SQLException e) {
            return new Resposta(pedido, false, "Erro no servidor", TipoExcecao.Exception, e);
        }

    }



    @Override
    public ArrayList<Song> getMusicas(Utilizador utilizador) {
        try{
            int id = getIDUtilizador(utilizador);
            if(id == -1) throw new SQLException("O utilizador " + utilizador.getName() + " não foi encontrado");

            ArrayList<Song> arrayList = new ArrayList<>();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * from musicas where autor = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            utilizador.resetPassword();

            while(resultSet.next()){
                String nome = resultSet.getString("nome");
                String album = resultSet.getString("album");
                String genero = resultSet.getString("nome");
                String filename = resultSet.getString("ficheiro");
                int duracao = resultSet.getInt("duracao");
                int ano = resultSet.getInt("ano");

                arrayList.add(new Song(nome,utilizador, album, ano, duracao, genero, filename));
            }

            preparedStatement.close();
            return arrayList;

        }catch (SQLException e){
            System.out.println("Erro de SQL no getMusicas: " + e.getMessage());
            return null;
        }
    }

    private int getIDUtilizador(Utilizador utilizador) throws SQLException {

        PreparedStatement getIDUtilizadorStatement = conn.prepareStatement("SELECT id from utilizadores where nome = ?");
        getIDUtilizadorStatement.setString(1, utilizador.getName());
        ResultSet userResultSet = getIDUtilizadorStatement.executeQuery();

        if(!userResultSet.next())
            return -1;
        int id = userResultSet.getInt("id");
        getIDUtilizadorStatement.close();

        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
        DBName = "Servidor"+id;

        String serverRunningPath = System.getProperty("user.dir");
        File temp = new File(serverRunningPath + SERVER_DIR);
        if(!temp.exists()) {
            if(!temp.mkdirs())
                throw new RuntimeException("Não consegui criar uma pasta para guardar os ficheiros mp3");
        }
        musicDir = new File(serverRunningPath + SERVER_DIR + id);
        if(musicDir.exists()) {
            System.out.println("[INFO] Tentar eliminar pasta de servidor ja existente...");
            musicDir.delete();
        }
        if(!musicDir.mkdir())
            throw new RuntimeException("Não consegui criar uma pasta para guardar os ficheiros mp3");

        musicDir.deleteOnExit();
    }


}
