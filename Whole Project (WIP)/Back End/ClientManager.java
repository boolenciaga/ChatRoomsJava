import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager
{
    public static void main(String[] args)
    {
        new ClientManager();
    }

    // sockets connecting to ServerManager
    private Socket socket;
    private ObjectOutputStream objectOutputToServerManager;
    private ObjectInputStream objectInputFromServerManager;

    // sockets connecting to GUI
    ServerSocket serverSocketForLogin;
    private Socket loginSocket;
    private ObjectOutputStream objectOutputToLogin;
    private ObjectInputStream objectInputFromLogin;

    private ArrayList<ConnectionToChatRoom> chatRoomConnections = new ArrayList<>();

    private ClientManager()
    {
        try
        {
            //establish socket to ServerManager
            socket = new Socket("localhost", 7777);

            if(socket.isConnected())
                System.out.println("Connected to ServerManager...\n");
            else
                System.out.println("Connection failed...\n");

            //await connection to login GUI
            serverSocketForLogin = new ServerSocket(8000);
            loginSocket = serverSocketForLogin.accept();

            wrapSocketStreams();

            //collect and send user name to ServerManager
            final String myUserName = objectInputFromLogin.readUTF();
            objectOutputToServerManager.writeUTF(myUserName);
            objectOutputToServerManager.flush();

            while(true)
            {
                //collect room requests from login GUI
                String chatRoomDesired = objectInputFromLogin.readUTF();

                //check if this ClientManager already has a connection with requested room
                boolean roomAlreadyConnected = false;
                for(ConnectionToChatRoom connection : chatRoomConnections)
                {
                    if(chatRoomDesired.equals(connection.getChatRoomName()))
                    {
                        //maybe send "room exists" info back to GUI
                        //maybe handle the case where request room that exists and is already open
                        roomAlreadyConnected = true;
                        break;
                    }
                }

                if(roomAlreadyConnected)
                {
                    //send do not open signal to GUI
                    objectOutputToLogin.writeBoolean(false);
                    objectOutputToLogin.flush();
                    continue; //skip the rest of loop body
                }
                else //send chat room requests to ServerManager
                {
                    objectOutputToLogin.writeBoolean(true); //send open signal to GUI
                    objectOutputToLogin.flush();
                    objectOutputToServerManager.writeUTF(chatRoomDesired);
                    objectOutputToServerManager.flush();
                }

                //receive chat room info from ServerManager
                ChatRoomInfoMsg roomMsg = (ChatRoomInfoMsg) objectInputFromServerManager.readObject();

                if(roomMsg.hadToBeCreated)
                    System.out.println("the room \"" + roomMsg.chatRoomName + "\" didn't exist, was created by server...");

                //make an object to handle the connection and chatting
                ConnectionToChatRoom newChatRoom = new ConnectionToChatRoom(roomMsg.chatRoomPort, roomMsg.chatRoomName, myUserName);
                chatRoomConnections.add(newChatRoom);

                //send GUI the name/port to connect to the new ConnectionToChatRoom
                objectOutputToLogin.writeUTF(roomMsg.chatRoomName);
                objectOutputToLogin.writeInt(newChatRoom.getMessagingWindowPort());
                objectOutputToLogin.flush();
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("exception caught in ClientManager's constructor!");
            e.printStackTrace();
        }
    }

    private void wrapSocketStreams()
    {
        try
        {
            //wrap ServerManager socket streams
            objectOutputToServerManager = new ObjectOutputStream(socket.getOutputStream());
            objectInputFromServerManager = new ObjectInputStream(socket.getInputStream());

            //wrap login socket streams
            objectInputFromLogin = new ObjectInputStream(loginSocket.getInputStream());
            objectOutputToLogin = new ObjectOutputStream(loginSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("exception caught in wrapSocketStream()\n\n");
            e.printStackTrace();
        }
    }
}
