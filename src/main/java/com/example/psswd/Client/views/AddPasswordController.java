package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.CommPsswd;
import com.example.psswd.Request;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;


/**
 * Klasa obsługująca dodawanie hasła do konkretnej bazy danych z GUI
 */
public class AddPasswordController {

    /**
     * Pole tekstowy nazwy, pod którą chcemy zapisać hasło, np. Facebook
     */
    @FXML
    private TextField nameField;

    /**
     * Pole tekstowe linku, który będzie korespondował z zapisanym hasłem, np. facebook.com
     */
    @FXML
    private TextField urlField;

    /**
     * Pole tekstowe do wpisania hasła
     */
    @FXML
    private PasswordField passwordField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void initialize() {
        // Dodaj listener do textProperty
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            onPasswordFieldChange(newValue);
        });
    }


    /**
     * Funkcja do zamykania okna dodawania hasła po kliknięciu przycisku "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }

    /**
     * Funkcja do dodawania wpisu do bazy danych po kliknięciu przycisku "ADD"
     * @param actionEvent event wywołujący funkcję (kliknięcie ADD) [ActionEvent]
     */
    public void onAddClick(ActionEvent actionEvent) {
        CommPsswd addedPsswd = new CommPsswd();
        addedPsswd.setName(nameField.getText());
        addedPsswd.setUrl(urlField.getText());
        addedPsswd.setPassword(passwordField.getText());

        // pobranie instancji połączenia
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
        // wysłanie request
        connectionHandlerInstance.sendObjectToServer(new Request("add"));
        // wysłanie CommPsswd zawierającego dane dodanego hasła
        connectionHandlerInstance.sendObjectToServer(addedPsswd);
        Request reply = (Request) connectionHandlerInstance.readObjectFromServer();

        if(reply.getRequest().equals("success")) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.INFORMATION);
            alertBuilder
                    .setTitle("Information")
                    .setHeaderText("Your password has been added successfully");
            alertBuilder.getAlert().showAndWait();
            SceneController.destroyStage(actionEvent);
            return;
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText(reply.getRequest());
            alertBuilder.getAlert().showAndWait();
        }

        SceneController.destroyStage(actionEvent);
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
