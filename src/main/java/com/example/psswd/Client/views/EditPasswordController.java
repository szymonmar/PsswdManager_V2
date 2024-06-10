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
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

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
        urlField.setText(passwordUrl.get());
        nameField.setText(passwordName.get());
        passwordField.setText(passwordText.get());
    }

    /**
     * Pole tekstowe do wpisania hasła
     */
    @FXML
    private TextField passwordField;
    @FXML


    /**
     * Pole tekstowe linku, który będzie korespondował z zapisanym hasłem, np. facebook.com
     */
    private TextField urlField;
    @FXML

    /**
     * Pole tekstowy nazwy, pod którą chcemy zapisać hasło, np. Facebook
     */
    private TextField nameField;

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
            SceneController.destroyStage(actionEvent);
            return;
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error from server")
                    .setHeaderText(reply.getRequest())
                    .setException(null);
            alertBuilder.getAlert().showAndWait();
        }
    }

    /**
     * Funkcja do zamykania okna dodawania hasła po kliknięciu przycisku "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }


}
