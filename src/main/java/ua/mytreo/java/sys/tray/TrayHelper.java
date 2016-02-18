package ua.mytreo.java.sys.tray;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * @author mytreo
 * @version 1.0
 * 11.02.2016.
 */
public class TrayHelper {
    private  TrayIcon trayIcon;

    public TrayHelper(Stage primaryStage) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            //нужный флаг чтобы можно было развернуть из трея
            Platform.setImplicitExit(false);
            Image image = null;
            try {
                image = Toolkit.getDefaultToolkit().getImage((URI.create("file:resources/system/trayicon.png")).toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            PopupMenu popup = new PopupMenu();
            MenuItem cmExit = new MenuItem("Exit");
            MenuItem cmShow = new MenuItem("Show");
            popup.add(cmExit);
            popup.add(cmShow);

            trayIcon = new TrayIcon(image, "BWPopup2.0", popup);
            trayIcon.setImageAutoSize(true);


            ActionListener listenerExit = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent arg0) {
                    System.exit(0);
                }
            };
            ActionListener listenerShow = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent arg0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            primaryStage.show();
                            primaryStage.toFront();
                        }
                    });
                }
            };

            ActionListener listenerTray = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent event) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!primaryStage.isShowing()) {
                                primaryStage.show();
                                primaryStage.toFront();
                            } else {
                                primaryStage.hide();
                            }
                        }
                    });
                }
            };

            trayIcon.addActionListener(listenerTray);
            cmExit.addActionListener(listenerExit);
            cmShow.addActionListener(listenerShow);

            try

            {
                tray.add(trayIcon);
                trayIcon.displayMessage("BWPopup2.0", "Application started!",
                        TrayIcon.MessageType.INFO);
                //по сути тултип и его вызов
            } catch (
                    Exception e
                    )

            {
                System.err.println("Can't add to tray");
            }
        } else {
            System.err.println("Tray unavailable");
        }
        //


    }

    public void changeIcon(boolean message){
        Image image = null;
        try {
            if(!message){
            image = Toolkit.getDefaultToolkit().getImage((URI.create("file:resources/system/trayicon.png")).toURL());}
            else{ image = Toolkit.getDefaultToolkit().getImage((URI.create("file:resources/system/trayiconM.png")).toURL());}
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        trayIcon.setImage(image);
    }

}
