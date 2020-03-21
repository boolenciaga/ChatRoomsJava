import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class Server
{
    //driver for testing class
    public static void main(String[] args)
    {
        new Server();
    }


    // DATA MEMBERS
    private int serverPort = 6000;
    private int numOfClients = 0;

    private ArrayList<ClientConnection> clientList = new ArrayList<>();
    private ArrayBlockingQueue<Messages.ChatMsg> msgQueue = new ArrayBlockingQueue<>(1000);


    // METHODS
    public Server()
    {
        System.out.println("Server turned on\n");

        //turn on message publisher
        new Thread(new Publisher()).start();

        try
        {
            //establish server on a port
            ServerSocket ss = new ServerSocket(serverPort);

            //server open for connections while some condition
            while(true)
            {
                Socket socket = ss.accept();

                ++numOfClients;

                displayConnectionStatus(socket);

                clientList.add(new ClientConnection(socket, numOfClients));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class Publisher implements Runnable
    {
        @Override
        public void run()
        {
            while(true)
            {
                try
                {
                    //pull message off queue
                    Messages.ChatMsg nextMsg = msgQueue.take();

                    //propagate message to all running clients
                    for (ClientConnection client : clientList)
                    {
                        if(client.myThread.isAlive())
                            client.sendMessage(nextMsg);
                    }
                }
                catch (InterruptedException e) {
                    System.out.println("exception caught in PUBLISHER-run()***\n" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    private class ClientConnection implements Runnable
    {
        private Socket serverToClientSocket;
        private int clientNumber;
        private Thread myThread;

        ObjectInputStream objectInputFromClient;
        ObjectOutputStream objectOutputToClient;

        public ClientConnection(Socket socket, int clientNumber)
        {
            serverToClientSocket = socket;
            this.clientNumber = clientNumber;

            //Establish IO streams
            try
            {
                objectInputFromClient = new ObjectInputStream(serverToClientSocket.getInputStream());
                objectOutputToClient = new ObjectOutputStream(serverToClientSocket.getOutputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            //Form a new thread for client connection
            myThread = new Thread(this);
            myThread.start();
        }

        public void sendMessage(Messages.ChatMsg o)
        {
            try
            {
                objectOutputToClient.writeObject(o);
            }
            catch (IOException e) {
                System.out.println("exception caught in sendMessage() of Client "+clientNumber+"***\n");
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            try
            {
                //send client their number
                objectOutputToClient.writeInt(clientNumber);
                objectOutputToClient.flush();

                //Serve the client
                while(true)
                {
                    //receive messages
                    Messages.ChatMsg receivedMsg = (Messages.ChatMsg) objectInputFromClient.readObject();

                    System.out.println("Server RECEIVED : " + receivedMsg.txt + " {from "+receivedMsg.sentByUser+"}\n");

                    //store message in blocking queue for publisher
                    msgQueue.add(receivedMsg);
                }
            }
            catch(IOException | ClassNotFoundException e) {
                System.out.println("exception caught in CLIENT-run() -- " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /*********************************************************************************/


    private void displayConnectionStatus(Socket socket)
    {
        if(socket.isConnected())
        {
            InetAddress clientAddress = socket.getInetAddress();

            System.out.println("Server-Client connection established...");
            System.out.println("(Connected to " + clientAddress.getHostName() + " [IP: " + clientAddress.getHostAddress() + "])\n\n");
        }
        else
        {
            System.out.println("-- SOCKET NOT CONNECTED --\n");
        }
    }
}
