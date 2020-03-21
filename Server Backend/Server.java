import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class Server
{
    //driver for testing class
    public static void main(String[] args)
    {
        new Server();
    }


    private int portNumber = 6000;
    private int numOfClients = 0;


    private ArrayBlockingQueue<Messages.ChatMsg> msgQueue = new ArrayBlockingQueue<Messages.ChatMsg>(1000);


    public Server()
    {
        System.out.println("Server turned on\n");

        try
        {
            //establish server on a port
            ServerSocket ss = new ServerSocket(portNumber);

            //start publisher on a thread


            //server open for connections while some condition
            while(true)
            {
                Socket socket = ss.accept();

                ++numOfClients;

                displayConnectionStatus(socket);

                //Form a new thread for client connection
                new Thread(new ClientConnection(socket, numOfClients)).start();
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
                    Messages.ChatMsg nextMsg = msgQueue.take();

                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class ClientConnection implements Runnable
    {
        private Socket serverToClientSocket;
        private int clientNumber;

        public ClientConnection(Socket socket, int clientNumber)
        {
            serverToClientSocket = socket;
            this.clientNumber = clientNumber;
        }

        @Override
        public void run()
        {
            try
            {
                //Establish IO streams
                ObjectInputStream objectInputFromClient = new ObjectInputStream(serverToClientSocket.getInputStream());
                ObjectOutputStream objectOutputToClient = new ObjectOutputStream(serverToClientSocket.getOutputStream());

                BufferedReader strInputFromClient = new BufferedReader(new InputStreamReader(serverToClientSocket.getInputStream()));
                PrintWriter strOutputToClient = new PrintWriter(serverToClientSocket.getOutputStream(), true);

                DataOutputStream primOutputToClient = new DataOutputStream(serverToClientSocket.getOutputStream());

                //send client their number
                primOutputToClient.write(clientNumber);

                //Serve the client
                while(true)
                {
                    //receive messages
                    Messages.ChatMsg receivedMsg = (Messages.ChatMsg) objectInputFromClient.readObject();

                    System.out.println("Server RECEIVED : " + receivedMsg.txt + " {from "+receivedMsg.sentByUser+"}\n");

                    //store message in blocking queue
                    msgQueue.add(receivedMsg);


//                    outputToClient.println(incomingString + " {sent back from server}");
                }
            }
            catch(IOException | ClassNotFoundException e) {
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
