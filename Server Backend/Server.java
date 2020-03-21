import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    //driver for testing class
    public static void main(String[] args)
    {
        new Server();
    }


    private int portNumber = 6000;
    private int numOfClients = 0;


    public Server()
    {
        System.out.println("Server turned on\n");

        try
        {
            //establish server on a port
            ServerSocket ss = new ServerSocket(portNumber);

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


    private static class ClientConnection implements Runnable
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
                BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(serverToClientSocket.getInputStream()));

                PrintWriter outputToClient = new PrintWriter(serverToClientSocket.getOutputStream(), true);

                //Serve the client
                while(true)
                {
                    String incomingString = inputFromClient.readLine();
                    System.out.println("Server RECEIVED : " + incomingString + " {from client #"+clientNumber+"}\n");

                    outputToClient.println(incomingString + " {sent back from server}");
                }
            }
            catch(IOException e) {
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
