package com.example.psswd.alert;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

final public class AlertBuilder implements Alert {
    private final javafx.scene.control.Alert alert;

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

    @Override
    public Alert setException(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Exception stacktrace:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        return this;
    }

    public javafx.scene.control.Alert getAlert() {
        return alert;
    }
}
