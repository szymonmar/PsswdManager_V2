package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.Client.generator.PasswordGenerator;
import com.example.psswd.CommPsswd;
import com.example.psswd.Client.model.Password;
import com.example.psswd.Request;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * FXML controller for 'Edit password' window
 */
public class EditPasswordController implements Initializable {

    /**
     * Holds name of the selected password entity
     */
    private StringProperty passwordName = new SimpleStringProperty();

    /**
     * Holds url of the selected password entity
     */
    private StringProperty passwordUrl = new SimpleStringProperty();

    /**
     * Holds password of the selected password entity
     */
    private StringProperty passwordText = new SimpleStringProperty();

    /**
     * Holds id of the selected password entity
     */
    private IntegerProperty passwordId = new SimpleIntegerProperty();

    /**
     * Password strength progress bar
     */
    @FXML
    private ProgressBar progressBar;

    /**
     * Password text field
     */
    @FXML
    private TextField passwordField;

    /**
     * URL text field
     */
    @FXML
    private TextField urlField;

    /**
     * Name text field
     */
    @FXML
    private TextField nameField;

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
     * Creates new EditPasswordController with properties from selected password entity
     * @param passwordId id of the selected password entity
     * @param password Password object associated with te entity to be edited
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public EditPasswordController(int passwordId, Password password) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.passwordId.set(passwordId);
        this.passwordName.set(password.getName());
        this.passwordUrl.set(password.getUrl());
        this.passwordText.set(password.getDecryptedPassword());
    }

    /**
     * Runs once when Edit Password window is opened,
     * sets up the controller
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        onPasswordFieldChange(passwordText.get());
        urlField.setText(passwordUrl.get());
        nameField.setText(passwordName.get());
        passwordField.setText(passwordText.get());
        // Dodaj listener do textProperty
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            onPasswordFieldChange(newValue);
        });

        // Dodaj listener do slidera
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            numOfCharacters.setText(String.valueOf(newValue.intValue()));
        });
    }


    /**
     * Sends request to change password entity data and passes new data to the server
     * @param actionEvent event that triggers the function
     */
    public void onSaveClick(ActionEvent actionEvent) {
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

        // pobranie danych z frontendu
        CommPsswd commPsswd = new CommPsswd();
        commPsswd.setId(this.passwordId.get());
        commPsswd.setName(name);
        commPsswd.setUrl(urlField.getText());
        commPsswd.setPassword(pass);

        // pobranie instancji połączenia, wysłanie request i danych hasła
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
        connectionHandlerInstance.sendObjectToServer(new Request("edit"));
        connectionHandlerInstance.sendObjectToServer(commPsswd);
        Request reply = (Request) connectionHandlerInstance.readObjectFromServer();
        if(reply.getRequest().equals("success")) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.INFORMATION);
            alertBuilder
                    .setTitle("Information")
                    .setHeaderText("Your changes have been saved");
            alertBuilder.getAlert().showAndWait();
            SceneController.destroyStage(actionEvent);
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
    }

    /**
     * Closes the window after clicking 'Cancel'
     * @param actionEvent event triggering the action
     */
    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
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
