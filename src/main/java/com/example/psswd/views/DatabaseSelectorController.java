package com.example.psswd.views;

import com.example.psswd.SceneController;
import com.example.psswd.alert.AlertBuilder;
import com.example.psswd.config.Config;
import com.example.psswd.config.DatabaseRecord;
import com.example.psswd.crypto.CryptoController;
import com.example.psswd.dao.sqlite.SqliteDataSourceDAOFactory;
import com.example.psswd.model.Info;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class DatabaseSelectorController implements Initializable {

    @FXML
    private TableView<DatabaseRecord> databaseTable;
    @FXML
    private TableColumn<DatabaseRecord, String> pathCol;
    @FXML
    private TableColumn<DatabaseRecord, String> nameCol;

    private Config config;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            config = Config.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        nameCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().name())
        );
        pathCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().path())
        );

        update();
    }
    private void update() {
        databaseTable.getItems().setAll(config.getDatabases());
    }

    @FXML
    private void onCreateDatabaseClick(ActionEvent actionEvent) {
        try {
            SceneController.setScene(actionEvent, "new-database-window.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void onRemoveDatabaseClick(ActionEvent actionEvent) {
        try {
            config.removeDatabase(
                    databaseTable.getSelectionModel().getSelectedItem()
            );
        } catch (Exception e) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Cannot delete the database.")
                    .setException(e);
            alertBuilder.getAlert().showAndWait();
        }
        update();
    }

    public void onOpenDatabaseClick(ActionEvent actionEvent) {
        DatabaseRecord databaseRecord = databaseTable.getSelectionModel().getSelectedItem();
        try {
            openLocalDatabase(databaseRecord, actionEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SimpleStringProperty unlockPassword = new SimpleStringProperty();
    private void showPasswordDialog(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("unlock-database-dialog.fxml"));
        Parent parent = fxmlLoader.load();
        UnlockDatabaseController unlockDatabaseController = fxmlLoader.<UnlockDatabaseController>getController();
        unlockDatabaseController.setUnlockPassword(unlockPassword);
        Scene scene = new Scene(parent, 380, 170);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void openLocalDatabase(DatabaseRecord databaseRecord, ActionEvent actionEvent) {
        try {
            showPasswordDialog(actionEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
        try {
            sqliteDataSourceDAOFactory.establishConnection(databaseRecord.path());
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Could not open the database.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
            return;
        }
        Info info;
        try {
            info = sqliteDataSourceDAOFactory.getInfoDao().getInfo();
            System.out.println(Arrays.toString(info.getChallenge()));

        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Could not open the database.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
            return;
        }
        CryptoController cryptoController = CryptoController.getInstance();
        cryptoController.setDatabaseName(databaseRecord.name());
        boolean isValidPassword;
        try {
            cryptoController.setSalt(info.getSalt());
            cryptoController.initializeKey(unlockPassword.get());
            isValidPassword = cryptoController.verify(info.getChallenge());
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Failed to initialize key from password.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
            return;
        }
        if(!isValidPassword) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Wrong password")
                    .setHeaderText("Wrong password, try again.");
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