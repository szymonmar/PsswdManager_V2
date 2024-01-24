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
     * Instancja SqliteDataSourceDAOFactory
     */
    private final SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();

    /**
     * Instancja CryptoController
     */
    private final CryptoController cryptoController = CryptoController.getInstance();

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
        Password password = new Password();
        password.setName(nameField.getText());
        password.setUrl(urlField.getText());
        try {
            // Próba zakodowania hasła i zapisania go w obiekcie password
            password.setPassword(cryptoController.encrypt(passwordField.getText()));
        } catch (Exception exception) {
            // Wyświetlenie błędu w razie niepowodzenia
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
            // Próba stworzenia DAO w celu dodania wpisu do bazy danych
            sqliteDataSourceDAOFactory.getPasswordsDao().insertPassword(password);
        } catch (Exception exception) {
            // Wyświetlenie błędu w razie niepowodzenia
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
