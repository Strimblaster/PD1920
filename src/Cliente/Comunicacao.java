package Cliente;

import Cliente.Interfaces.IComunicacaoCliente;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidServerException;
import Comum.*;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.Pedido;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Serializers.PedidoDeserializer;
import Comum.Pedidos.Serializers.RespostaDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.*;

public class Comunicacao implements IComunicacaoCliente, Constants {

    ServerInfo serverInfo;

    public Comunicacao() throws SocketException {
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
    public Resposta login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        PedidoLogin pedidoLogin = new PedidoLogin(new Utilizador(username, password));
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());
            Gson gson = new Gson();
            InputStream inputStream = tcpSocket.getInputStream();
            OutputStream outputStream = tcpSocket.getOutputStream();

            String json = gson.toJson(pedidoLogin);
            outputStream.write(json.getBytes());

            byte[] buffer = new byte[PKT_SIZE];
            int nread = inputStream.read(buffer);

            json = new String(buffer, 0 , nread);

            gson = new GsonBuilder().registerTypeAdapter(Resposta.class, new RespostaDeserializer()).create();

            Resposta resposta = gson.fromJson(json, Resposta.class);
            if(resposta.getException() != null) throwException(resposta.getException());

            return gson.fromJson(json, Resposta.class);

        } catch (IOException e) {
            System.out.println("Ocorreu um erro no login: " + e.getMessage());
        }
        return null;
    }

    private void throwException(Exception exception) throws InvalidPasswordException, InvalidUsernameException {
        if(exception instanceof InvalidPasswordException)
            throw (InvalidPasswordException) exception;
        if(exception instanceof InvalidUsernameException)
            throw (InvalidUsernameException) exception;
    }

    @Override
    public Resposta signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        PedidoSignUp pedidoSignUp = new PedidoSignUp(new Utilizador(username, password));
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());
            Gson gson = new Gson();
            InputStream inputStream = tcpSocket.getInputStream();
            OutputStream outputStream = tcpSocket.getOutputStream();

            String json = gson.toJson(pedidoSignUp);
            outputStream.write(json.getBytes());

            byte[] buffer = new byte[PKT_SIZE];
            int nread = inputStream.read(buffer);

            json = new String(buffer, 0 , nread);
            gson = new GsonBuilder().registerTypeAdapter(Resposta.class, new RespostaDeserializer()).create();

            Resposta resposta = gson.fromJson(json, Resposta.class);
            if(resposta.getException() != null) throwException(resposta.getException());

            return resposta;

        } catch (IOException e) {
            System.out.println("Ocorreu um erro no login: " + e.getMessage());
        }
        return null;
    }
}
