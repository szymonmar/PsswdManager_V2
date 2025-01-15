package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.Client.generator.PasswordGenerator;
import com.example.psswd.Client.model.Password;
import com.example.psswd.CommPsswd;
import com.example.psswd.LoginCredentials;
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
import javafx.scene.text.Text;
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
 * FXML controller for 'Edit account' window
 */
public class EditAccountController {


    /**
     * Text field for old password
     */
    @FXML
    private PasswordField oldPasswordField;

    /**
     * Text field for new password
     */
    @FXML
    private PasswordField newPasswordField;


    /**
     * Text field for repeating new password
     */
    @FXML
    private PasswordField repNewPasswordField;

    /**
     * Text field for password in the 'Delete account' section
     */
    @FXML
    private PasswordField confirmPasswordField;

    /**
     * Checkbox for user to confirm that they understand consequences
     * of further actions
     */
    @FXML
    private CheckBox understandCheckBox;

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
     * Holds information whether the account has been deleted in this window
     */
    private boolean accDeleted = false;

    /**
     * @return true if account was deleted in the window, false if not
     */
    public boolean isAccDeleted() {
        return accDeleted;
    }

    /**
     * Runs once on opening the window
     */
    @FXML
    public void initialize() {
        // Dodaj listener do textProperty
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            onPasswordFieldChange(newValue);
        });

        // Dodaj listener do slidera
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            numOfCharacters.setText(String.valueOf(newValue.intValue()));
        });
    }

    /**
     * Sends request to change user password and passes new password to the server
     * @param actionEvent event that triggers the function
     */
    public void onSaveClick(ActionEvent actionEvent) {
        String pass = newPasswordField.getText();
        String repPass = repNewPasswordField.getText();
        if(pass.isEmpty() || repPass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Password field is empty.");
            alert.showAndWait();
            return;
        }
        if(pass.equals(repPass)) {
            // pobranie danych z frontendu
            LoginCredentials loginCredentials = new LoginCredentials(this.oldPasswordField.getText());
            loginCredentials.setNoweHaslo(this.newPasswordField.getText());

            // pobranie instancji połączenia, wysłanie request i nowego hasła do sprawdzenia przez serwer
            ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
            connectionHandlerInstance.sendObjectToServer(new Request("editAcc"));
            connectionHandlerInstance.sendObjectToServer(loginCredentials);
            Request reply = (Request) connectionHandlerInstance.readObjectFromServer();
            if(reply.getRequest().equals("success")) {
                AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.INFORMATION);
                alertBuilder
                        .setTitle("Information")
                        .setHeaderText("Your password has been changed");
                alertBuilder.getAlert().showAndWait();
                SceneController.destroyStage(actionEvent);
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
                alertBuilder
                        .setTitle("Error")
                        .setHeaderText(reply.getRequest());
                alertBuilder.getAlert().showAndWait();
            }
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Passwords are not identical!");
            alertBuilder.getAlert().showAndWait();
        }


    }

    /**
     * Handles 'Delete account' on client site and sends request to delete user account
     * @param actionEvent event that triggers the function
     */
    public void onDeleteClick(ActionEvent actionEvent) {
        if(understandCheckBox.isSelected()) {
            // pobranie instancji połączenia, wysłanie request i danych hasła
            ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
            connectionHandlerInstance.sendObjectToServer(new Request("deleteAcc"));
            connectionHandlerInstance.sendObjectToServer(new LoginCredentials(confirmPasswordField.getText()));
            Request reply = (Request) connectionHandlerInstance.readObjectFromServer();
            if(reply.getRequest().equals("success")) {
                accDeleted = true;
                AlertBuilder infobox = new AlertBuilder(Alert.AlertType.INFORMATION);
                infobox
                        .setTitle("Information")
                        .setHeaderText("Your account has been removed.");
                infobox.getAlert().showAndWait();
                try {
                    SceneController.destroyStage(actionEvent);
                } catch (Exception exception) {
                    AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
                    alertBuilder
                            .setTitle("Error")
                            .setHeaderText("Fatal error");
                    alertBuilder.getAlert().showAndWait();
                }
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
                alertBuilder
                        .setTitle("Error")
                        .setHeaderText(reply.getRequest());
                alertBuilder.getAlert().showAndWait();
            }
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("You need to confirm that You understand\nfurther consequences of this " +
                            "action before proceeding");
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
        newPasswordField.setText(password);
        repNewPasswordField.setText(password);
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
        String pass = newPasswordField.getText();
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
