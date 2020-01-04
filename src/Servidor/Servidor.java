package Servidor;

import Comum.*;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.PedidoAddSong;
import Comum.Pedidos.PedidoNewPlaylist;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.PedidoUploadFile;
import Servidor.Interfaces.Observable;
import Servidor.Interfaces.Listener;
import Servidor.Interfaces.ServerConstants;
import Servidor.Utils.PedidoSync;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class Servidor implements ServerConstants, Constants, Observable {

    private Connection conn;
    private String DBName;
    private int id;
    private Listener listener;
    private File musicDir;
    private String argDBName;



    private Servidor() throws SQLException {
        this.conn = DriverManager.getConnection(DB_URL, USER, PASS);
        argDBName = null;
    }

    private Servidor(String argDBName) throws SQLException {
        this.conn = DriverManager.getConnection(DB_URL, USER, PASS);
        this.argDBName = argDBName;
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
            insertUser(utilizador);

            listener.newUser(username,password);
            s.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void saveSongFile_Full(Utilizador utilizador, Song musica, byte[] file) {
        try {
            //Vai buscar o ID da musica para saber o nome do ficheiro
            int id = getSongIDByName(musica);

            //Guarda o ficheiro na pasta do servidor
            saveFile(file, id);

            //Altera a Row da musica na tabela para ter o nome do ficheiro
            insertSongFilename(id);

            listener.newSongFile(utilizador, musica, file);

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
            int id_musica = insertNewSong(musica, id);
            utilizador.setId(id);
            musica.setAutor(utilizador);
            listener.newSong(utilizador, musica);

            return id_musica + ".mp3";

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public byte[] downloadFile(Utilizador utilizador, Song musica) {

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

            musica.setFilename(filename);

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
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    songsArrayList.add(resultSetToMusica(resultSet));
                }
            }

            if(playlists){
                String query = constructQueryPlaylist(nome);
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    String nomePlaylist = resultSet.getString("nome");
                    int idCriador = resultSet.getInt("criador");
                    ArrayList<Song> musicas = new ArrayList<>();
                    int idPlaylist = resultSet.getInt("id");

                    PreparedStatement selectMusicasStatement = conn.prepareStatement("SELECT * FROM lt_playlists_musicas where idPlaylists = ?");
                    selectMusicasStatement.setInt(1, idPlaylist);
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

                    PreparedStatement owner = conn.prepareStatement("SELECT * FROM utilizadores where nome = ?");
                    owner.setString(1, getUtilizador(idCriador).getName());
                    ResultSet resultOwner = owner.executeQuery();
                    resultOwner.next();
                    Utilizador user = resultSetToUtilizador(resultOwner);
                    playlistsArrayList.add(new Playlist(nomePlaylist, user, musicas, idPlaylist));
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

    private Utilizador resultSetToUtilizador(ResultSet resultOwner) throws SQLException {
        int id = resultOwner.getInt("id");
        String nome = resultOwner.getString("nome");

        return new Utilizador(id, nome);
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
            
            insertPlaylist(utilizador, nome);
            listener.newPlaylist(utilizador, nome);
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

            listener.newSongPlaylist(utilizador, playlist, song);
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

                preparedStatementMusicas.executeUpdate();
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
            throw new SQLException("Não existe utilizador com id " + id);

        String nome = userResultSet.getString("nome");

        getIDUtilizadorStatement.close();

        return new Utilizador(id,nome);
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

    private Song getMusicaByID(int id) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * from musicas where id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String nome = resultSet.getString("nome");
        String album = resultSet.getString("album");
        String genero = resultSet.getString("nome");
        String filename = resultSet.getString("ficheiro");
        int duracao = resultSet.getInt("duracao");
        int ano = resultSet.getInt("ano");
        int idAutor = resultSet.getInt("autor");
        preparedStatement.close();

        return new Song(nome,getUtilizador(idAutor), album, ano, duracao, genero, filename, id);
    }

    @Override
    public void setID(int id) {
        this.id = id;
        if(argDBName == null)
            DBName = "Servidor"+id;
        else
            DBName = argDBName+id;

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


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static void main(String[] args){
        try {
            System.out.println("[INFO] Servidor a arrancar...");
            Servidor servidor;
            InetAddress ip_DS = InetAddress.getByName(IP_DS);
            Scanner sc = new Scanner(System.in);

            if(args.length == 2) {
                servidor = new Servidor(args[1]);
                ip_DS = InetAddress.getByName(args[0]);
            }
            else if(args.length == 1)
                servidor = new Servidor(args[0]);
            else if(args.length == 0 )
                servidor = new Servidor();
            else{
                System.out.println("Usage:\nServer.jar ip dbName\nServer.jar dbName\nServer.jar");
                return;
            }

            new Comunicacao(servidor, ip_DS);

            servidor.requestID();

            System.out.println("[INFO] Atribuido o ID: "+ servidor.id);

            System.out.println("[INFO] A criar a base de dados...");
            servidor.createDatabase();

            System.out.println("[INFO] Servidor pronto");
            servidor.ready();


            while(true){
                String s = sc.next();
                if(s.equals("sair")) break;
            }
            servidor.exit();

        } catch (IllegalStateException e){
        }catch (NumberFormatException e) {
            System.out.println("Usage:\nServer.jar ip port db\nServer.jar ip port\nServer.jar db\nServer.jar");
        } catch (SocketException e) {
            System.out.println("[ERRO] Houve um problema com o Socket:\n"+ e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("[ERRO] Houve com a base de dados:\n"+ e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[ERRO] Houve um erro de IO:\n"+ e.getMessage());
            e.printStackTrace();
        }
    }

    private void exit() throws SQLException {
        conn.close();
        listener.serverExit();
    }

    private void ready() throws IOException {
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
    public void insertUser(Utilizador utilizador) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO utilizadores(nome, password) VALUES (?, ?)");
        statement.setString(1, utilizador.getName());
        statement.setString(2, utilizador.getPassword());
        statement.executeUpdate();
        statement.close();
    }

    @Override
    public void insertPlaylist(Utilizador utilizador, String nomePlaylist) throws SQLException {
        int id = getIDUtilizador(utilizador);

        PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO playlists(nome, criador) VALUES (?,?)");
        insertStatement.setString(1, nomePlaylist);
        insertStatement.setInt(2,id);
        insertStatement.executeUpdate();
    }

    @Override
    public void insertSong(Utilizador utilizador, Playlist playlist, Song song) {
            try{
                //Procura a playlist com este nome
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * from playlists where nome = ?");
                preparedStatement.setString(1, playlist.getNome());
                ResultSet resultSet = preparedStatement.executeQuery();
                if(!resultSet.next());

                int idPlaylist = resultSet.getInt("id");
                preparedStatement.close();

                //Procura a musica com este nome
                PreparedStatement preparedStatementMusica = conn.prepareStatement("SELECT * from musicas where nome = ?");
                preparedStatementMusica.setString(1, song.getNome());
                ResultSet resultSetMusica = preparedStatementMusica.executeQuery();
                if(!resultSetMusica.next());

                int idMusica = resultSetMusica .getInt("id");
                preparedStatementMusica .close();



                PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO lt_playlists_musicas(idMusicas, idPlaylists) VALUES (?,?)");
                insertStatement.setInt(1,idMusica);
                insertStatement.setInt(2,idPlaylist);
                insertStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    @Override
    public String checkSong(Song musica) {
        try {
            int songIDByName = getSongIDByName(musica);
            Song song = getMusicaByID(songIDByName);
            return song.getId() + ".mp3";
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void saveSongFile_Partial(Song musica, byte[] file) {
        try {
            int id;
            //Vai buscar o ID da musica para saber o nome do ficheiro
            try {
                id = getSongIDByName(musica);
            } catch (SQLException e) {
                insertNewSong(musica, musica.getAutor().getId());
                return;
            }

            if(file != null) {
                //Guarda o ficheiro na pasta do servidor
                saveFile(file, id);
            } else {
                //Altera a Row da musica na tabela para ter o nome do ficheiro
                insertSongFilename(id);
            }


        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    private int getSongIDByName(Song musica) throws SQLException {
        PreparedStatement getIDStatment = conn.prepareStatement("SELECT id FROM musicas WHERE nome = ?");
        getIDStatment.setString(1,musica.getNome());
        ResultSet resultSet = getIDStatment.executeQuery();
        if(!resultSet.next())
            throw new SQLException("Algo correu mal, o nome da musica não existe");
        int id = resultSet.getInt("id");
        getIDStatment.close();
        return id;
    }

    private void saveFile(byte[] file, int id) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(musicDir.getAbsolutePath() + File.separator + id + ".mp3", true);
        fileOutputStream.write(file);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private void insertSongFilename(int id) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("UPDATE musicas SET ficheiro = ? WHERE id = ?");
        preparedStatement.setString(1, id + ".mp3");
        preparedStatement.setInt(2, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private int insertNewSong(Song musica, int idAutor) throws SQLException {
        PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO musicas(nome, autor, album, duracao, ano, genero) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        insertStatement.setString(1,  musica.getNome());
        insertStatement.setInt(2,  idAutor);
        insertStatement.setString(3,  musica.getAlbum());
        insertStatement.setInt(4,  musica.getDuracao());
        insertStatement.setInt(5,  musica.getAno());
        insertStatement.setString(6,  musica.getGenero());
        insertStatement.executeUpdate();

        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        generatedKeys.next();

        int id_musica = generatedKeys.getInt(1);
        insertStatement.close();

        return id_musica;
    }

    @Override
    public void sync(ServerInfo serverInfo) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM utilizadores");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                String nome = resultSet.getString("nome");
                String password = resultSet.getString("password");

                listener.sendPedidoSync(new PedidoSync(new PedidoSignUp(new Utilizador(nome,password)), null), serverInfo);
            }
            statement.close();
            System.out.println("Users sincronizados");

            statement = conn.prepareStatement("SELECT * FROM musicas");
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                Song song = resultSetToMusica(resultSet);

                PedidoSync pedidoSync = new PedidoSync(new PedidoUploadFile(song.getAutor(), song), null);
                listener.sendPedidoSync(pedidoSync, serverInfo);
                if(song.getFilename() != null) {
                    int nread;
                    byte[] b = new byte[PKT_ENCODER];
                    byte[] file = getFile(song.getFilename());
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file);
                    while ((nread = byteArrayInputStream.read(b, 0, b.length)) != -1) {
                        ByteArrayInputStream buff = new ByteArrayInputStream(b, 0, nread);
                        String encodedFile = Base64.getEncoder().encodeToString(buff.readAllBytes());
                        pedidoSync.setFile(encodedFile);
                        listener.sendPedidoSync(pedidoSync, serverInfo);
                    }
                    pedidoSync = new PedidoSync(new PedidoUploadFile(song.getAutor(), song), null);
                    listener.sendPedidoSync(pedidoSync, serverInfo);
                }
            }
            statement.close();
            System.out.println("Musicas sincronizadas");

            statement = conn.prepareStatement("SELECT * FROM playlists");
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                String nome = resultSet.getString("nome");
                int criador = resultSet.getInt("criador");
                int idPlaylist = resultSet.getInt("id");
                Utilizador utilizador = getUtilizador(criador);

                listener.sendPedidoSync(new PedidoSync(new PedidoNewPlaylist(utilizador,nome), null), serverInfo);

                PreparedStatement musicasStatement = conn.prepareStatement("SELECT * FROM lt_playlists_musicas WHERE id = ?");
                musicasStatement.setInt(1, idPlaylist);
                ResultSet resultSetMusicas = musicasStatement.executeQuery();
                while(resultSetMusicas.next()){
                    int idMusica = resultSetMusicas.getInt("idMusicas");
                    Song song = getMusicaByID(idMusica);

                    listener.sendPedidoSync(new PedidoSync(new PedidoAddSong(utilizador, song, new Playlist(nome)), null), serverInfo);
                }
                resultSetMusicas.close();
            }
            statement.close();

            System.out.println("Playlists sincronizadas");




            listener.sendPedidoSync(new PedidoSync(), serverInfo);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getFile(String filename) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(musicDir.getAbsoluteFile() + File.separator + filename);
        byte[] b = fileInputStream.readAllBytes();
        fileInputStream.close();
        return b;
    }

}
