package server;

import java.net.InetAddress;

// store individual client information
public class ClientInfo {
    // represents an Internet Protocol (IP) address.
    private InetAddress address;
    private int port;
    private int id;
    private String userName;

    // constructor w/ parameters
    public ClientInfo(String userName, int id, InetAddress address, int port) {
        this.userName = userName;
        this.id = id;
        this.address = address;
        this.port = port;
    }

    // username getter
    public String getUserName() {
        return userName;
    }

    // id getter
    public int getID() {
        return id;
    }

    // address getter
    public InetAddress getAddress() {
        return address;
    }

    // port getter
    public int getPort() {
        return port;
    }
}
