package ua.mytreo.java.controller;

import ua.mytreo.java.dbservice.DBException;
import ua.mytreo.java.dbservice.DBService;
import ua.mytreo.java.dbservice.dataSets.ContactsDataSet;
import ua.mytreo.java.dbservice.dataSets.GroupsDataSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ua.mytreo.java.sys.dialog.DateInputDialog;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private DataFormat contactDataFormat = new DataFormat("mytreo.java.program.bwpopup2.ua.mytreo.java.dbservice.dataSets.ContactsDataSet");
    private final ContextMenu cMenuContact = new ContextMenu();
    private final ContextMenu cMenuGroup = new ContextMenu();

    @FXML
    private TreeView contactTree;
    @FXML
    public Button btnAddGroup;
    @FXML
    public Button btnRemoveMessages;
    @FXML
    public Button btnSettings;


    private DBService dbService;

    private ChatController chatControllerController;
    private InsUpdContact insUpdContactController;

    private Stage mainStage;
    private Stage chatStage;
    private Stage insUpdContactStage;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setDBService(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //buttons
        btnAddGroup.setGraphic(new ImageView(new Image("file:resources/system/addGroup.png")));
        btnRemoveMessages.setGraphic(new ImageView(new Image("file:resources/system/delmes.png")));
        btnSettings.setGraphic(new ImageView(new Image("file:resources/system/settings.png")));

        //menus--------------------------------------------------------
        MenuItem cmDelContact = new MenuItem("Delete Contact");
        cmDelContact.setOnAction((ActionEvent ev) -> {
            System.out.println("del Contact");
            try {
                dbService.removeContact((ContactsDataSet) contactTree.getTreeItem(contactTree.getSelectionModel().getSelectedIndex()).getValue());
                refreshContactList();
            } catch (DBException e) {
                System.out.println("Can't delete");
            }
        });

        MenuItem cmUpdContact = new MenuItem("Update Contact");
        cmUpdContact.setOnAction((ActionEvent ev) -> {
            System.out.println("upd Contact");
            try {
                ContactsDataSet contact = (ContactsDataSet) contactTree.getTreeItem(contactTree.getSelectionModel().getSelectedIndex()).getValue();
                contact = showInsUpdContactWindow(contact);
                if (contact != null) {
                    dbService.updContact(contact);
                    refreshContactList();
                }

            } catch (DBException e) {
                System.out.println("Can't update");
            }
        });

        MenuItem cmDelMessages = new MenuItem("Delete Messages from Contact");
        cmDelMessages.setOnAction((ActionEvent ev) -> {
            try {
                ContactsDataSet contact = (ContactsDataSet) contactTree.getTreeItem(contactTree.getSelectionModel().getSelectedIndex()).getValue();
                dbService.removeMessagesByContact(contact.getId());
            } catch (DBException e) {
                System.out.println("Can't delete messages");
            }
        });

        MenuItem cmAddContact = new MenuItem("Add Contact");
        cmAddContact.setOnAction((ActionEvent ev) -> {
            System.out.println("add Contact");
            try {

                ContactsDataSet contact = showInsUpdContactWindow(new ContactsDataSet());
                if (contact != null) {
                    dbService.addContact(contact);
                    refreshContactList();
                }
            } catch (DBException e) {
                System.out.println("Can't add contact");
            }

        });

        MenuItem cmRenameGroup = new MenuItem("Rename Group");
        cmRenameGroup.setOnAction((ActionEvent ev) -> {
            try {
                GroupsDataSet group = (GroupsDataSet) contactTree.getTreeItem(contactTree.getSelectionModel().getSelectedIndex()).getValue();

                TextInputDialog textInput = new TextInputDialog("");
                textInput.setGraphic(null);
                textInput.setHeaderText(null);
                textInput.setTitle("New Group Name is");
                textInput.getDialogPane().setContentText("Input name here:");
                String newGroupName = textInput.showAndWait().get();

                if (newGroupName != null && !newGroupName.isEmpty()) {
                    group.setGroup_name(newGroupName);
                    dbService.updGroup(group);
                    refreshContactList();
                }
            } catch (NoSuchElementException | DBException e) {
                System.out.println("Can't rename group");
            }
        });

        MenuItem cmDelGroup = new MenuItem("Delete Group");
        cmDelGroup.setOnAction((ActionEvent ev) -> {
            try {
                GroupsDataSet group = (GroupsDataSet) contactTree.getTreeItem(contactTree.getSelectionModel().getSelectedIndex()).getValue();
                dbService.removeGroup(group);
                refreshContactList();
            } catch (DBException e) {
                System.out.println("Can't del group");
            }
        });

        cMenuContact.getItems().addAll(cmDelContact, cmUpdContact, cmDelMessages);
        cMenuGroup.getItems().addAll(cmAddContact, cmRenameGroup, cmDelGroup);
        //cellFactory----------------------------------------------------------
        contactTree.setCellFactory((tv) -> {
            return new TreeCell<Object>() {
                @Override
                public void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisclosureNode(null);

                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (item instanceof GroupsDataSet) {
                            setText(((GroupsDataSet) item).getGroup_name());
                            try {
                                Image im = new Image("file:resources/system/arrow.png");
                                setGraphic(new ImageView(im));
                                // setGraphic(getTreeItem().getGraphic());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            setOnDragOver(new EventHandler<DragEvent>() {
                                @Override
                                public void handle(DragEvent event) {
                                    //    System.out.println("over "+item);
                                    event.acceptTransferModes(TransferMode.MOVE);
                                    event.consume();
                                }
                            });
                            setOnDragEntered(new EventHandler<DragEvent>() {
                                @Override
                                public void handle(DragEvent event) {
                                    //       System.out.println("entered " + item);
                                    setTextFill(Color.GREEN);
                                    event.consume();
                                }
                            });
                            setOnDragExited(new EventHandler<DragEvent>() {
                                @Override
                                public void handle(DragEvent event) {
                                    //        System.out.println("exited " + item);
                                    setTextFill(Color.BLACK);
                                    event.consume();
                                }
                            });
                            //to
                            setOnDragDropped((event) -> {
                                Dragboard db = event.getDragboard();
                                System.out.println("dropped into " + item + " " + db.getContent(contactDataFormat).toString());
                                ContactsDataSet cds = (ContactsDataSet) db.getContent(contactDataFormat);
                                cds.setGroup_id(((GroupsDataSet) item).getId());
                                try {
                                    dbService.updContact(cds);
                                    refreshContactList();
                                } catch (DBException e) {
                                    e.printStackTrace();
                                }
                                db.clear();
                                event.setDropCompleted(true);
                            });
                            //from
                            /*setOnDragDone(new EventHandler<DragEvent>() {
                                @Override
                                public void handle(DragEvent event) {
                                    Dragboard db = event.getDragboard();
                                    // System.out.println("done from  " + item+" "+db.getString());
                                }
                            });*/
                            TreeCell<Object> tc = this;
                            setOnMouseClicked((MouseEvent e) -> {
                                if (e.getButton() == MouseButton.SECONDARY)
                                    cMenuGroup.show(tc, e.getScreenX(), e.getScreenY());
                            });

                        } else {
                            setText(((ContactsDataSet) item).getName());
                            try {
                                Image im = new Image(("file:resources/face.png"));
                                setGraphic(new ImageView(im));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // add listeners to support dragging
                            setOnDragOver(event -> {
                                //    System.out.println("over "+item);
                                setTextFill(Color.BLACK);
                                event.acceptTransferModes(TransferMode.NONE);
                                event.consume();
                            });

                            TreeCell<Object> tc = this;
                            setOnDragDetected((event) -> {
                                //tc.startFullDrag();//full for mouse
                                Dragboard db = tc.startDragAndDrop(TransferMode.MOVE);
                                db.clear();
                                // Put a string on a dragboard
                                ClipboardContent content = new ClipboardContent();
                                // content.put(DataFormat.PLAIN_TEXT,item.toString());

                                content.put(contactDataFormat, item);

                                Image im = new Image("file:resources/face.png");
                                db.setDragViewOffsetX(-13);
                                db.setDragViewOffsetY(-10);
                                db.setDragView(im);

                                db.setContent(content);
                                event.consume();
                            });

                            setOnMouseClicked((MouseEvent e) -> {
                                if (e.getButton() == MouseButton.PRIMARY) {
                                    if (e.getClickCount() == 2) {
                                        showChatWindow((ContactsDataSet) item);
                                    }
                                }
                                if (e.getButton() == MouseButton.SECONDARY) {
                                    cMenuContact.show(tc, e.getScreenX(), e.getScreenY());
                                }
                            });


                        }
                    }
                }
            };

        });

        contactTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        contactTree.setShowRoot(false);
        // refreshContactList(); нельзя так как еще не передан дбСервис
    }

    public void refreshContactList() {
        final TreeItem<Object> treeRoot = new TreeItem<>();
        try {
            for (GroupsDataSet group : dbService.getAllGroups()) {
                treeRoot.getChildren().add(new TreeItem<>(group));
            }
            for (TreeItem<Object> group : treeRoot.getChildren()) {
                for (ContactsDataSet contact : dbService.getContactsByGroup(((GroupsDataSet) group.getValue()).getId())) {
                    group.getChildren().add(new TreeItem<>(contact));
                }
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        contactTree.setRoot(treeRoot);
        treeRoot.getChildren().get(0).setExpanded(true);
    }

    public void showChatWindow(ContactsDataSet contact) {
        if (chatStage == null) {
            chatStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/fxml/chatform.fxml"));

            Parent fxmlChat = null;
            try {
                fxmlChat = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            chatControllerController = fxmlLoader.getController();
            chatControllerController.setChatStage(chatStage);
            chatControllerController.setDbService(dbService);

            chatStage.setScene(new Scene(fxmlChat, 520, 420));
            chatStage.setMinHeight(420);
            chatStage.setMinWidth(520);
            chatStage.setMaxWidth(520);
            chatStage.initStyle(StageStyle.UTILITY);
            chatStage.initModality(Modality.WINDOW_MODAL);
            chatStage.initOwner(mainStage);

        }
        chatControllerController.setContact(contact);
        chatControllerController.refreshMessages();
        chatStage.setTitle("Chat with " + contact.getName());
        chatStage.show();
    }

    public ContactsDataSet showInsUpdContactWindow(ContactsDataSet contact) {
        if (insUpdContactStage == null) {
            insUpdContactStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../view/fxml/updcontact.fxml"));

            Parent fxmlContact = null;
            try {
                fxmlContact = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            insUpdContactController = fxmlLoader.getController();
            insUpdContactController.setUpdContactStage(insUpdContactStage);
            insUpdContactController.setDbService(dbService);

            insUpdContactStage.setScene(new Scene(fxmlContact, 460, 234));
            insUpdContactStage.setResizable(false);
            insUpdContactStage.initStyle(StageStyle.UTILITY);
            insUpdContactStage.initModality(Modality.WINDOW_MODAL);
            insUpdContactStage.initOwner(mainStage);
        }

        insUpdContactController.setContact(contact);
        insUpdContactStage.showAndWait();

        return (insUpdContactController.isOkClicked()) ? insUpdContactController.getContact() : null;
    }

    public void btnAddGroupClick(ActionEvent actionEvent) {
        try {
            TextInputDialog textInput = new TextInputDialog("");
            textInput.setGraphic(null);
            textInput.setHeaderText(null);
            textInput.setTitle("New Group Name is");
            textInput.getDialogPane().setContentText("Input name here:");
            String newGroupName = textInput.showAndWait().get();

            if (newGroupName != null && !newGroupName.isEmpty()) {
                dbService.addGroup(newGroupName);
                refreshContactList();
            }
        } catch (NoSuchElementException | DBException e) {
            System.out.println("Can't rename group");
        }
    }

    public void btnRemoveMessagesClick(ActionEvent actionEvent) {
        Dialog<LocalDate> dateInput = new DateInputDialog(LocalDate.now());
        dateInput.setGraphic(null);
        dateInput.setHeaderText(null);
        dateInput.setTitle("Del messages from date");
        Date removeDate;
        LocalDate ld;


        try {
            try {
                ld = dateInput.showAndWait().get();
            } catch (Exception e) {
                ld = null;
            }
            if (ld != null) {
                removeDate = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
                dbService.removeMessagesByDate(removeDate.getTime());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(mainStage);
                alert.setTitle("Messages");
                alert.setHeaderText("Messages were removed");
                alert.setContentText("earlier then " + removeDate.toString());
                alert.showAndWait();
            }
        } catch (DBException e) {
            e.printStackTrace();
        }

    }

    public void btnSettingsClick(ActionEvent actionEvent) {
        //TODO settings
    }
}
