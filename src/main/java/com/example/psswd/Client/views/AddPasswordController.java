package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.Client.generator.PasswordGenerator;
import com.example.psswd.CommPsswd;
import com.example.psswd.Request;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Random;


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
    private Slider slider;

    @FXML
    private Label numOfCharacters;

    @FXML
    private CheckBox capitals;

    @FXML
    private CheckBox numbers;

    @FXML
    private CheckBox symbols;

    @FXML
    private TextField generatedPassword;

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
        String name = nameField.getText();
        String pass = passwordField.getText();
        if(name.isEmpty()) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.WARNING);
            alertBuilder
                    .setTitle("Warning")
                    .setHeaderText("Name field is empty!");
            alertBuilder.getAlert().showAndWait();
            return;
        }
        if(pass.isEmpty()) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.WARNING);
            alertBuilder
                    .setTitle("Warning")
                    .setHeaderText("Password field is empty!");
            alertBuilder.getAlert().showAndWait();
            return;
        }
        CommPsswd addedPsswd = new CommPsswd();
        addedPsswd.setName(name);
        addedPsswd.setUrl(urlField.getText());
        addedPsswd.setPassword(pass);

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
        passwordStrengthBarController(newValue);
    }

    public void onGenerateClick(ActionEvent actionEvent) {
        boolean hasCapitals = capitals.isSelected();
        boolean hasNumbers = numbers.isSelected();
        boolean hasSymbols = symbols.isSelected();
        double numOfChars = slider.getValue();

        String pass = PasswordGenerator.generatePassword(
                hasCapitals, hasNumbers, hasSymbols, numOfChars);

        generatedPassword.setText(pass);
    }

    public void onUseClick(ActionEvent actionEvent) {
        String password = generatedPassword.getText();
        if(password.isEmpty()) {
            return;
        }
        passwordField.setText(password);
    }


    public void onDictClick(ActionEvent actionEvent) {
        String pass = passwordField.getText();
        if(pass.isEmpty()) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.WARNING);
            alertBuilder
                    .setTitle("Test failed")
                    .setHeaderText("Password field is empty!");
            alertBuilder.getAlert().showAndWait();
            return;
        }
        CommPsswd checkPsswd = new CommPsswd();
        checkPsswd.setPassword(pass);

        // pobranie instancji połączenia
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
        // wysłanie request i hasła do testu
        connectionHandlerInstance.sendObjectToServer(new Request("dictionary"));
        connectionHandlerInstance.sendObjectToServer(checkPsswd);

        Request reply = (Request) connectionHandlerInstance.readObjectFromServer();

        if(reply.getRequest().equals("success")) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.INFORMATION);
            alertBuilder
                    .setTitle("Test passed")
                    .setHeaderText("Your password passed the dictionary attack test.");
            alertBuilder.getAlert().showAndWait();
        } else if(reply.getRequest().equals("fail")) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.WARNING);
            alertBuilder
                    .setTitle("Test failed")
                    .setHeaderText("Your password did not pass the dictionary attack test.");
            alertBuilder.getAlert().showAndWait();
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText(reply.getRequest());
            alertBuilder.getAlert().showAndWait();
        }
    }

    public void passwordStrengthBarController(String password) {
        double passStrength = 0.0;
        double lengthFactor = 1.0;
        double charFactor = 0.0;
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChars = password.matches(".*[#$&@!+=?].*");

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
}
