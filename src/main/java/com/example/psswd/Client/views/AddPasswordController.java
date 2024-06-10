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

        SceneController.destroyStage(actionEvent);
    }



}
