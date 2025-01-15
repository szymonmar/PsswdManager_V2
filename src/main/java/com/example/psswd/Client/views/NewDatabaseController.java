package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.Client.generator.PasswordGenerator;
import com.example.psswd.CommPsswd;
import com.example.psswd.LoginCredentials;
import com.example.psswd.Request;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;


/**
 * FXML controller for 'Create new user' screen
 */
public class NewDatabaseController {

    /**
     * Text field for database name (username)
     */
    @FXML
    private TextField dbNameField;

    /**
     * Text field for password
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Text field for repeat password
     */
    @FXML
    private PasswordField passwordRepeatField;

    /**
     * Password strength progress bar
     */
    @FXML
    private ProgressBar progressBar;

    /**
     * Password length slider for password generator
     */
    @FXML
    private Slider slider;

    /**
     * Label displaying number of characters selected with the slider
     */
    @FXML
    private Label numOfCharacters;

    /**
     * 'Capital letters' checkbox for generator
     */
    @FXML
    private CheckBox capitals;

    /**
     * 'Numbers' checkbox for generator
     */
    @FXML
    private CheckBox numbers;

    /**
     * 'Special symbols' checkbox for generator
     */
    @FXML
    private CheckBox symbols;

    /**
     * Text field displaying generated password
     */
    @FXML
    private TextField generatedPassword;


    /**
    * Runs once on opening the window
    */
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
     * Closes the window after clicking 'Cancel'
     * @param actionEvent event triggering the action
     */
    public void onCancelClick(ActionEvent actionEvent) {
        try {
            SceneController.setScene(actionEvent, "hello-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends new user request to the server and passes login data for the new user
     * @param actionEvent event triggering the action
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

        if(passwd.isEmpty() || passwdRepeat.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Password field is empty.");
            alert.showAndWait();
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
        connectionHandlerInstance.establishConnection();
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
                        .setHeaderText("Fatal error.");
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

    /**
     * Function triggered by the listener on 'Password' field
     * @param newValue password in the 'Password' field
     */
    public void onPasswordFieldChange(String newValue) {
        passwordStrengthBarController(newValue);
    }

    /**
     * Runs password generator algorithm after clicking 'Generate password'
     * @param actionEvent event triggering the action
     */
    public void onGenerateClick(ActionEvent actionEvent) {
        boolean hasCapitals = capitals.isSelected();
        boolean hasNumbers = numbers.isSelected();
        boolean hasSymbols = symbols.isSelected();
        double numOfChars = slider.getValue();

        String pass = PasswordGenerator.generatePassword(
                hasCapitals, hasNumbers, hasSymbols, numOfChars);

        generatedPassword.setText(pass);
    }

    /**
     * Passes generated password to 'Password' and 'Repeat password' fields
     * @param actionEvent event triggering the action
     */
    public void onUseClick(ActionEvent actionEvent) {
        String password = generatedPassword.getText();
        if(password.isEmpty()) {
            return;
        }
        passwordField.setText(password);
        passwordRepeatField.setText(password);
    }

    /**
     * Handles dictionary attack check
     * @param actionEvent event triggering the dictionary attack check
     */
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

    /**
     * Handles 'Password strength' bar
     * @param password password in the 'Password' field
     */
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
