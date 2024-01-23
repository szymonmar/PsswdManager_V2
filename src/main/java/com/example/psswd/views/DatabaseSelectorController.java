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

/**
 * Klasa obsługująca wybór bazy danych z hasłami (konta użytkownika) z GUI
 * Ekran domowy
 */
public class DatabaseSelectorController implements Initializable {

    /**
     * Tablica z dostępnymi bazami danych
     */
    @FXML
    private TableView<DatabaseRecord> databaseTable;

    /**
     * Kolumna tabeli ze ścieżką do pliku bazy danych
     */
    @FXML
    private TableColumn<DatabaseRecord, String> pathCol;

    /**
     * Kolumna tabeli z nazwą bazy danych
     */
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

    /**
     * Pobiera pamiętane lokalne bazy danych z configu
     */
    private void update() {
        databaseTable.getItems().setAll(config.getDatabases());
    }

    /**
     * Funkcja do otwierania okna tworzenia nowej bazy danych po kliknięciu przycisku "CREATE"
     * @param actionEvent event wywołujący funkcję (kliknięcie CREATE) [ActionEvent]
     */
    @FXML
    private void onCreateDatabaseClick(ActionEvent actionEvent) {
        try {
            SceneController.setScene(actionEvent, "new-database-window.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Funkcja do usuwania bazy danych z zapamiętanych po kliknięciu przycisku "REMOVE DATABASE"
     */
    @FXML
    private void onRemoveDatabaseClick() {
        try {
            // Usuwa bazę danych z pamiętanych w configu
            config.removeDatabase(
                    // Sprawdza która z zapamiętanych baz jest zaznaczona
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

    /**
     * Funkcja do otwierania bazy danych  po kliknięciu przycisku "OPEN DATABASE"
     * @param actionEvent event wywołujący funkcję (kliknięcie OPEN DATABASE) [ActionEvent]
     */
    public void onOpenDatabaseClick(ActionEvent actionEvent) {
        // Sprawdza która z zapamiętanych baz jest zaznaczona
        DatabaseRecord databaseRecord = databaseTable.getSelectionModel().getSelectedItem();
        try {
            openLocalDatabase(databaseRecord, actionEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SimpleStringProperty unlockPassword = new SimpleStringProperty();

    /**
     * Funkcja do wyświetlania okna podania hasła do bazy danych
     */
    private void showPasswordDialog() throws IOException {
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

    /**
     * Funkcja otwierająca bazę danych po kliknięciu "OPEN DATABASE"
     * @param databaseRecord baza danych do otworzenia [DatabaseRecord]
     * @param actionEvent event wywołujący funkcję (kliknięcie OPEN DATABASE) [ActionEvent]
     */
    private void openLocalDatabase(DatabaseRecord databaseRecord, ActionEvent actionEvent) {
        try {
            // wyświetla okno podania hasła do bazy danych
            showPasswordDialog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
        try {
            // tworzy połączenie pomiędzy kontraktem i bazą danych
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
            // pobiera informacje z kontraktu o bazie danych
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
            // ustawia zmienne potrzebne do odkodowania bazy danych
            cryptoController.setSalt(info.getSalt());
            cryptoController.initializeKey(unlockPassword.get());
            // sprawdza czy podano dobre hasło do bazy danych
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
        // jeśli hasło jest niepoprawne
        if(!isValidPassword) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Wrong password")
                    .setHeaderText("Wrong password, try again.");
            alertBuilder.getAlert().showAndWait();
            return;
        }
        try {
            // jeśli hasło jest poprawne wyświetla bazę danych
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