package Servidor;

import Comum.*;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.PedidoDeserializer;
import Servidor.Interfaces.*;
import Servidor.Runnables.*;
import Servidor.Threads.MulticastListenerThread;
import Servidor.Threads.PingThread;
import Servidor.Utils.MulticastConfirmationMessage;
import Servidor.Utils.MulticastMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.Base64;

public class Comunicacao extends Thread implements IEvent, Constants, ServerConstants {

    public final ArrayList<ServerInfo> servidores;
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;
    private MulticastSocket multicastSocketConfirmation;
    private IServer server;
    public ServerInfo myServerInfo;
    private InetAddress multicastAddr;


    public Comunicacao(Servidor servidor) throws IOException {
        serverSocket = new ServerSocket(0);
        datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(TIMEOUT_5s);
        servidores = new ArrayList<>();
        server = servidor;


        multicastAddr = InetAddress.getByName(MULTICAST_ADDR);
        InetAddress multicastAddrConfirmation = InetAddress.getByName(MULTICAST_ADDR_CONFIRMATION);

        multicastSocket = new MulticastSocket(MULTICAST_PORT);
        multicastSocketConfirmation = new MulticastSocket(MULTICAST_PORT_CONFIRMATION);

        multicastSocket.joinGroup(multicastAddr);
        multicastSocketConfirmation.joinGroup(multicastAddrConfirmation);


        multicastAddr = InetAddress.getByName(MULTICAST_ADDR);
        servidor.setListener(this);
    }

    private int requestServerID() throws IOException {
        String porta = Integer.toString(serverSocket.getLocalPort());
        byte[] b = porta.getBytes();
        int id;

        DatagramPacket p = new DatagramPacket(b, b.length, InetAddress.getByName(IP_DS), SERVER_PORT_DS);
        datagramSocket.send(p);
        datagramSocket.receive(p);
        id = Integer.parseInt(new String(p.getData(), 0, p.getLength()));
        p = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
        datagramSocket.receive(p);
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
                Pedido pedido = gson.fromJson(str, Pedido.class);
                Runnable pedidoRunnable;


                if(pedido instanceof PedidoLogin)
                    pedidoRunnable = new LoginRunnable(s,(PedidoLogin) pedido,server);
                else if(pedido instanceof PedidoSignUp)
                    pedidoRunnable = new SignUpRunnable(s, (PedidoSignUp) pedido, server);
                else if(pedido instanceof PedidoUploadFile)
                    pedidoRunnable = new UploadFileRunnable(s, (PedidoUploadFile) pedido, server);
                else if(pedido instanceof PedidoMusicas)
                    pedidoRunnable = new GetMusicasRunnable(s, (PedidoMusicas) pedido, server);
                else if(pedido instanceof PedidoSearch)
                    pedidoRunnable = new SearchRunnable(s, (PedidoSearch) pedido, server);
                else if(pedido instanceof PedidoDownloadFile)
                    pedidoRunnable = new DownloadFileRunnable(s, (PedidoDownloadFile) pedido, server);
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
                    System.out.println("PedidoDisconnect recebido");
                    continue;
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
    public void serverReady() {
        new PingThread(datagramSocket, this).start();
        new MulticastListenerThread(multicastSocket, datagramSocket, server, servidores, myServerInfo).start();
        start();
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
        datagramSocket.close();

        multicastSocket.close();
        multicastSocketConfirmation.close();
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
    public void newSongFile(Utilizador utilizador, Song song, byte[] file) {
        MulticastMessage message = new MulticastMessage(myServerInfo, new PedidoUploadFile(utilizador, song));
        int nread;
        byte[] b = new byte[PKT_SIZE/2];
        byte[] encodedFile = Base64.getEncoder().encode(file);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encodedFile);
        while((nread = byteArrayInputStream.read(b, 0, b.length)) != -1){
            message.setFile(new String(b, 0, nread));
            sendMulticastMessage(message);
        }

        message.setFile(null);
        sendMulticastMessage(message);
    }

    @Override
    public void newSongPlaylist(Utilizador utilizador, Playlist playlist, Song song) {

    }


    private synchronized void sendMulticastMessage(MulticastMessage message) {
        String json = new Gson().toJson(message);
        byte[] bytes = json.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, multicastAddr, MULTICAST_PORT);
        try {
            System.out.println("Enviei: " + json);
            datagramSocket.send(datagramPacket);
            int i = 0;
            while (i != servidores.size()-1) {
                DatagramPacket packet = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
                multicastSocketConfirmation.receive(packet);
                String jsonConfirmation = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                MulticastConfirmationMessage confirmationMessage = new Gson().fromJson(jsonConfirmation, MulticastConfirmationMessage.class);

                System.out.println("Recebi Confirmação: " + jsonConfirmation);
                if(!checkMessage(confirmationMessage)) continue;
                i++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkMessage(MulticastConfirmationMessage message) {

        ServerInfo receiver = message.getReceiver();

        //Verifica se fui eu que mandei a mensagem
        if(message.getSender().getId() == myServerInfo.getId()) return false;

        //Verifica se é para mim
        if(receiver != null)
            if(receiver.getId() != myServerInfo.getId())
                return false;
        return true;
    }

}
