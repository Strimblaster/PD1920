package Servidor;

import Comum.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.SocketException;

public class ServidorThread extends Thread implements Constants {

    Servidor servidor;
    DatagramPacket datagramPacket;

    public ServidorThread(Servidor servidor) {
        this.servidor = servidor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);

                //Espera packet de um servidor (ping)
                servidor.dsSocket.receive(datagramPacket);

                //Envia de volta uma mensagem a dizer que está on
                String string = "Estou on";
                byte[] resp = string.getBytes();
                datagramPacket.setData(resp);
                datagramPacket.setLength(resp.length);

                servidor.dsSocket.send(datagramPacket);

            } catch (SocketException e) {
                return;
            } catch (IOException e) {
                System.out.println("[Erro] [ServidorThread] - Erro: " + e.getMessage());
            }
        }
    }
}
