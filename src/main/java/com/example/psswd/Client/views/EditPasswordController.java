package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.CommPsswd;
import com.example.psswd.Client.model.Password;
import com.example.psswd.Request;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

/**
 * Klasa obsługująca edycję wpisu w bazie danych z GUI
 */
public class EditPasswordController implements Initializable {
    /**
     * parametr z nazwą hasła
     */
    private StringProperty passwordName = new SimpleStringProperty();
    /**
     * parametr z url hasła
     */
    private StringProperty passwordUrl = new SimpleStringProperty();
    /**
     * parametr z hasłem
     */
    private StringProperty passwordText = new SimpleStringProperty();
    /**
     * parametr z id hasła
     */
    private IntegerProperty passwordId = new SimpleIntegerProperty();

    @FXML
    private ProgressBar progressBar;

    /**
     * Konstruktor kontrolera widoku edycji hasła
     * @param passwordId id hasła które będzie edytowane
     * @param password obiekt typu Password który będzie edytowany
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
     * Pole tekstowe do wpisania hasła
     */
    @FXML
    private TextField passwordField;



    /**
     * Pole tekstowe linku, który będzie korespondował z zapisanym hasłem, np. facebook.com
     */
    @FXML
    private TextField urlField;


    /**
     * Pole tekstowy nazwy, pod którą chcemy zapisać hasło, np. Facebook
     */
    @FXML
    private TextField nameField;

    @FXML
    private Slider slider;

    @FXML
    private Label numOfCharacters;

    /**
     * Funkcja do zapisania zmian we wpisie bazy danych po kliknięciu przycisku "SAVE"
     * @param actionEvent event wywołujący funkcję (kliknięcie SAVE) [ActionEvent]
     */
    public void onSaveClick(ActionEvent actionEvent) {
        // pobranie danych z frontendu
        CommPsswd commPsswd = new CommPsswd();
        commPsswd.setId(this.passwordId.get());
        commPsswd.setName(nameField.getText());
        commPsswd.setUrl(urlField.getText());
        commPsswd.setPassword(passwordField.getText());

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

    public void onPasswordFieldChange(String newValue) {
        String password = newValue;
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

    // todo
    public void onUseClick(ActionEvent actionEvent) {

    }

    /**
     * Funkcja do zamykania okna dodawania hasła po kliknięciu przycisku "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }


}
