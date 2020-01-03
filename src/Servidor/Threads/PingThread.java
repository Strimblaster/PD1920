package Servidor.Threads;

import Comum.Constants;
import Comum.ServerInfo;
import Servidor.Comunicacao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class PingThread extends Thread implements Constants {

    private DatagramSocket datagramSocket;
    private Comunicacao comunicacao;

    public PingThread(DatagramSocket datagramSocket, Comunicacao comunicacao) {
        this.datagramSocket = datagramSocket;
        this.comunicacao = comunicacao;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);

                //Espera packet de um servidor (ping)
                datagramSocket.receive(datagramPacket);
                String json = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

                Type listType = new TypeToken<ArrayList<ServerInfo>>(){}.getType();
                ArrayList<ServerInfo> servers = new Gson().fromJson(json, listType);
                synchronized (comunicacao.servidores){
                    comunicacao.servidores.clear();
                    comunicacao.servidores.addAll(servers);
                }

                //Envia de volta uma mensagem a dizer que está on
                String string = "Estou on";
                byte[] resp = string.getBytes();
                datagramPacket.setData(resp);
                datagramPacket.setLength(resp.length);

                datagramSocket.send(datagramPacket);

            } catch (SocketException e) {
                return;
            } catch (IOException e) {
                System.out.println("[Erro] [PingThread] - Erro: " + e.getMessage());
            }
        }
    }
}
