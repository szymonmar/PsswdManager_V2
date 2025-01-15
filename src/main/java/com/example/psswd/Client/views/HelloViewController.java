package com.example.psswd.Client.views;

import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML controller for hello screen
 */
public class HelloViewController implements Initializable {

    /**
     * Holds information whether user is logged in or not
     */
    private boolean loggedIn;

    /**
     * Runs once when opening the window
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {}


    /**
     * Opens "Create new user" view
     * @param actionEvent event that triggers the function
     */
    @FXML
    private void onCreateDatabaseClick(ActionEvent actionEvent) {
        try {
            SceneController.setScene(actionEvent, "new-database-window.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles "Log in" click
     * @param actionEvent event that triggers the function
     */
    public void onOpenDatabaseClick(ActionEvent actionEvent) {
        // Sprawdza która z zapamiętanych baz jest zaznaczona
        try {
            openLocalDatabase(actionEvent);
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Fatal error");
            alertBuilder.getAlert().showAndWait();
        }
    }

    private SimpleStringProperty unlockPassword = new SimpleStringProperty();
    private SimpleStringProperty unlockLogin = new SimpleStringProperty();

    /**
     * Handles opening 'log in' dialog and attaching the controller to it
     * @throws IOException when cannot open the fxml file
     */
    private void showPasswordDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("unlock-database-dialog.fxml"));
        Parent parent = fxmlLoader.load();
        UnlockDatabaseController unlockDatabaseController = fxmlLoader.<UnlockDatabaseController>getController();
        unlockDatabaseController.setUnlockPassword(unlockPassword, unlockLogin);

        Scene scene = new Scene(parent, 380, 210);
        Stage stage = new Stage();
        stage.setTitle("Log in");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();

        loggedIn = unlockDatabaseController.isLoggedIn();
    }

    /**
     * Opens "Log in" dialog if logged out
     * @param actionEvent event that triggers the function
     */
    private void openLocalDatabase(ActionEvent actionEvent) {

        try {
            // wyświetla okno podania danych do logowania
            showPasswordDialog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(loggedIn) {
            try {
                SceneController.setScene(actionEvent, "passwords-view.fxml"); // Otwieramy okno z danymi w bazie danych
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }
}