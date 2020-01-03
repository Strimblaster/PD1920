package Servidor;

import Comum.*;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.PedidoDeserializer;
import Comum.Pedidos.Serializers.PedidoSyncDeserializer;
import Servidor.Interfaces.*;
import Servidor.Runnables.*;
import Servidor.Threads.MulticastListenerThread;
import Servidor.Threads.PingThread;
import Servidor.Utils.MulticastConfirmationMessage;
import Servidor.Utils.MulticastMessage;
import Servidor.Utils.PedidoSync;
import Servidor.Utils.ThreadMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

public class Comunicacao extends Thread implements Listener, Constants, ServerConstants {

    public final ArrayList<ServerInfo> servidores;
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocketDS;
    private DatagramSocket datagramSocketMulticast;
    private DatagramSocket datagramSocketSync;
    private MulticastSocket multicastSocket;
    private Observable server;
    public ServerInfo myServerInfo;
    private InetAddress multicastAddr;


    public Comunicacao(Servidor servidor) throws IOException {
        serverSocket = new ServerSocket(0);
        datagramSocketDS = new DatagramSocket();
        datagramSocketDS.setSoTimeout(TIMEOUT_5s);
        datagramSocketMulticast = new DatagramSocket();
        datagramSocketSync = new DatagramSocket();
        servidores = new ArrayList<>();
        server = servidor;


        multicastAddr = InetAddress.getByName(MULTICAST_ADDR);

        multicastSocket = new MulticastSocket(MULTICAST_PORT);
        multicastSocket.joinGroup(multicastAddr);


        multicastAddr = InetAddress.getByName(MULTICAST_ADDR);
        servidor.setListener(this);
    }

    private int requestServerID() throws IOException {
        String porta = Integer.toString(serverSocket.getLocalPort());
        byte[] b = porta.getBytes();
        int id;

        DatagramPacket p = new DatagramPacket(b, b.length, InetAddress.getByName(IP_DS), SERVER_PORT_DS);
        datagramSocketDS.send(p);
        datagramSocketDS.receive(p);
        id = Integer.parseInt(new String(p.getData(), 0, p.getLength()));
        p = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
        datagramSocketDS.receive(p);
        String json = new String(p.getData(), 0, p.getLength());

        Type listType = new TypeToken<ArrayList<ServerInfo>>(){}.getType();
        ArrayList<ServerInfo> servers = new Gson().fromJson(json, listType);
        servers.forEach( serverInfo -> {
            if(serverInfo.getId() == id)
                myServerInfo = serverInfo;
        });
        synchronized (servidores){
            servidores.clear();
            servidores.addAll(servers);
        }
        myServerInfo.setPort(datagramSocketMulticast.getLocalPort());
        return id;
    }

    @Override
    public void run() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Pedido.class, new PedidoDeserializer()).create();

        try{
            while(true){
                Socket s = serverSocket.accept();
                System.out.println("[INFO] - [Comunicação]: Novo pedido recebido de: " + s.getInetAddress().getHostName() + ":" + s.getPort());
                InputStream inputStream = s.getInputStream();
                byte[] bytes = new byte[PKT_SIZE];
                int read = inputStream.read(bytes);
                String str = new String(bytes, 0, read);
                Pedido pedido;
                try {
                    pedido = gson.fromJson(str, Pedido.class);
                } catch (JsonParseException e){
                    e.printStackTrace();
                    System.out.println("Não foi possivel deserializar o pedido recebido");
                    continue;
                }
                Runnable pedidoRunnable;


                if(pedido instanceof PedidoLogin)
                    pedidoRunnable = new LoginRunnable(s,(PedidoLogin) pedido,server);
                else if(pedido instanceof PedidoSignUp)
                    pedidoRunnable = new SignUpRunnable(s, (PedidoSignUp) pedido, server);
                else if(pedido instanceof PedidoUploadFile)
                    pedidoRunnable = new UploadFileRunnable(s, (PedidoUploadFile) pedido, server, ThreadMode.Normal);
                else if(pedido instanceof PedidoMusicas)
                    pedidoRunnable = new GetMusicasRunnable(s, (PedidoMusicas) pedido, server);
                else if(pedido instanceof PedidoSearch)
                    pedidoRunnable = new SearchRunnable(s, (PedidoSearch) pedido, server);
                else if(pedido instanceof PedidoDownloadFile)
                    pedidoRunnable = new DownloadFileRunnable(s, (PedidoDownloadFile) pedido, server, ThreadMode.Normal);
                else if(pedido instanceof PedidoPlaylists)
                    pedidoRunnable = new GetPlaylistsRunnable(s, (PedidoPlaylists) pedido, server);
                else if(pedido instanceof PedidoNewPlaylist)
                    pedidoRunnable = new NewPlaylistRunnable(s, (PedidoNewPlaylist) pedido, server);
                else if(pedido instanceof PedidoAddSong)
                    pedidoRunnable = new AddSongRunnable(s, (PedidoAddSong) pedido, server);
                else if(pedido instanceof PedidoEditSong)
                    pedidoRunnable = new EditSongRunnable(s, (PedidoEditSong) pedido, server);
                else if(pedido instanceof PedidoEditPlaylist)
                    pedidoRunnable = new EditPlaylistRunnable(s, (PedidoEditPlaylist) pedido, server);
                else if(pedido instanceof PedidoDisconnect) {
                    Pedido pedidoDisc = ((PedidoDisconnect) pedido).getPedido();
                    if(pedidoDisc instanceof PedidoDownloadFile)
                        pedidoRunnable = new DownloadFileRunnable(s, (PedidoDownloadFile) pedidoDisc, server, ThreadMode.Disconnect);
                    else if(pedidoDisc instanceof PedidoUploadFile)
                        pedidoRunnable = new UploadFileRunnable(s, (PedidoUploadFile) pedidoDisc, server, ThreadMode.Disconnect);
                    else{
                        System.out.println("[INFO] - [Comunicação]: Recebi um pedido Disconnect não identificado");
                        continue;
                    }
                }
                else{
                    System.out.println("[INFO] - [Comunicação]: Recebi um pedido não identificado");
                    continue;
                }

                new Thread(pedidoRunnable).start();
            }
        }catch (SocketException ignored){

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void serverReady() throws IOException {
        new PingThread(datagramSocketDS, this).start();
        if(servidores.size() > 1)
            syncDB();
        new MulticastListenerThread(multicastSocket, datagramSocketMulticast, server, servidores, myServerInfo, this).start();
        start();
    }

    private void syncDB() throws IOException {
        MulticastMessage multicastMessage = new MulticastMessage(myServerInfo);
        byte[] bytes = new Gson().toJson(multicastMessage).getBytes();
        DatagramPacket packetSend = new DatagramPacket(bytes, bytes.length, multicastAddr, MULTICAST_PORT);
        datagramSocketSync.send(packetSend);

        Gson gsonPedidoSync = new GsonBuilder().registerTypeAdapter(PedidoSync.class, new PedidoSyncDeserializer()).create();
        while(true) {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
            datagramSocketSync.receive(datagramPacket);
            String json = new String(datagramPacket.getData(), 0 ,datagramPacket.getLength());
            PedidoSync pedidoSync = gsonPedidoSync.fromJson(json, PedidoSync.class);
            Pedido pedido = pedidoSync.getPedido();
            if(pedido == null) {
                packetSend.setAddress(datagramPacket.getAddress());
                packetSend.setPort(datagramPacket.getPort());
                datagramSocketSync.send(packetSend);
                break;
            }
            try {
                if (pedido instanceof PedidoSignUp)
                    server.insertUser(pedido.getUtilizador());
                else if (pedido instanceof PedidoUploadFile) {
                    byte[] file = null;
                    if(pedidoSync.getFile() != null) file = Base64.getDecoder().decode(pedidoSync.getFile());
                    server.saveSongFile_Partial(((PedidoUploadFile) pedido).getMusica(), file);
                }
                else if (pedido instanceof PedidoNewPlaylist)
                    server.insertPlaylist(pedido.getUtilizador(), ((PedidoNewPlaylist) pedido).getNome());
                else if (pedido instanceof PedidoAddSong) {
                    PedidoAddSong pedidoAddSong = (PedidoAddSong) pedido;
                    server.insertSong(pedidoAddSong.getUtilizador(), pedidoAddSong.getPlaylist(), pedidoAddSong.getSong());
                }
                packetSend.setAddress(datagramPacket.getAddress());
                packetSend.setPort(datagramPacket.getPort());

                datagramSocketSync.send(packetSend);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void needID() throws IOException {
        server.setID(requestServerID());
    }

    @Override
    public void serverExit() {
        try {
            serverSocket.close();
        } catch (IOException ignored) { }
        datagramSocketDS.close();
        datagramSocketMulticast.close();
        multicastSocket.close();
        datagramSocketSync.close();
    }

    @Override
    public void newUser(String username, String password) {
        MulticastMessage message = new MulticastMessage(myServerInfo, new PedidoSignUp(new Utilizador(username, password)), null);

        sendMulticastMessage(message);
    }

    @Override
    public void newPlaylist(Utilizador utilizador, String nomePlaylist) {
        MulticastMessage message = new MulticastMessage(myServerInfo, new PedidoNewPlaylist(utilizador, nomePlaylist));

        sendMulticastMessage(message);
    }

    @Override
    public void newSong(Utilizador utilizador, Song song) {
        MulticastMessage message = new MulticastMessage(myServerInfo, new PedidoUploadFile(utilizador, song), null);

        sendMulticastMessage(message);
    }

    @Override
    public void sendPedidoSync(PedidoSync pedidoSync, ServerInfo serverInfo) {
        byte[] bytes = new Gson().toJson(pedidoSync).getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, serverInfo.getIp(), serverInfo.getPort());
        try {
            datagramSocketSync.send(datagramPacket);
            datagramSocketSync.receive(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void newSongFile(Utilizador utilizador, Song song, byte[] file) {
        MulticastMessage message = new MulticastMessage(myServerInfo, new PedidoUploadFile(utilizador, song));
        int nread;
        byte[] b = new byte[PKT_ENCODER];

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file);
        while((nread = byteArrayInputStream.read(b, 0, b.length)) != -1){
            ByteArrayInputStream buff = new ByteArrayInputStream(b, 0, nread);
            String encodedFile = Base64.getEncoder().encodeToString(buff.readAllBytes());
            message.setFile(encodedFile);
            sendMulticastMessage(message);
        }

        message.setFile(null);
        sendMulticastMessage(message);
        System.out.println("Musica " + song.getNome() + " sincronizada");
    }

    @Override
    public void newSongPlaylist(Utilizador utilizador, Playlist playlist, Song song) {
        MulticastMessage message = new MulticastMessage(myServerInfo, new PedidoAddSong(utilizador, song, playlist), null);

        sendMulticastMessage(message);
    }


    private synchronized void sendMulticastMessage(MulticastMessage message) {
        String json = new Gson().toJson(message);
        byte[] bytes = json.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, multicastAddr, MULTICAST_PORT);
        try {
            datagramSocketMulticast.setSoTimeout(10000);
            datagramSocketMulticast.send(datagramPacket);
            int i = 0;
            while (i != servidores.size()-1) {
                DatagramPacket packet = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
                datagramSocketMulticast.receive(packet);
                String jsonConfirmation = new String(packet.getData(), 0, packet.getLength());
                MulticastConfirmationMessage confirmationMessage = new Gson().fromJson(jsonConfirmation, MulticastConfirmationMessage.class);

                if(!confirmationMessage.isSucess()) continue;
                i++;
            }

        }catch (SocketException ex) {
            System.out.println("Houve um servidor que não respondeu ao multicast");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkPrimary() {
        int myid = myServerInfo.getId();
        ServerInfo maisVelho = new ServerInfo(null,-1,-1);
        synchronized (servidores) {
            for (ServerInfo server : servidores) {
                if (server.getPingCount() >= maisVelho.getPingCount())
                    maisVelho = server;
            }
        }
        return maisVelho.getId() == myid;
    }
}
