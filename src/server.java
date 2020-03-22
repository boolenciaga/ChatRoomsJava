import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    public static void main(String[] args) throws IOException
    {
        ServerSocket socket = new ServerSocket(9999);
        Socket s = socket.accept();

        System.out.println("client connected");

        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        String str  = bf.readLine();
        System.out.println("client: " + str);

        PrintWriter pr = new PrintWriter(s.getOutputStream());
        pr.println("You're connected to the server.");
        pr.flush();

        while(s.isConnected())
        {

        }
    }
}
