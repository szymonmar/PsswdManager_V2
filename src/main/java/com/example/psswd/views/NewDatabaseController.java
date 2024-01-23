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

/**
 * Klasa obsługująca dodanie nowej bazy danych (nowy użytkownik) z GUI
 */
public class NewDatabaseController {

    /**
     * Pole tekstowe nazwy bazy danych (nazwa użytkownika)
     */
    @FXML
    private TextField dbNameField;

    /**
     * Pole tekstowe hasła do bazy danych (hasła użytkownika)
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Pole tekstowe ponownego podania hasła
     */
    @FXML
    private PasswordField passwordRepeatField;

    /**
     * Funkcja do przełączania okna z tworzenia użytkownika na wybór już istniejącego po naciśnięciu "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    public void onCancelClick(ActionEvent actionEvent) {
        try {
            SceneController.setScene(actionEvent, "database-selector-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Funkcja do tworzenia nowej bazy danych (nowego użytkownika) po kliknięciu "CREATE"
     * @param actionEvent event wywołujący funkcję (kliknięcie CREATE) [ActionEvent]
     */
    public void onCreateClick(ActionEvent actionEvent) {
        String dbName = dbNameField.getText();
        String passwd = passwordField.getText();
        String passwdRepeat = passwordRepeatField.getText();

        // Sprawdza czy nazwa bazy danych (nazwa użytkownika) została podana
        if(dbName.isBlank()) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setHeaderText("Error")
                    .setHeaderText("Empty database name");
            alertBuilder.getAlert().showAndWait();
            return;
        }

        // Sprawdza czy hasło jest powtórzone dwa razy
        if(!passwd.equals(passwdRepeat)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Passwords are not identical.");
            alert.showAndWait();
            return;
        }

        // Tworzenie nowego pliku z bazą danych dla użytkownika
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(dbName);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Passwd databases (*.pass)", "*.pass");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(SceneController.getStage(actionEvent));

        SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
        try {
            // Tworzy połączenie pomiędzy kontraktem a plikiem z bazą danych
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
        info.setName(dbName); // Zapis nazwy bazy danych do obiektu z informacjami o bazie
        CryptoController cryptoController = CryptoController.getInstance();
        cryptoController.setDatabaseName(dbName);
        try {
            cryptoController.initializeKey(passwd); // Tworzymy klucz na podstawie hasła
            info.setChallenge(cryptoController.encrypt(dbName)); // Szyfrujemy nazwę bazy danych, aby stworzych "challenge" i zapisujemy do obiektu z informacjami o bazie
            info.setSalt(cryptoController.getSalt()); //Tworzymy i zapisujemy salt do obiektu z informacjami o bazie

            sqliteDataSourceDAOFactory.getInfoDao().insertInfo(info); // Zapisujemy informacje do kontraktu
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
            config.addDatabase(dbName, file.getAbsolutePath()); // Zapisujemy nazwę i ścieżkę do bazy danych do configu
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
            SceneController.setScene(actionEvent, "passwords-view.fxml"); // Otwieramy okno z danymi w bazie danych
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
