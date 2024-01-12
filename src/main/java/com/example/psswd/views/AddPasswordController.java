package com.example.psswd.views;

import com.example.psswd.SceneController;
import com.example.psswd.alert.AlertBuilder;
import com.example.psswd.crypto.CryptoController;
import com.example.psswd.dao.sqlite.SqliteDataSourceDAOFactory;
import com.example.psswd.model.Password;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Arrays;

public class AddPasswordController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField urlField;
    @FXML
    private PasswordField passwordField;

    private final SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
    private final CryptoController cryptoController = CryptoController.getInstance();

    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }
    public void onAddClick(ActionEvent actionEvent) {
        Password password = new Password();
        password.setName(nameField.getText());
        password.setUrl(urlField.getText());
        try {
            password.setPassword(cryptoController.encrypt(passwordField.getText()));
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Could not encrypt password.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
            return;
        }
        System.out.println(Arrays.toString(password.getPassword()));
        try {
            sqliteDataSourceDAOFactory.getPasswordsDao().insertPassword(password);
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Could not add password to the database.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
            return;
        }

        SceneController.destroyStage(actionEvent);
    }



}
