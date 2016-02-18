package ua.mytreo.java.sys.node;

import ua.mytreo.java.dbservice.DBException;
import ua.mytreo.java.dbservice.dataSets.MessagesDataSet;
import ua.mytreo.java.controller.ChatController;
import ua.mytreo.java.controller.MoreController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mytreo
 * @version 1.0
 */
public class ChatBubble extends Region {
    private ChatController chatController;
    private MessagesDataSet message;
    private final String resourcePath = "../fxml/chatbubble.fxml";

    @FXML
    private TextArea textMessage;
    @FXML
    private Label lblDate;
    @FXML
    private ProgressIndicator pBar;
    @FXML
    private Button btnReply;
    @FXML
    private Button btnMore;
    @FXML
    private Button btnDel;

    private SimpleDateFormat sdf = new SimpleDateFormat();

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public ChatBubble(MessagesDataSet message) {
        this.message = message;
        this.loadFXML();
        textMessage.setText(message.getText());

        textMessage.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        textMessage.getStyleClass().add(message.getFrom0to1() == 1 ? "chat-bubble-r" : "chat-bubble-l");
        // textMessage.getStyleClass().add(message.getFrom0to1() == 1 ? "chat-bubble-l2 : "chat-bubble-r2");

        pBar.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        pBar.setVisible(message.getFrom0to1() == 1);
        pBar.setProgress(message.getSuccess() == 1 ? 100 : 0);
        lblDate.setText(sdf.format(new Date(message.getTime())));

        //buttons
        btnDel.setGraphic(new ImageView(new Image("file:resources/system/delete.png")));
        btnMore.setGraphic(new ImageView(new Image("file:resources/system/more.png")));
        btnReply.setGraphic(new ImageView(new Image("file:resources/system/reply.png")));
    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader();

        loader.setController(this);
        loader.setLocation(this.getViewURL());

        try {
            Node root = loader.load();
            this.getChildren().add(root);
            setNodeOrientation(message.getFrom0to1() == 0 ? NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getViewPath() {
        return String.format(resourcePath, this.getClass().getSimpleName());
    }

    private URL getViewURL() {
        return this.getClass().getResource(this.getViewPath());
    }

    @Override
    protected void layoutChildren() {
        for (Node node : getChildren()) {
            layoutInArea(node, 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.TOP);
        }
    }

    public void btnReplyClick(ActionEvent actionEvent) {
        String[] strings = message.getText().split("\n");
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(">").append(s);
        }
        chatController.printChatArea(sb.toString());
    }

    public void btnMoreClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../fxml/more.fxml"));
            Scene scene = new Scene(loader.load());
            Stage moreStage = new Stage();
            moreStage.setScene(scene);
            ((MoreController)loader.getController()).printMoreAreaText(message.getText());
            moreStage.setTitle("Message from "+chatController.getDbService().getContact(message.getContact_id()).getName());
            moreStage.getIcons().add(new Image("file:resources/system/trayicon.png"));
            moreStage.show();
        } catch (DBException | IOException e) {
            e.printStackTrace();
        }

    }

    public void btnDelClick(ActionEvent actionEvent) {
        try {
            chatController.getDbService().removeMessage(message);
            chatController.refreshMessages();
        } catch (DBException e) {
            e.printStackTrace();
        }
    }
}
