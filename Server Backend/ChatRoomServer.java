import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class ChatRoomServer implements Runnable
{
    //DATA MEMBERS
    private ServerSocket ss;
    private int chatRoomPort;
    private final String chatRoomName;
    private Thread chatRoomThread;
    private int numInRoom = 0;

    private ArrayList<MemberConnection> membersInTheRoom = new ArrayList<>();
    private ArrayBlockingQueue<ChatMsg> msgQueue = new ArrayBlockingQueue<>(1000);


    //METHODS
    public ChatRoomServer(String roomName)
    {
        chatRoomName = roomName;

        try
        {
            //establish server on an available port
            ss = new ServerSocket(0);
            chatRoomPort = ss.getLocalPort();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        chatRoomThread = new Thread(this);
        chatRoomThread.start();
    }

    @Override
    public void run()
    {
        //turn on message publisher object for the whole room
        new Thread(new Publisher()).start();

        try
        {
            //chat room open for connections to new members
            while(true)
            {
                Socket socket = ss.accept();

                ++numInRoom;

                displayConnectionStatus(socket);

                membersInTheRoom.add(new MemberConnection(socket));
            }
        }
        catch (IOException e) {
            System.out.println("exception caught in " + chatRoomName + "'s run()");
            e.printStackTrace();
        }
    }


    private class MemberConnection implements Runnable
    {
        private Socket socketToMember;
        private String memberName;
        private Thread thisThread;

        ObjectInputStream objectInputFromClient;
        ObjectOutputStream objectOutputToClient;

        public MemberConnection(Socket socket)
        {
            socketToMember = socket;

            //Wrap the IO streams
            try
            {
                objectInputFromClient = new ObjectInputStream(socketToMember.getInputStream());
                objectOutputToClient = new ObjectOutputStream(socketToMember.getOutputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            //Send this connection on its own thread
            thisThread = new Thread(this);
            thisThread.start();
        }

        @Override
        public void run()
        {
            try
            {
                //read in client name
                memberName = objectInputFromClient.readUTF();

                //Receive messages from clients
                while(true)
                {
                    //receive messages
                    ChatMsg receivedMsg = (ChatMsg) objectInputFromClient.readObject();

                    System.out.println("Chat Room \"" + chatRoomName + "\" RECEIVED : " + receivedMsg.txt + " {from " + memberName + "}\n");

                    //repackage msg to ensure proper sentBy value
                    receivedMsg = new ChatMsg(receivedMsg, memberName);

                    //store message in blocking queue for publisher
                    msgQueue.add(receivedMsg);
                }
            }
            catch(IOException | ClassNotFoundException e)
            {
                System.out.println("exception caught in run() of "  + memberName + "'s MemberConnection object\n");
                e.printStackTrace();
            }
        }

        void sendMessageFromPublisher(ChatMsg o)
        {
            try
            {
                objectOutputToClient.writeObject(o);
            }
            catch (IOException e) {
                System.out.println("exception caught in sendMessageFromPub() of " + memberName + "'s MemberConnection object\n");
                e.printStackTrace();
            }
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
                    ChatMsg nextMsg = msgQueue.take();

                    //propagate message to all running clients
                    for (MemberConnection client : membersInTheRoom)
                    {
                        if(client.thisThread.isAlive() && !nextMsg.sentBy.equals(client.memberName))
                            client.sendMessageFromPublisher(nextMsg);
                    }
                }
                catch (InterruptedException e) {
                    System.out.println("exception caught in Publisher's run()\n");
                    e.printStackTrace();
                }
            }
        }
    }


    /*********************************************************************************/


    private void displayConnectionStatus(Socket socket)
    {
        if(socket.isConnected())
        {
            System.out.println("Room-Member connection established...");
            System.out.println("Room: " + chatRoomName);
            System.out.println("(Connected to " + socket.getPort() + " [IP: " + socket.getInetAddress().getHostAddress() + "])\n");
        }
        else
            System.out.println("-- SOCKET NOT CONNECTED --\n");
    }


    public int getChatRoomPort() {return chatRoomPort;}
}
