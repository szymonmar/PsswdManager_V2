package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.LoginCredentials;
import com.example.psswd.Request;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Slider slider;

    @FXML
    private Label numOfCharacters;

    @FXML
    public void initialize() {
        // Dodaj listener do passwordField
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            onPasswordFieldChange(newValue);
        });

        // Dodaj listener do slidera
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            numOfCharacters.setText(String.valueOf(newValue.intValue()));
        });
    }

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
                    .setHeaderText(reply.getRequest());
            alertBuilder.getAlert().showAndWait();
        }
    }

    public void onPasswordFieldChange(String newValue) {
        String password = passwordField.getText();
        double passStrength = 0.0;
        double lengthFactor = 1.0;
        double charFactor = 0.0;
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChars = password.matches(".*[^a-zA-Z0-9 ].*");

        if(password.isEmpty()) {
            progressBar.setProgress(0);
            progressBar.setStyle("-fx-accent: #FF0000;");
            return;
        }
        if(password.length() < 6) {
            charFactor = 0.1;
            lengthFactor = 0.5;
        } else {
            if(password.length() < 10){
                lengthFactor = 0.8;
            } else if(password.length() < 12){
                lengthFactor = 1;
            } else if(password.length() < 13){
                lengthFactor = 1.2;
            } else if(password.length() < 14){
                lengthFactor = 1.4;
            } else if(password.length() < 15){
                lengthFactor = 1.6;
            } else if(password.length() < 16){
                lengthFactor = 1.8;
            } else {
                lengthFactor = 2.0;
            }

            if(hasLowerCase) {
                charFactor += 0.125;
            }
            if(hasUpperCase) {
                charFactor += 0.125;
            }
            if(hasDigit) {
                charFactor += 0.125;
            }
            if(hasSpecialChars) {
                charFactor += 0.125;
            }
        }

        passStrength = charFactor * lengthFactor;
        progressBar.setProgress(passStrength);

        if(passStrength < 0.5) {
            progressBar.setStyle("-fx-accent: #FF0000;");
        } else if (passStrength < 0.8) {
            progressBar.setStyle("-fx-accent: #FFAA00;");
        } else {
            progressBar.setStyle("-fx-accent: #4CAF50;");
        }
    }

    // todo
    public void onGenerateClick(ActionEvent actionEvent) {

    }
}
