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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
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


    /**
     * Checkbox potwierdzający, że użytkownik zna konsekwencje usunięcia konta
     */
    @FXML
    private CheckBox understandCheckBox;

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
            connectionHandlerInstance.sendObjectToServer(new Request("deleteaccount"));         // todo po stronie serwera
            Request reply = (Request) connectionHandlerInstance.readObjectFromServer();
            if(reply.getRequest().equals("success")) {
                AlertBuilder infobox = new AlertBuilder(Alert.AlertType.INFORMATION);
                infobox
                        .setTitle("Information")
                        .setHeaderText("Your account has been removed.");
                infobox.getAlert().showAndWait();
                try {
                    SceneController.setScene(actionEvent, "database-selector-view.fxml");
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
                        .setHeaderText("Error occured on the server side");
                alertBuilder.getAlert().showAndWait();
            }
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("You need to confirm that You understand further consequences of this " +
                            "action before proceeding");
            alertBuilder.getAlert().showAndWait();
        }


    }

    public void onGenerateClick(ActionEvent actionEvent) {

    }

    /**
     * Funkcja do zamykania okna dodawania hasła po kliknięciu przycisku "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }


}
