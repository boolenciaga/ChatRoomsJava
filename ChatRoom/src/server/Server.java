package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {

    // A datagram socket is the sending or receiving point for a packet delivery service.
    // Each packet sent or received on a datagram socket is individually addressed and routed.
    // Multiple packets sent from one machine to another may be routed differently, and may arrive in any order.
    private static DatagramSocket socket;


    private static boolean running;

    // start the server
    // create the resources needed and initialize them
    public static void start(int port) {
        try {
            socket = new DatagramSocket(port);
            running = true;     // server is running
            listen();           // start new thread
            System.out.println("Server started on port: " + port);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // send a message to every connected client
    private static void broadcast(String message) {

    }

    // send messages to individual clients
    private static void send() {

    }

    // will have a thread and wait for messages to arrive
    private static void listen() {
        Thread listenThread = new Thread("ChatRoom Listener"){
            public void run() {
                try {
                    // server is running
                    while(running) {
                        byte[] data = new byte[1024]; // receiving messages. use a byte array to get a data
                        // using packet to trace it
                        DatagramPacket packet = new DatagramPacket(data, data.length); // writing to our data variable
                        socket.receive(packet); // when socket gets a message, writes to the byte array

                        String message = new String(data);
                        message = message.substring(0, message.indexOf("\\e"));

                        // MANAGE MESSAGE
                        broadcast(message);
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }; listenThread.start();
    }

    // stop the server without closing the program
    public static void stop() {

    }
}
