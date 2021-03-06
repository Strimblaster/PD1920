package Comum;

import java.io.Serializable;
import java.net.InetAddress;

public class ServerInfo implements Serializable {

    private InetAddress ip;
    private int port;
    private int id;
    private int pingCount;

    public ServerInfo(InetAddress ip, int port, int id) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        pingCount=0;
    }

    public ServerInfo(InetAddress ip, int port, int id, int pingCount) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.pingCount=pingCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        String ip = this.ip.getHostName();
        return "Server " + id + " - " + ip + ":" + port;
    }

    public int getPingCount() {
        return pingCount;
    }

    public void incrementPingCount() {
        pingCount++;
    }
}
