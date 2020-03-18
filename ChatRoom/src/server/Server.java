package server;

import java.net.DatagramSocket;

public class Server {

    private static DatagramSocket socket;


    // start the server
    // create the resources needed and initialize them
    public static void start(int port) {
        try {
            socket = new DatagramSocket(port);
            System.out.println("Server started on port: " + port);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // send a message to every connected client
    private static void broadcast() {

    }

    // send messages to individual clients
    private static void send() {

    }

    // will have a thread and wait for messages to arrive
    private static void listen() {

    }

    // stop the server without closing the program
    public static void stop() {

    }
}
