import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String args[])
    {
        try
        {
            ServerSocket ss = new ServerSocket(5000);

            Socket socket = ss.accept();

            System.out.println("\nServer-Client connection established...\n");

            InetAddress clientAddress = socket.getInetAddress();
            System.out.println("(Connected to " + clientAddress.getHostName() + ")\n\n");

            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String incomingString = serverInput.readLine();
            System.out.println("Server RECEIVED : "+incomingString+"\n");

            PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);
            serverOutput.println(incomingString+" {sent back from server}\n\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
