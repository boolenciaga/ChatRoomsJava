package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class chatRoomController
{
    @FXML
    private TextArea messageLog;

    @FXML
    private TextField messagingBox;

    @FXML
    private Button sendButton;

    @FXML
    void sendButtonClicked(ActionEvent event)
    {
        if(!messagingBox.getText().isEmpty())
        {
            System.out.println("button clicked");
            Node source = (Node) event.getSource();
            String sceneName = (String) (source.getScene().getUserData());
            Socket socketToUse = Global.socketMap.get(sceneName);

            System.out.println("socket attained");
            try
            {
                ObjectOutputStream toConnection = new ObjectOutputStream(socketToUse.getOutputStream());
                ObjectInputStream fromConnection = new ObjectInputStream(socketToUse.getInputStream());

                System.out.println("text in box: " + messagingBox.getText());

                toConnection.writeUTF(messagingBox.getText());
                toConnection.flush();

                System.out.println("wrote it");

                String str = fromConnection.readUTF();      //TRIPPPPPPIN BALLLLZZZZZZ
                System.out.println(str);
                messageLog.setText(str + "\n\n");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


//        System.out.println(event.getEventType());
//        System.out.println(event.getSource());
//        System.out.println(event.toString());
//        System.out.println(event.getEventType().getName());
//        System.out.println(event.getTarget());
//
//
//        Node source = (Node) event.getSource();
//        System.out.println((String) (source.getScene().getUserData()));
    }

}
