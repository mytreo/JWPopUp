package ua.mytreo.java.jwpopup.sys.dialog;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.time.LocalDate;


/**
 *  @author mytreo
 * @version 1.0
 * 15.02.2016.
 */
public class DateInputDialog extends Dialog<LocalDate> {
    private final GridPane grid;
    private final DatePicker datePicker;
    private final LocalDate defaultValue;

    public DateInputDialog() {
        this(LocalDate.now());
    }

    public DateInputDialog(@NamedArg("defaultValue") LocalDate defaultValue) {
        final DialogPane dialogPane = getDialogPane();

        // -- datePicker
        this.datePicker = new DatePicker();
        this.datePicker.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(datePicker, Priority.ALWAYS);
        GridPane.setFillWidth(datePicker, true);

        this.defaultValue = defaultValue;
        this.datePicker.setValue(defaultValue);

        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? datePicker.getValue() : null;
        });
    }

    private void updateGrid() {
        grid.getChildren().clear();
        grid.add(datePicker, 1, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> datePicker.requestFocus());
    }
}
