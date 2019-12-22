package Cliente;

import Cliente.Interfaces.IComunicacaoCliente;
import Cliente.Interfaces.IEvent;
import Cliente.Runnables.UploadFileRunnable;
import Cliente.javaFX.SceneController;
import Comum.Exceptions.*;
import Comum.*;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.PedidoDeserializer;
import Comum.Pedidos.Serializers.RespostaDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.*;

public class Comunicacao implements IComunicacaoCliente, Constants {

    ServerInfo serverInfo;
    IEvent event;
    File musicDir;

    public Comunicacao(IEvent clientController, File musicDir) {
        this.event = clientController;
        this.musicDir = musicDir;
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

            enviaPedido(tcpSocket,pedidoLogin);

            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return resposta;

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException e) {
            System.out.println("Ocorreu um erro no login: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Resposta signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        PedidoSignUp pedidoSignUp = new PedidoSignUp(new Utilizador(username, password));
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            enviaPedido(tcpSocket,pedidoSignUp);

            Resposta resposta = recebeResposta(tcpSocket);

            tcpSocket.close();
            return resposta;

        } catch (IOException | InvalidSongDescriptionException | ServerErrorException e) {
            System.out.println("Ocorreu um erro no signUp: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Resposta uploadFile(Utilizador utilizador, Song song) throws InvalidSongDescriptionException {
        PedidoUploadFile pedidoUploadFile = new PedidoUploadFile(utilizador, song);
        try {
            Socket tcpSocket = new Socket(serverInfo.getIp(), serverInfo.getPort());

            //Envia pedido para saber se os parametros da Musica são validos
            enviaPedido(tcpSocket,pedidoUploadFile);

            //Recebe resposta do pedido
            Resposta resposta = recebeResposta(tcpSocket);

            Thread t = new  Thread(new UploadFileRunnable(tcpSocket, pedidoUploadFile, event, musicDir, ((PedidoUploadFile)resposta.getPedido()).getMusica().getFilename()));
            t.start();
            return resposta;

        } catch (IOException | ServerErrorException e) {
            System.out.println("Ocorreu um erro no Upload: " + e.getMessage());
        } catch (InvalidUsernameException | InvalidPasswordException ignored) {
            //Mandamos sempre o Username e password para verificar se é mesmo o utilizador
            //Mas não verificamos porque não fala nada disso no enunciado ou seja nunca vão chegar estas exceções aqui
        }

        return null;
    }



    void enviaPedido(Socket socket, Pedido pedido) throws IOException {
        Gson gson = new Gson();
        OutputStream outputStream = socket.getOutputStream();

        String json = gson.toJson(pedido);
        outputStream.write(json.getBytes());
        outputStream.flush();
    }

    Resposta recebeResposta(Socket socket) throws IOException, InvalidUsernameException, InvalidSongDescriptionException, InvalidPasswordException, ServerErrorException {

        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[PKT_SIZE];
        int nread = inputStream.read(buffer);

        String json = new String(buffer, 0 , nread);
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
            else{
                throw new ServerErrorException(resposta.getInfo() + ": " + exception.getMessage());
            }
        }

        return resposta;

    }

}
