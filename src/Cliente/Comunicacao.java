package Cliente;

import Cliente.Interfaces.IComunicacaoCliente;
import Cliente.Interfaces.IEvent;
import Cliente.Runnables.DownloadFileRunnable;
import Cliente.Runnables.UploadFileRunnable;
import Comum.Exceptions.*;
import Comum.*;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Pedidos.Serializers.PlaylistDeserializer;
import Comum.Pedidos.Serializers.RespostaDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;

public class Comunicacao implements IComunicacaoCliente, Constants {

    ServerInfo serverInfo;
    IEvent event;
    File musicDir;

    public Comunicacao(IEvent clientController) {
        this.event = clientController;
    }

    @Override
    public ServerInfo getServerInfo() throws IOException, InvalidServerException {
        DatagramSocket udpSocket = new DatagramSocket();
        udpSocket.setSoTimeout(TIMEOUT_2s);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] incommingData = new byte[PKT_SIZE];


        PrintWriter out = new PrintWriter(byteArrayOutputStream,true);
        out.println("Olá DS!\n");
        DatagramPacket datagramPacket = new DatagramPacket(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.toByteArray().length, InetAddress.getByName(IP_DS), CLIENT_PORT_DS);
        udpSocket.send(datagramPacket);

        datagramPacket = new DatagramPacket(incommingData, incommingData.length);
        udpSocket.receive(datagramPacket);


        Gson gson = new Gson();
        String jsonServerInfo = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

        serverInfo = gson.fromJson(jsonServerInfo, ServerInfo.class);

        if(serverInfo == null)
            throw new InvalidServerException();

        return serverInfo;
    }

    @Override
    public void setMusicDir(File dir) {
        musicDir = dir;
    }

    @Override
    public boolean login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        PedidoLogin pedidoLogin = new PedidoLogin(new Utilizador(username, password));
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            try{
                enviaPedido(tcpSocket,pedidoLogin);
            }catch (IOException e){
                getServerInfo();
                login(username, password);
            }

            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return resposta.isSucess();

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException | InvalidPlaylistNameException | InvalidServerException e) {
            System.out.println("Ocorreu um erro no login: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException, InvalidServerException {
        PedidoSignUp pedidoSignUp = new PedidoSignUp(new Utilizador(username, password));
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            try{
                enviaPedido(tcpSocket,pedidoSignUp);
            }catch (IOException e){
                getServerInfo();
                login(username, password);
            }

            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return resposta.isSucess();

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException | InvalidPlaylistNameException e) {
            System.out.println("Ocorreu um erro no signUp: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String uploadFile(Utilizador utilizador, Song song) throws InvalidSongDescriptionException {
        PedidoUploadFile pedidoUploadFile = new PedidoUploadFile(utilizador, song);
        if(musicDir == null) throw new RuntimeException("[Erro] [Comunicação]: musicDir == null ");
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            //Envia pedido para saber se os parametros da Musica são validos
            try{
                enviaPedido(tcpSocket,pedidoUploadFile);
            }catch (IOException e){
                getServerInfo();
                uploadFile(utilizador, song);
            }

            //Recebe resposta do pedido
            Resposta resposta = recebeResposta(tcpSocket);

            Thread t = new  Thread(new UploadFileRunnable(tcpSocket, pedidoUploadFile, event, musicDir, ((PedidoUploadFile)resposta.getPedido()).getMusica().getFilename()));
            t.start();
            return null;

        } catch (IOException | ServerErrorException | InvalidPlaylistNameException | InvalidServerException e) {
            System.out.println("Ocorreu um erro no Upload: " + e.getMessage());
        } catch (InvalidUsernameException | InvalidPasswordException ignored) {
            //Mandamos sempre o Username e password para verificar se é mesmo o utilizador
            //Mas não verificamos porque não fala nada disso no enunciado ou seja nunca vão chegar estas exceções aqui
        }

        return null;
    }

    @Override
    public byte[] downloadFile(Utilizador utilizador, Song song) {
        PedidoDownloadFile pedidoDownloadFile = new PedidoDownloadFile(utilizador, song);
        if(musicDir == null) throw new RuntimeException("[Erro] [Comunicação]: musicDir == null ");
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            try{
                enviaPedido(tcpSocket,pedidoDownloadFile);
            }catch (IOException e){
                getServerInfo();
                downloadFile(utilizador, song);
            }

            Thread t = new  Thread(new DownloadFileRunnable(tcpSocket, pedidoDownloadFile, event, musicDir));
            t.start();
            return null;
        } catch (IOException | InvalidServerException e) {
            System.out.println("Ocorreu um erro no Download: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<Song> getMusicas(Utilizador utilizador) {
        PedidoMusicas pedidoMusicas = new PedidoMusicas(utilizador);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());
            InputStream inputStream = tcpSocket.getInputStream();
            byte[] buffer = new byte[PKT_SIZE];
            Gson gson = new Gson();

            try{
                enviaPedido(tcpSocket,pedidoMusicas);
            }catch (IOException e){
                getServerInfo();
                getMusicas(utilizador);
            }

            int nread = inputStream.read(buffer);
            System.out.println("[DEBUG] - Recebi " + nread + " bytes");
            String json = new String(buffer, 0 , nread);


            Type listType = new TypeToken<ArrayList<Song>>(){}.getType();
            ArrayList<Song> songs = gson.fromJson(json, listType);

            tcpSocket.close();
            return songs;

        } catch (IOException | InvalidServerException e) {
            System.out.println("Ocorreu um erro no signUp: " + e.getMessage());
        }
        return null;
    }

    @Override
    public FilteredResult search(Utilizador utilizador, boolean songs, boolean playlists, String nome, String album, String genero, int ano, int duracao) {
        PedidoSearch pedidoSearch = new PedidoSearch(utilizador, songs, playlists, nome, album, genero, ano, duracao);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            try{
                enviaPedido(tcpSocket,pedidoSearch);
            }catch (IOException e){
                getServerInfo();
                search(utilizador, songs, playlists, nome, album, genero, ano, duracao);
            }

            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return ((PedidoSearch)resposta.getPedido()).getFilteredResult();

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException | InvalidUsernameException | InvalidPasswordException | InvalidPlaylistNameException | InvalidServerException e) {
            System.out.println("Ocorreu um erro no pedido de Search: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean newPlaylist(Utilizador utilizador, String nome) throws InvalidPlaylistNameException {
        PedidoNewPlaylist pedidoNewPlaylist = new PedidoNewPlaylist(utilizador,nome);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            try{
                enviaPedido(tcpSocket,pedidoNewPlaylist);
            }catch (IOException e){
                getServerInfo();
                newPlaylist(utilizador, nome);
            }

            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return resposta.isSucess();

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException | InvalidUsernameException | InvalidPasswordException | InvalidServerException e) {
            System.out.println("Ocorreu um erro ao criar playlist: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ArrayList<Playlist> getPlaylists(Utilizador utilizador) {
        PedidoPlaylists pedidoPlaylists = new PedidoPlaylists(utilizador);
        Gson gson = new GsonBuilder().registerTypeAdapter(Playlist.class, new PlaylistDeserializer()).create();
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());
            InputStream inputStream = tcpSocket.getInputStream();

            try{
                enviaPedido(tcpSocket,pedidoPlaylists);
            }catch (IOException e){
                getServerInfo();
                getPlaylists(utilizador);
            }

            byte[] buffer = new byte[PKT_SIZE];
            int nread = inputStream.read(buffer);
            System.out.println("[DEBUG] - Recebi " + nread + " bytes");
            String json = new String(buffer, 0 , nread);


            Type listType = new TypeToken<ArrayList<Playlist>>(){}.getType();
            ArrayList<Playlist> playlists = gson.fromJson(json, listType);

            tcpSocket.close();
            return playlists;

        } catch (IOException | InvalidServerException e) {
            System.out.println("Ocorreu um erro ao criar playlist: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean addSong(Utilizador utilizador, Playlist playlist, Song song) {
        PedidoAddSong pedidoAddSong = new PedidoAddSong(utilizador, song, playlist);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            try{
                enviaPedido(tcpSocket,pedidoAddSong);
            }catch (IOException e){
                getServerInfo();
                addSong(utilizador, playlist, song);
            }

            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return resposta.isSucess();

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException | InvalidPlaylistNameException | InvalidUsernameException | InvalidPasswordException | InvalidServerException e) {
            System.out.println("Ocorreu um erro ao addicionar uma musica: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean editFile(Utilizador utilizador, Song song) {
        PedidoEditSong pedidoEditSong = new PedidoEditSong(utilizador, song);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            try{
                enviaPedido(tcpSocket,pedidoEditSong);
            }catch (IOException e){
                getServerInfo();
                editFile(utilizador, song);
            }

            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return resposta.isSucess();

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException | InvalidPlaylistNameException | InvalidUsernameException | InvalidPasswordException | InvalidServerException e) {
            System.out.println("Ocorreu um erro ao editar a musica: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean editPlaylist(Utilizador utilizador, Playlist playlist) {
        PedidoEditPlaylist pedidoEditPlaylist = new PedidoEditPlaylist(utilizador, playlist);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            try{
                enviaPedido(tcpSocket,pedidoEditPlaylist);
            }catch (IOException e){
                getServerInfo();
                editPlaylist(utilizador, playlist);
            }
            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return resposta.isSucess();

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException | InvalidPlaylistNameException | InvalidUsernameException | InvalidPasswordException | InvalidServerException e) {
            System.out.println("Ocorreu um erro ao editar a playlist: " + e.getMessage());
        }
        return false;
    }

    void enviaPedido(Socket socket, Pedido pedido) throws IOException {
        Gson gson = new Gson();
        OutputStream outputStream = socket.getOutputStream();
        String json = gson.toJson(pedido);

        try{
            outputStream.write(json.getBytes());
        } catch (IOException e){
            throw new IOException();
        }
        outputStream.flush();
    }

    Resposta recebeResposta(Socket socket) throws IOException, InvalidUsernameException, InvalidSongDescriptionException, InvalidPasswordException, ServerErrorException, InvalidPlaylistNameException {

        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[PKT_SIZE];
        int nread = inputStream.read(buffer);

        //System.out.println("DEBUG: " + nread + " bytes recebidos (Não apagar isto por enquanto pls)");

        String json = new String(buffer, 0 , nread);
        System.out.println("[DEBUG] - Recebi " + nread + " bytes");
        Gson gson = new GsonBuilder().registerTypeAdapter(Resposta.class, new RespostaDeserializer()).create();

        Resposta resposta = gson.fromJson(json, Resposta.class);

        if(resposta.getException() != null){
            socket.close();
            Exception exception = resposta.getException();
            if(exception instanceof InvalidPasswordException) {
                throw (InvalidPasswordException) exception;
            }
            else if(exception instanceof InvalidUsernameException) {
                throw (InvalidUsernameException) exception;
            }
            else if(exception instanceof InvalidSongDescriptionException) {
                throw (InvalidSongDescriptionException) exception;
            }
            else if(exception instanceof InvalidPlaylistNameException) {
                throw (InvalidPlaylistNameException) exception;
            }
            else{
                throw new ServerErrorException(resposta.getInfo() + ": " + exception.getMessage());
            }
        }

        return resposta;

    }

    @Override
    public String uploadFileDisconnected(Utilizador utilizador, PedidoUploadFile pedido, File cliMusicDir, String filename) {
        PedidoDisconnect pedidoDisconnected = new PedidoDisconnect(utilizador, pedido);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            enviaPedido(tcpSocket,pedidoDisconnected);

            Thread t = new  Thread(new UploadFileRunnable(tcpSocket, pedido, event, cliMusicDir, filename));
            t.start();
            return null;

        } catch (IOException e) {
            System.out.println("Ocorreu um erro no Upload: " + e.getMessage());
        }
        return null;
    }

    @Override
    public byte[] downloadFileDisconnected(Utilizador utilizador, PedidoDownloadFile pedido, File cliMusicDir) {
        PedidoDisconnect pedidoDisconnected = new PedidoDisconnect(utilizador, pedido);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            enviaPedido(tcpSocket,pedidoDisconnected);

            Thread t = new  Thread(new DownloadFileRunnable(tcpSocket, pedido, event, cliMusicDir));
            t.start();
            return null;

        } catch (IOException e) {
            System.out.println("Ocorreu um erro no Upload: " + e.getMessage());
        }
        return null;
    }
}
