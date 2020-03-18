package server;

import java.net.InetAddress;

// store information
public class ClientInfo {
    private InetAddress address;
    private int port;
    private int id;
    private String userName;

    public ClientInfo(String userName, int id, InetAddress address, int port) {
        this.userName = userName;
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public int getID() {
        return id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
