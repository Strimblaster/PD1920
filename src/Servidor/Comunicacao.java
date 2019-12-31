package Servidor;

import Comum.Constants;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.PedidoDeserializer;
import Comum.ServerInfo;
import Servidor.Interfaces.*;
import Servidor.Runnables.*;
import Servidor.Threads.MulticastListenerThread;
import Servidor.Threads.PingThread;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;

public class Comunicacao extends Thread implements IEvent, Constants, ServerConstants {

    public final ArrayList<ServerInfo> servidores;
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;
    private IServer server;
    public ServerInfo myServerInfo;





    public Comunicacao(Servidor servidor) throws IOException {
        serverSocket = new ServerSocket(0);
        datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(TIMEOUT_5s);
        servidores = new ArrayList<>();
        server = servidor;
        multicastSocket = new MulticastSocket(MULTICAST_PORT);
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
    public void serverReady() {
        new PingThread(datagramSocket, this).start();
        new MulticastListenerThread(multicastSocket, server, servidores, myServerInfo).start();
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
}
