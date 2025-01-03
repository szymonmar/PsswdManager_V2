package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
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
 * Klasa obsługująca edycję konta użytkownika z GUI
 */
public class EditAccountController {


    /**
     * Pole tekstowe do wpisania starego hasła do konta
     */
    @FXML
    private PasswordField oldPasswordField;

    /**
     * Pole tekstowe do wpisania nowego hasła do konta
     */
    @FXML
    private PasswordField newPasswordField;


    /**
     * Pole tekstowe do ponownego wpisania nowego hasła do konta
     */
    @FXML
    private PasswordField repNewPasswordField;

    @FXML
    private PasswordField confirmPasswordField;


    /**
     * Checkbox potwierdzający, że użytkownik zna konsekwencje usunięcia konta
     */
    @FXML
    private CheckBox understandCheckBox;

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

    private boolean accDeleted = false;

    public boolean isAccDeleted() {
        return accDeleted;
    }

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
     * Funkcja do zapisania zmian we wpisie bazy danych po kliknięciu przycisku "SAVE"
     * @param actionEvent event wywołujący funkcję (kliknięcie SAVE) [ActionEvent]
     */
    public void onSaveClick(ActionEvent actionEvent) {
        if(this.newPasswordField.getText().equals(this.repNewPasswordField.getText())) {
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
     * Funkcja obsługująca kliknięcie "Delete Account" po stronie klienta
     * @param actionEvent event wywołujący funkcję (kliknięcie Delete Account) [ActionEvent]
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
                            .setHeaderText("Fatal error")
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
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("You need to confirm that You understand\nfurther consequences of this " +
                            "action before proceeding");
            alertBuilder.getAlert().showAndWait();
        }
    }



    public void onPasswordFieldChange(String newValue) {
        String password = newPasswordField.getText();
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

    public void onGenerateClick(ActionEvent actionEvent) {
        boolean hasCapitals = capitals.isSelected();
        boolean hasNumbers = numbers.isSelected();
        boolean hasSymbols = symbols.isSelected();
        double numOfChars = slider.getValue();

        // Pule znaków do haseł
        String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String specialCharacters = "!@#$%^&*()-_=+[]{}|;:,.<>?/";

        // Pula znaków do użycia w generatorze
        StringBuilder characterPool = new StringBuilder(lowercaseLetters);
        if (hasCapitals) {
            characterPool.append(uppercaseLetters);
        }
        if (hasNumbers) {
            characterPool.append(digits);
        }
        if (hasSymbols) {
            characterPool.append(specialCharacters);
        }

        // Sprawdzenie, czy w puli są znaki
        if (characterPool.isEmpty()) {
            throw new IllegalStateException("Character pool is empty. Check the input parameters.");
        }

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < numOfChars; i++) {
            int randomIndex = random.nextInt(characterPool.length());
            password.append(characterPool.charAt(randomIndex));
        }

        generatedPassword.setText(password.toString());
    }

    public void onUseClick(ActionEvent actionEvent) {
        String password = generatedPassword.getText();
        if(password.isEmpty()) {
            return;
        }
        newPasswordField.setText(password);
        repNewPasswordField.setText(password);
    }

    /**
     * Funkcja do zamykania okna dodawania hasła po kliknięciu przycisku "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }

    /**
     * Funkcja do wyświetlania okna testu ataku słownikowego
     */
    private void showDictionaryDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("dict-attack-dialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 380, 210);
        Stage stage = new Stage();
        stage.setTitle("Dictionary Attack Test");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }


    public void onDictClick(ActionEvent actionEvent) {
        CommPsswd checkPsswd = new CommPsswd();
        checkPsswd.setPassword(newPasswordField.getText());

        // pobranie instancji połączenia
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
        // wysłanie request i hasła do testu
        connectionHandlerInstance.sendObjectToServer(new Request("dictionary"));
        connectionHandlerInstance.sendObjectToServer(checkPsswd);
        try {
            // wyświetla okno testu ataku słownikowego
            showDictionaryDialog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Request reply = (Request) connectionHandlerInstance.readObjectFromServer();

        if(reply.getRequest().equals("success")) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.INFORMATION);
            alertBuilder
                    .setTitle("Test passed")
                    .setHeaderText("Your password passed the dictionary attack test.");
            alertBuilder.getAlert().showAndWait();
            SceneController.destroyStage(actionEvent);
            return;
        } else if(reply.getRequest().equals("fail")) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.WARNING);
            alertBuilder
                    .setTitle("Test failed")
                    .setHeaderText("Your password did not pass the dictionary attack test.");
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

}
