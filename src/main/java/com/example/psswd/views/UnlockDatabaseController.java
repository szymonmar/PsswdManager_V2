package com.example.psswd.views;

import com.example.psswd.SceneController;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class UnlockDatabaseController {
    @FXML
    private PasswordField passwordField;
    private SimpleStringProperty unlockPassword;

    public void setUnlockPassword(SimpleStringProperty unlockPassword) {
        this.unlockPassword = unlockPassword;
    }

    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }

    @FXML
    private void onUnlockClick(ActionEvent actionEvent) {
        unlockPassword.set(passwordField.getText());
        SceneController.destroyStage(actionEvent);
    }



}
