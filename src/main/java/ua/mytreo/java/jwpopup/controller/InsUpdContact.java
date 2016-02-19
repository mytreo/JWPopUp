package ua.mytreo.java.jwpopup.controller;

import ua.mytreo.java.jwpopup.dbservice.DBService;
import ua.mytreo.java.jwpopup.dbservice.DBException;
import ua.mytreo.java.jwpopup.dbservice.dataSets.ContactsDataSet;
import ua.mytreo.java.jwpopup.dbservice.dataSets.GroupsDataSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.StringConverter;


import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author mytreo
 * @version 1.0
 * 15.02.2016.
 */
public class InsUpdContact implements Initializable {

    private Stage insUpdContactStage;
    private ContactsDataSet contact;
    private DBService dbService;
    private boolean okClicked = false;

    private static Image[] images = new Image[5];
    @FXML
    private Pagination pagination;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAdress;
    @FXML
    private TextField txtIp;
    @FXML
    private ComboBox<GroupsDataSet> cbxGroup;

    public void setDbService(DBService dbService) {
        this.dbService = dbService;
        try {
            cbxGroup.getItems().addAll(dbService.getAllGroups());
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public void setUpdContactStage(Stage updContactStage) {
        this.insUpdContactStage = updContactStage;
    }

    public ContactsDataSet getContact() {
        return contact;
    }

    public void setContact(ContactsDataSet contact) {
        this.contact = contact;

        txtName.setText(contact.getName());
        txtAdress.setText(contact.getNamePC());
        txtIp.setText(contact.getIP());

        //0=insert else upd
        if (contact.getId() == 0) {
            insUpdContactStage.setTitle("Insert Contact ");
            txtAdress.setEditable(true);
            txtIp.setEditable(true);
            cbxGroup.getSelectionModel().selectFirst();
        } else {
            insUpdContactStage.setTitle("Update Contact " + contact.getName());
            txtAdress.setEditable(false);
            txtIp.setEditable(false);
            try {
                cbxGroup.getSelectionModel().select(dbService.getGroup(contact.getGroup_id()));
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
        okClicked=false;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // images[0] = new Image(getClass().getResource("/ensemble/samples/shared-resources/Animal1.jpg").toExternalForm(), false);
        images[0] = new Image("file:resources/avatar/ic_account_box_black_48dp.png");
        images[1] = new Image("file:resources/avatar/ic_account_circle_black_48dp.png");
        images[2] = new Image("file:resources/avatar/ic_android_black_48dp.png");
        images[3] = new Image("file:resources/avatar/ic_assignment_ind_black_48dp.png");
        images[4] = new Image("file:resources/avatar/ic_face_black_48dp.png");


        cbxGroup.setConverter(new StringConverter<GroupsDataSet>() {
            @Override
            public String toString(GroupsDataSet group) {
                return group.getGroup_name();
            }

            @Override
            public GroupsDataSet fromString(String groupName) {
                GroupsDataSet gds = null;
                try {
                    for (GroupsDataSet group : dbService.getAllGroups()) {
                        if (group.getGroup_name().equals(groupName)) {
                            gds = group;
                        }
                    }
                } catch (DBException e) {
                    e.printStackTrace();
                }
                return gds;
            }
        });


        pagination.setPageFactory((Integer pageIndex) -> createAnimalPage(pageIndex));
        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        pagination.setMaxPageIndicatorCount(images.length);
        pagination.setPageCount(images.length);
    }

    private ImageView createAnimalPage(int pageIndex) {
        return  new ImageView(images[pageIndex]);
    }

    public boolean isOkClicked() {
        return okClicked;
    }


    public void btnOkClick(ActionEvent actionEvent) {
        okClicked = true;
        contact.setName(txtName.getText());
        contact.setNamePC(txtAdress.getText());
        contact.setIP(txtIp.getText());
        contact.setGroup_id(cbxGroup.getSelectionModel().getSelectedItem().getId());
        contact.setAvatar(images[pagination.getCurrentPageIndex()].toString());
        insUpdContactStage.close();
    }

    public void btnCancelClick(ActionEvent actionEvent) {
        contact=null;
        okClicked=false;
        insUpdContactStage.close();
    }
}
