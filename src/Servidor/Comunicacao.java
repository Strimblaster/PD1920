package Servidor;

import Comum.Constants;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.PedidoDeserializer;
import Servidor.Interfaces.*;
import Servidor.Runnables.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class Comunicacao extends Thread implements IEvent, Constants, ServerConstants {
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private IServer server;
    private Thread pingThread;



    public Comunicacao(Servidor servidor) throws IOException {
        serverSocket = new ServerSocket(0);
        datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(TIMEOUT_5s);

        server = servidor;
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

        //multicast
//        dsSocket.receive(p);
//        Gson gson = new Gson();
//        String arrayList = new String(p.getData(), 0, p.getLength());
//
//        this.servidores = gson.fromJson(arrayList, ArrayList.class);

        return id;
    }


    @Override
    public void serverReady() {
        pingThread = new Thread(new PingRunnable(datagramSocket));
        pingThread.start();
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
                System.out.println("ola");
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
