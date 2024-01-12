package com.example.psswd.views;

import com.example.psswd.alert.AlertBuilder;
import com.example.psswd.config.Config;
import com.example.psswd.crypto.CryptoController;
import com.example.psswd.dao.InfoDao;
import com.example.psswd.dao.sqlite.SqliteDataSourceDAOFactory;
import com.example.psswd.SceneController;
import com.example.psswd.model.Info;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.Connection;

public class NewDatabaseController {
    @FXML
    private TextField dbNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordRepeatField;

    public void onCancelClick(ActionEvent actionEvent) {
        try {
            SceneController.setScene(actionEvent, "database-selector-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onCreateClick(ActionEvent actionEvent) {
        String dbName = dbNameField.getText();
        String passwd = passwordField.getText();
        String passwdRepeat = passwordRepeatField.getText();

        if(dbName.isBlank()) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setHeaderText("Error")
                    .setHeaderText("Empty database name");
            alertBuilder.getAlert().showAndWait();
            return;
        }

        if(!passwd.equals(passwdRepeat)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Passwords are not identical.");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(dbName);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Passwd databases (*.pass)", "*.pass");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(SceneController.getStage(actionEvent));

        SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
        try {
            sqliteDataSourceDAOFactory.establishConnection(file.getAbsolutePath());
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Could not create the database.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
            return;
        }

        Info info = new Info();
        info.setName(dbName);
        CryptoController cryptoController = CryptoController.getInstance();
        cryptoController.setDatabaseName(dbName);
        try {
            cryptoController.initializeKey(passwd);
            info.setChallenge(cryptoController.encrypt(dbName));
            info.setSalt(cryptoController.getSalt());

            sqliteDataSourceDAOFactory.getInfoDao().insertInfo(info);
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Failed to initialize key from password.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
            return;
        }
        try {
            Config config = Config.getInstance();
            config.addDatabase(dbName, file.getAbsolutePath());
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Failed to add a database to the config file.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
            return;
        }
        try {
            SceneController.setScene(actionEvent, "passwords-view.fxml");
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Fatal error.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
        }
    }
}
