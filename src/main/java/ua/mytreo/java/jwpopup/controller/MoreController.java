package ua.mytreo.java.jwpopup.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author mytreo
 * @version 1.0
 * 16.02.2016.
 */
public class MoreController implements Initializable {
    @FXML
    private TextArea moreArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void printMoreAreaText(String message){
        moreArea.setText(message);
    }
}
