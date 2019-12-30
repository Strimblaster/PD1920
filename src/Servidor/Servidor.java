package Servidor;

import Comum.*;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.*;
import Servidor.Interfaces.IServer;
import Servidor.Interfaces.IEvent;
import Servidor.Interfaces.ServerConstants;

import java.io.*;
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

            while(true){
                String s = sc.next();
                System.out.println(s);
                if(s.equals("sair")) break;
            }
            servidor.exit();

        } catch (SocketException e) {
            System.out.println("[ERRO] Houve um problema com o Socket:\n"+ e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO] Houve com a base de dados:\n"+ e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERRO] Houve um erro de IO:\n"+ e.getMessage());
        }
    }

    private void exit() throws SQLException {
        conn.close();
        listener.serverExit();
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
    public boolean login(String username, String password) throws InvalidPasswordException, InvalidUsernameException {
        try{
            Utilizador utilizador = new Utilizador(username, password);
            PreparedStatement s = conn.prepareStatement("SELECT nome,password FROM utilizadores WHERE nome=?");
            s.setString(1, utilizador.getName());
            ResultSet resultSet = s.executeQuery();
            if(!resultSet.next())
                throw new InvalidUsernameException("Username não foi encontrado");
            if(!resultSet.getString("password").equals(password))
                throw new InvalidPasswordException("Password Incorreta");

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean signUp(String username, String password) throws InvalidUsernameException {

        try{
            Utilizador utilizador = new Utilizador(username, password);
            PreparedStatement s = conn.prepareStatement("SELECT nome FROM utilizadores WHERE nome=?");
            s.setString(1,utilizador.getName());
            ResultSet resultSetUsername = s.executeQuery();
            if(resultSetUsername.next()){
                throw new InvalidUsernameException("Username já está registado");
            }
            s.executeUpdate("INSERT INTO utilizadores(nome, password) VALUES (\'"+utilizador.getName()+"\', \'"+utilizador.getPassword()+"\')");

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    public String uploadFile(Utilizador utilizador, Song musica) throws InvalidSongDescriptionException {
        if(musica.getAlbum() == null) throw new InvalidSongDescriptionException("Nome do Album Invalido");
        if(musica.getAlbum().equals("")) throw new InvalidSongDescriptionException("Nome do Album Invalido");

        if(musica.getGenero() == null) throw new InvalidSongDescriptionException("Genero Invalido");
        if(musica.getGenero().equals("")) throw new InvalidSongDescriptionException("Genero Invalido");

        if(musica.getNome() == null) throw new InvalidSongDescriptionException("Nome de Musica Invalido");
        if(musica.getNome().equals("")) throw new InvalidSongDescriptionException("Nome de Musica Invalido");

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

            return id_musica + ".mp3";

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public byte[] downloadFile(Utilizador utilizador, Song musica) {

        PedidoDownloadFile pedido = new PedidoDownloadFile(utilizador,musica);

        try {
            //Procurar a musica na BD
            PreparedStatement statement = conn.prepareStatement("select * from musicas where nome=?");
            statement.setString(1,  musica.getNome());
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()){
                return null;
            }

            String filename = resultSet.getString("ficheiro");
            statement.close();

            pedido.getMusica().setFilename(filename);

            byte[] file = new byte[0];
            byte[] buffer = new byte[PKT_SIZE];
            int nRead;

            File fileToSend = new File(musicDir.getAbsolutePath() + File.separator + filename);

            FileInputStream inputStream = new FileInputStream(fileToSend);
            while((nRead=inputStream.read(buffer))!=-1) {
                byte[] temp = new byte[file.length + nRead];
                System.arraycopy(file, 0, temp, 0, file.length);
                System.arraycopy(buffer, 0, temp, file.length, nRead);
                file = temp;
            }

            return file;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
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
                Song song = resultSetToMusica(resultSet);
                arrayList.add(song);
            }

            preparedStatement.close();
            return arrayList;

        }catch (SQLException e){
            System.out.println("Erro de SQL no getMusicas: " + e.getMessage());
            return null;
        }
    }

    @Override
    public FilteredResult search(Utilizador utilizador, boolean songs, boolean playlists, String nome, String album, String genero, int ano, int duracao) {
        ArrayList<Song> songsArrayList = new ArrayList<>();
        ArrayList<Playlist> playlistsArrayList = new ArrayList<>();
        try {
            if(songs){
                String query = constructQuerySongs(nome, album, genero, ano, duracao);
                System.out.println(query);
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    songsArrayList.add(new Song(
                            resultSet.getString("nome"),
                            resultSet.getString("album"),
                            resultSet.getInt("ano"),
                            resultSet.getInt("duracao"),
                            resultSet.getString("genero"),
                            resultSet.getString("ficheiro")));
            }

            if(playlists){
                String query = constructQueryPlaylist(nome);
                System.out.println(query);
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    String nomePlaylist = resultSet.getString("nome");
                    ArrayList<Song> musicas = new ArrayList<>();
                    int idPlaylist = resultSet.getInt("id");

                    PreparedStatement selectMusicasStatement = conn.prepareStatement("SELECT * FROM lt_playlists_musicas where idPlaylists = ?");
                    selectMusicasStatement.setInt(1,id);
                    ResultSet resultSetMusicas = selectMusicasStatement.executeQuery();

                    while (resultSetMusicas.next()){
                        int idMusica = resultSetMusicas.getInt("idMusicas");
                        PreparedStatement statement = conn.prepareStatement("SELECT * FROM musicas where id = ?");
                        statement.setInt(1,idMusica);
                        ResultSet resultSet1 = statement.executeQuery();
                        resultSet1.next();
                        Song musica = resultSetToMusica(resultSet1);
                        musicas.add(musica);
                        statement.close();
                    }

                    playlistsArrayList.add(new Playlist(nomePlaylist, utilizador, musicas, idPlaylist));
                    selectMusicasStatement.close();
                }
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new FilteredResult(songsArrayList, playlistsArrayList);
        }

        return new FilteredResult(songsArrayList, playlistsArrayList);
    }

    private String constructQueryPlaylist(String nome) {
        StringBuilder base = new StringBuilder("SELECT * FROM playlists");
        StringBuilder condicoes = new StringBuilder();
        int contador = 0;

        if(validToQueryBuilder(nome)){
            condicoes.append(" nome = '").append(nome + "'");
            contador++;
        }
        if(contador > 0) {
            base.append(" WHERE");
            base.append(condicoes);
        }
        return base.toString();
    }

    @Override
    public boolean newPlaylist(Utilizador utilizador, String nome) throws InvalidPlaylistNameException {
        if(nome==null) throw new InvalidPlaylistNameException("Nome deve ser preenchido");
        try{
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * from playlists where nome = ?");
            preparedStatement.setString(1, nome);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                throw new InvalidPlaylistNameException();
            preparedStatement.close();
            
            int id = getIDUtilizador(utilizador);

            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO playlists(nome, criador) VALUES (?,?)");
            insertStatement.setString(1, nome);
            insertStatement.setInt(2,id);
            insertStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Playlist> getPlaylists(Utilizador utilizador) {
        try{
            int id = getIDUtilizador(utilizador);
            if(id == -1) throw new SQLException("O utilizador " + utilizador.getName() + " não foi encontrado");

            ArrayList<Playlist> arrayList = new ArrayList<>();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * from playlists where criador = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();


            while(resultSet.next()){
                ArrayList<Song> songs = new ArrayList<>();
                String nome = resultSet.getString("nome");
                int idPlaylist = resultSet.getInt("id");

                PreparedStatement selectMusicasStatement = conn.prepareStatement("SELECT * FROM lt_playlists_musicas where idPlaylists = ?");
                selectMusicasStatement.setInt(1,idPlaylist);
                ResultSet resultSetMusicas = selectMusicasStatement.executeQuery();

                while (resultSetMusicas.next()){
                    int idMusica = resultSetMusicas.getInt("idMusicas");
                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM musicas where id = ?");
                    statement.setInt(1,idMusica);
                    ResultSet resultSet1 = statement.executeQuery();
                    resultSet1.next();
                    Song musica = resultSetToMusica(resultSet1);
                    songs.add(musica);
                    statement.close();
                }

                arrayList.add(new Playlist(nome, utilizador, songs, idPlaylist));
                selectMusicasStatement.close();
            }

            preparedStatement.close();
            return arrayList;

        }catch (SQLException e){
            System.out.println("Erro de SQL no getPlaylists: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean addSong(Utilizador utilizador, Playlist playlist, Song song) throws InvalidPlaylistNameException, InvalidSongDescriptionException {
        try{
            //Procura a playlist com este nome
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * from playlists where nome = ?");
            preparedStatement.setString(1, playlist.getNome());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next())
                throw new InvalidPlaylistNameException("Playlist não existe");

            int idPlaylist = resultSet.getInt("id");
            preparedStatement.close();

            //Procura a musica com este nome
            PreparedStatement preparedStatementMusica = conn.prepareStatement("SELECT * from musicas where nome = ?");
            preparedStatementMusica.setString(1, song.getNome());
            ResultSet resultSetMusica = preparedStatementMusica.executeQuery();
            if(!resultSetMusica.next())
                throw new InvalidSongDescriptionException("Musica não existe");

            int idMusica = resultSetMusica .getInt("id");
            preparedStatementMusica .close();



            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO lt_playlists_musicas(idMusicas, idPlaylists) VALUES (?,?)");
            insertStatement.setInt(1,idMusica);
            insertStatement.setInt(2,idPlaylist);
            insertStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean editFile(Utilizador utilizador, Song song) {
        try{

            PreparedStatement ps = conn.prepareStatement("SELECT * from musicas where nome = ?");
            ps.setString(1, song.getNome());
            ResultSet resultSetMusica = ps.executeQuery();
            if(resultSetMusica.next())
                return false;

            PreparedStatement preparedStatementMusica = conn.prepareStatement("UPDATE musicas SET nome = ?, album=?, genero=?, ano=?, duracao=? WHERE id = ?");
            preparedStatementMusica.setString(1, song.getNome());
            preparedStatementMusica.setString(2, song.getAlbum());
            preparedStatementMusica.setString(3, song.getGenero());
            preparedStatementMusica.setInt(4, song.getAno());
            preparedStatementMusica.setInt(5, song.getDuracao());
            preparedStatementMusica.setInt(6, song.getId());

            preparedStatementMusica.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean editPlaylist(Utilizador utilizador, Playlist playlist) {
        try{

            PreparedStatement ps = conn.prepareStatement("SELECT * from playlists where nome = ? AND id!=?");
            ps.setString(1, playlist.getNome());
            ps.setInt(2, playlist.getId());
            ResultSet resultSetPlaylist = ps.executeQuery();
            if(resultSetPlaylist.next())
                return false;

            PreparedStatement preparedStatementPlaylist = conn.prepareStatement("UPDATE playlists SET nome = ? WHERE id = ?");
            preparedStatementPlaylist.setString(1, playlist.getNome());
            preparedStatementPlaylist.setInt(2, playlist.getId());

            preparedStatementPlaylist.executeUpdate();

            ArrayList<Song> songs = playlist.getMusicas();
            for(Song song : songs){
                PreparedStatement preparedStatementMusicas = conn.prepareStatement("DELETE FROM lt_playlists_musicas WHERE idMusicas = ? AND idPlaylists = ?");
                preparedStatementMusicas.setInt(1, song.getId());
                preparedStatementMusicas.setInt(2, playlist.getId());

                int ret2 = preparedStatementMusicas.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    private Utilizador getUtilizador(int id) throws SQLException {

        PreparedStatement getIDUtilizadorStatement = conn.prepareStatement("SELECT * from utilizadores where id = ?");
        getIDUtilizadorStatement.setInt(1, id);
        ResultSet userResultSet = getIDUtilizadorStatement.executeQuery();

        if(!userResultSet.next())
            return null;

        String nome = userResultSet.getString("nome");

        getIDUtilizadorStatement.close();

        return new Utilizador(nome);
    }

    private Song resultSetToMusica(ResultSet resultSet) throws SQLException {
        String nome = resultSet.getString("nome");
        String album = resultSet.getString("album");
        String genero = resultSet.getString("nome");
        String filename = resultSet.getString("ficheiro");
        int duracao = resultSet.getInt("duracao");
        int ano = resultSet.getInt("ano");
        int idAutor = resultSet.getInt("autor");
        int id = resultSet.getInt("id");

        return new Song(nome,getUtilizador(idAutor), album, ano, duracao, genero, filename, id);
    }

    @Override
    public void setID(int id) {
        this.id = id;
        DBName = "Servidor"+id;

        createServerDirectory(id);

    }

    private void createServerDirectory(int id) {
        //Path da diretoria em que o servidor está a correr
        String serverRunningPath = System.getProperty("user.dir");

        //Se a pasta temp/servers não existe, cria-a
        File temp = new File(serverRunningPath + SERVER_DIR);
        if(!temp.exists()) {
            if(!temp.mkdirs())
                throw new RuntimeException("Não consegui criar uma pasta para guardar os ficheiros mp3");
        }

        //Dentro da pasta temp/servers tenta criar uma pasta com o id do servidor
        musicDir = new File(serverRunningPath + SERVER_DIR + id);
        //Verifica antes se ela já existe e apaga-a se existir
        if(musicDir.exists()) {
            System.out.println("[INFO] Tentar eliminar pasta de servidor ja existente...");
            String[]files = musicDir.list();
            for(String s: files){
                File currentFile = new File(musicDir.getPath(),s);
                currentFile.delete();
            }
            if(!musicDir.delete())
                throw new RuntimeException("Não consegui eliminar a pasta de servidor já existente");
        }
        if(!musicDir.mkdirs())
            throw new RuntimeException("Não consegui criar uma pasta para guardar os ficheiros mp3");

    }


    //Não gosto de como isto está feito... se quiserem podem mudar desde que funfe
    private String constructQuerySongs(String nome, String album, String genero, int ano, int duracao) {
        StringBuilder base = new StringBuilder("SELECT * FROM musicas");
        StringBuilder condicoes = new StringBuilder();
        int contador = 0;

        if(validToQueryBuilder(nome)){
            condicoes.append(" nome = '").append(nome + "'");
            contador++;
        }
        if(validToQueryBuilder(album)){
            if(contador > 0) condicoes.append(" AND");
            condicoes.append(" album = '").append(album + "'");
            contador++;
        }
        if(validToQueryBuilder(genero)){
            if(contador > 0) condicoes.append(" AND");
            condicoes.append(" genero = '").append(genero + "'");
            contador++;
        }
        if(validToQueryBuilder(ano)){
            if(contador > 0) condicoes.append(" AND");
            condicoes.append(" ano = ").append(ano);
            contador++;
        }
        if(validToQueryBuilder(duracao)){
            if(contador > 0) condicoes.append(" AND");
            condicoes.append(" duracao = ").append(duracao);
            contador++;
        }
        if(contador > 0) {
            base.append(" WHERE");
            base.append(condicoes);
        }
        return base.toString();
    }

    boolean validToQueryBuilder(String s){
        if(s == null) return false;
        return !s.equals("");
    }
    boolean validToQueryBuilder(int i){ return i!=-1;}
}
