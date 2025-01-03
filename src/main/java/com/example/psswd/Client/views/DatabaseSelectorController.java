package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.LoginCredentials;
import com.example.psswd.Request;
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
 * Klasa obsługująca wybór bazy danych z hasłami (konta użytkownika) z GUI
 * Ekran domowy
 */
public class DatabaseSelectorController implements Initializable {

    private boolean loggedIn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}


    /**
     * Funkcja do otwierania okna tworzenia nowej bazy danych po kliknięciu przycisku "CREATE"
     * @param actionEvent event wywołujący funkcję (kliknięcie CREATE) [ActionEvent]
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
     * Funkcja do otwierania bazy danych  po kliknięciu przycisku "OPEN DATABASE"
     * @param actionEvent event wywołujący funkcję (kliknięcie OPEN DATABASE) [ActionEvent]
     */
    public void onOpenDatabaseClick(ActionEvent actionEvent) {
        // Sprawdza która z zapamiętanych baz jest zaznaczona
        try {
            openLocalDatabase(actionEvent);
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Fatal error")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
        }
    }

    private SimpleStringProperty unlockPassword = new SimpleStringProperty();
    private SimpleStringProperty unlockLogin = new SimpleStringProperty();

    /**
     * Funkcja do wyświetlania okna podania hasła do bazy danych
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
        stage.showAndWait();

        loggedIn = unlockDatabaseController.isLoggedIn();
    }

    /**
     * Funkcja otiwerająca okno logowania
     * @param actionEvent event wywołujący funkcję (kliknięcie OPEN DATABASE) [ActionEvent]
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