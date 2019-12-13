package Servidor.Runnables;

import Comum.Constants;
import Servidor.Servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.SocketException;

public class PingRunnable implements Runnable, Constants {

    private DatagramSocket datagramSocket;

    public PingRunnable(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);

                //Espera packet de um servidor (ping)
                datagramSocket.receive(datagramPacket);

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
