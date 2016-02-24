package ua.mytreo.java.jwpopup;

import ua.mytreo.java.jwpopup.dbservice.DBException;
import ua.mytreo.java.jwpopup.dbservice.DBService;
import ua.mytreo.java.jwpopup.dbservice.dataSets.ContactsDataSet;
import ua.mytreo.java.jwpopup.dbservice.dataSets.GroupsDataSet;
import ua.mytreo.java.jwpopup.dbservice.dataSets.MessagesDataSet;
import ua.mytreo.java.jwpopup.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ua.mytreo.java.jwpopup.sys.mailslot.MailSlotReceiver;
import ua.mytreo.java.jwpopup.sys.tray.TrayHelper;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * Main Application class
 *
 * @author mytreo
 * @version 1.0
 */
public class App extends javafx.application.Application {

    public static String getComputerName() {
        String computerName = null;
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return computerName;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        //db
        DBService dbService = new DBService();
        dbService.initBase();

        //mailslot
        MailSlotReceiver msr = new MailSlotReceiver();
        msr.start();

        //jaxaFx stage
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/fxml/mainform.fxml"));

        Parent fxmlMain = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        mainController.setMainStage(primaryStage);
        mainController.setDBService(dbService);
        mainController.refreshContactList();


        primaryStage.setTitle("BWPopUp2.0");
        primaryStage.setScene(new Scene(fxmlMain, 190, 320));
        primaryStage.setMaxWidth(190);
        primaryStage.setMinHeight(160);
        primaryStage.initStyle(StageStyle.UTILITY);

       // primaryStage.getIcons().add(new Image("file:resources/images/appicon.png"));
        primaryStage.show();

        new TrayHelper(primaryStage);//.changeIcon(true);

    }


    public static void main(String[] args) {

        //dbTest();
        launch(args);
    }


    public static void dbTest() {
        DBService dbService = new DBService();
        dbService.printConnectInfo();
        try {
            dbService.cleanUpBase();
            dbService.initBase();
            dbService.addGroup("Friends");
            dbService.addGroup("Workers");
            int i = 0;
            for (GroupsDataSet group : dbService.getAllGroups()) {
                dbService.addContact(new ContactsDataSet(0, "User" + ++i, "OISOU-561" + i, "127.0.0." + i, group.getId(), "avatar.ico"));
                dbService.addContact(new ContactsDataSet(0, "User" + ++i, "OISOU-561" + i, "127.0.0." + i, group.getId(), "avatar.ico"));
                dbService.addContact(new ContactsDataSet(0, "User" + ++i, "OISOU-561" + i, "127.0.0." + i, group.getId(), "avatar.ico"));
                dbService.addContact(new ContactsDataSet(0, "User" + ++i, "OISOU-561" + i, "127.0.0." + i, group.getId(), "avatar.ico"));
            }
            for (GroupsDataSet group : dbService.getAllGroups()) {
                for (ContactsDataSet contact : dbService.getContactsByGroup(group.getId())) {
                    dbService.addMessage(new MessagesDataSet(0, "textjgvjhvbhlkbklb", contact.getId(), (++i % 2 == 0) ? 0 : 1, (i % 5 == 0) ? 0 : 1, (new Date()).getTime()));
                    dbService.addMessage(new MessagesDataSet(0, "textjgvjhvbhlkbklb", contact.getId(), (++i % 2 == 0) ? 0 : 1, (i % 5 == 0) ? 0 : 1, (new Date()).getTime()));
                    dbService.addMessage(new MessagesDataSet(0, "textjgvjhvbhlkbklb", contact.getId(), (++i % 2 == 0) ? 0 : 1, (i % 5 == 0) ? 0 : 1, (new Date()).getTime()));
                    dbService.addMessage(new MessagesDataSet(0, "textjgvjhvbhlkbklb", contact.getId(), (++i % 2 == 0) ? 0 : 1, (i % 5 == 0) ? 0 : 1, (new Date()).getTime()));
                    dbService.addMessage(new MessagesDataSet(0, "textjgvjhvbhlkbklb", contact.getId(), (++i % 2 == 0) ? 0 : 1, (i % 5 == 0) ? 0 : 1, (new Date()).getTime()));
                }
            }


        } catch (DBException e) {
            e.printStackTrace();
        }

    }


}
