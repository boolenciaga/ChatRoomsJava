package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

class Global
{
    static HashMap<String, Socket> socketMap = new HashMap<>();

    static Socket socketWithClientManager;
    static ObjectOutputStream toClientManager;
    static ObjectInputStream fromClientManager;

    static void initializeSocketWithCM(int port)
    {
        try
        {
            socketWithClientManager = new Socket("localhost", port);
            if(socketWithClientManager.isConnected())
            {
                toClientManager = new ObjectOutputStream(socketWithClientManager.getOutputStream());
                fromClientManager = new ObjectInputStream(socketWithClientManager.getInputStream());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    static Socket socketWithConnectToChatroom;
    static ObjectOutputStream toConnectToChatroom;
    static ObjectInputStream fromConnectToChatroom;

    static void initializeSocketWithConnToChatRoom(int port)
    {
        try
        {
            socketWithConnectToChatroom = new Socket("localhost", port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
