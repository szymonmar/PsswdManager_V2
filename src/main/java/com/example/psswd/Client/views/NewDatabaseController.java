package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.LoginCredentials;
import com.example.psswd.Request;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;


/**
 * Klasa obsługująca dodanie nowej bazy danych (nowy użytkownik) z GUI
 */
public class NewDatabaseController {

    /**
     * Pole tekstowe nazwy bazy danych (nazwa użytkownika)
     */
    @FXML
    private TextField dbNameField;

    /**
     * Pole tekstowe hasła do bazy danych (hasła użytkownika)
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Pole tekstowe ponownego podania hasła
     */
    @FXML
    private PasswordField passwordRepeatField;

    /**
     * Funkcja do przełączania okna z tworzenia użytkownika na wybór już istniejącego po naciśnięciu "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    public void onCancelClick(ActionEvent actionEvent) {
        try {
            SceneController.setScene(actionEvent, "database-selector-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Funkcja do tworzenia nowej bazy danych (nowego użytkownika) po kliknięciu "CREATE"
     * @param actionEvent event wywołujący funkcję (kliknięcie CREATE) [ActionEvent]
     */
    public void onCreateClick(ActionEvent actionEvent) {
        String dbName = dbNameField.getText();
        String passwd = passwordField.getText();
        String passwdRepeat = passwordRepeatField.getText();

        // Sprawdza czy nazwa bazy danych (nazwa użytkownika) została podana
        if(dbName.isBlank()) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setHeaderText("Error")
                    .setHeaderText("Empty username");
            alertBuilder.getAlert().showAndWait();
            return;
        }

        // Sprawdza czy hasło jest powtórzone dwa razy
        if(!passwd.equals(passwdRepeat)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Passwords are not identical.");
            alert.showAndWait();
            return;
        }

        // pobranie instancji połączenia, przesłanie request i danych logowania
        LoginCredentials loginCredentials = new LoginCredentials(dbName, passwd);
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
        connectionHandlerInstance.sendObjectToServer(new Request("newuser"));
        connectionHandlerInstance.sendObjectToServer(loginCredentials);
        Request reply = (Request) connectionHandlerInstance.readObjectFromServer();

        if(reply.getRequest().equals("success")) {
            try {
                SceneController.setScene(actionEvent, "passwords-view.fxml"); // Otwieramy okno z danymi w bazie danych
            } catch (Exception exception) {
                AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
                alertBuilder
                        .setTitle("Error")
                        .setHeaderText("Fatal error.")
                        .setException(exception);
                alertBuilder.getAlert().showAndWait();
            }
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText(reply.getRequest())
                    .setException(null);
            alertBuilder.getAlert().showAndWait();
        }
    }
}
