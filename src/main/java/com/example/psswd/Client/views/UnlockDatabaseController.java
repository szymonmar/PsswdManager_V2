package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.LoginCredentials;
import com.example.psswd.Request;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Klasa obsługująca okno podania hasła do bazy danych
 */
public class UnlockDatabaseController {

    /**
     * Text field for password
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Text field for username
     */
    @FXML
    private TextField loginField;

    /**
     * Holds password entered in passwordField
     */
    private SimpleStringProperty unlockPassword;

    /**
     * Holds username entered in loginField
     */
    private SimpleStringProperty unlockLogin;

    /**
     * True if logged in, false if not
     */
    private boolean loggedIn = false;

    /**
     * Setter for login credentials
     * @param unlockPassword password
     * @param unlockLogin username
     */
    public void setUnlockPassword(SimpleStringProperty unlockPassword, SimpleStringProperty unlockLogin) {
        this.unlockPassword = unlockPassword;
        this.unlockLogin = unlockLogin;
    }

    /**
     * Closes the window after clicking 'Cancel'
     * @param actionEvent event triggering the action
     */
    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }

    /**
     * Sends log in request and log in data to the server,
     * sets loggedIn to true if log in successful, false if not
     * @param actionEvent event triggering the function
     */
    @FXML
    private void onUnlockClick(ActionEvent actionEvent) {
        unlockPassword.set(passwordField.getText());
        unlockLogin.set(loginField.getText());


        LoginCredentials loginCredentials = new LoginCredentials(unlockLogin.get(), unlockPassword.get());
        // pobranie instancji połączenia
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
        connectionHandlerInstance.establishConnection();
        // przesłanie request i danych logowania
        connectionHandlerInstance.sendObjectToServer(new Request("login"));
        connectionHandlerInstance.sendObjectToServer(loginCredentials);

        Request reply = (Request) connectionHandlerInstance.readObjectFromServer();

        // jeśli zalogowano to otwiera managera
        if(reply.getRequest().equals("success")) {
            loggedIn = true;
        } else {
            loggedIn = true;
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText(reply.getRequest());
            alertBuilder.getAlert().showAndWait();
            return;
        }


        SceneController.destroyStage(actionEvent);
    }

    /**
     * Getter for isLoggedIn
     * @return true if user is logged in, false if not
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }
}
