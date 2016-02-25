package ua.mytreo.java.jwpopup.controller;

import ua.mytreo.java.jwpopup.dbservice.DBService;
import ua.mytreo.java.jwpopup.dbservice.DBException;
import ua.mytreo.java.jwpopup.dbservice.dataSets.ContactsDataSet;
import ua.mytreo.java.jwpopup.dbservice.dataSets.MessagesDataSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import ua.mytreo.java.jwpopup.sys.images.ImageHelper;
import ua.mytreo.java.jwpopup.sys.mailslot.MailSlotSender;
import ua.mytreo.java.jwpopup.sys.node.ChatBubble;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @author mytreo
 * @version 1.0
 * 18.11.2015.
 */
public class ChatController implements Initializable {
    @FXML
    public Button btnSend;
    @FXML
    public Button btnNew;
    @FXML
    public TextArea chatArea;
    @FXML
    private ListView<MessagesDataSet> chatListView;
    ObservableList<MessagesDataSet> chatList = FXCollections.observableArrayList();
    private Tooltip tooltipChatArea;

    private MailSlotSender mailSlotSender = new MailSlotSender();


    private Stage chatStage;
    private DBService dbService;
    private ContactsDataSet contact;
    private ChatController chatControllerController = this;

    public void setChatStage(Stage chatStage) {
        this.chatStage = chatStage;
    }

    public void setDbService(DBService dbService) {
        this.dbService = dbService;
    }

    public DBService getDbService() {
        return dbService;
    }

    public void setContact(ContactsDataSet contact) {
        this.contact = contact;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //tooltips
        tooltipChatArea = new Tooltip("Enter the message first!");
        tooltipChatArea.setAutoHide(true);
        //buttons
        btnSend.setGraphic(new ImageView(ImageHelper.getImageByName("send")));
        btnNew.setGraphic(new ImageView(ImageHelper.getImageByName("create")));
        //chatlist
        chatListView.setItems(chatList);
        chatListView.setCellFactory(new Callback<ListView<MessagesDataSet>, ListCell<MessagesDataSet>>() {
            @Override
            public ListCell<MessagesDataSet> call(ListView<MessagesDataSet> arg0) {
                return new ListCell<MessagesDataSet>() {
                    ListCell<MessagesDataSet> lc = this;

                    @Override
                    protected void updateItem(MessagesDataSet item, boolean bln) {
                        super.updateItem(item, bln);
                        if (item != null) {

                           /* Label lblMes = new Label(item.getText());
                            lblMes.getStyleClass().add(item.getFrom0to1() == 1 ? "chat-bubble-l" : "chat-bubble-r");
                            setGraphic(lblMes);

                            setText(item.getText());
                            setTextFill(item.getFrom0to1() == 0 ?  Color.RED : Color.GREEN);*/
                            ChatBubble chatBubble = new ChatBubble(item);
                            chatBubble.setChatController(chatControllerController);

                            setGraphic(chatBubble);

                        }
                    }
                };
            }
        });
    }

    public void refreshMessages() {
        try {
            chatList.clear();
            chatList.addAll(dbService.getMessagesByUser(contact.getId()));
            //chatListView.refresh();
        } catch (DBException e) {
            e.printStackTrace();
        }

    }

    public void btnSendClick(ActionEvent actionEvent) {
        //TODO send
        if (!chatArea.getText().isEmpty()) {
            MessagesDataSet message = new MessagesDataSet(0, chatArea.getText(), contact.getId(), 0, 0, (new Date()).getTime());
            try {
                dbService.addMessage(message);
            } catch (DBException e) {
                e.printStackTrace();
            }
  //          chatArea.clear();
            refreshMessages();
            //ссылка на тот самый бабл
             mailSlotSender.sendMessageTo(contact.getNamePC(),chatArea.getText());
        } else {
            tooltipChatArea.show(chatArea, chatStage.getX() + chatArea.getLayoutX() + chatArea.getWidth() / 2, chatStage.getY() + chatArea.getLayoutY() + chatArea.getHeight() / 2);
        }

    }

    public void btnNewClick(ActionEvent actionEvent) {
        chatArea.clear();
    }

    public void printChatArea(String message) {
        chatArea.setText(message);
    }

}
