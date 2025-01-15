package com.example.psswd.Client.alert;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class representing alert shown in the password manager
 */
final public class AlertBuilder implements Alert {

    /**
     * JavaFX alert
     */
    private final javafx.scene.control.Alert alert;

    /**
     * Creates new alert
     * @param alertType alert type (INFORMATION / WARNING / ERROR / CONFIRMATION)
     */
    public AlertBuilder(javafx.scene.control.Alert.AlertType alertType) {
        alert = new javafx.scene.control.Alert(alertType);
    }

    @Override
    public Alert setTitle(String title) {
        alert.setTitle(title);
        return this;
    }

    @Override
    public Alert setHeaderText(String headerText) {
        alert.setHeaderText(headerText);
        return this;
    }

    /**
     * Returns alert to be shown in the front-end (use this method in the controller)
     * @return Alert
     */
    public javafx.scene.control.Alert getAlert() {
        return alert;
    }
}
