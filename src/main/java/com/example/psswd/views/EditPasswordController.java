package com.example.psswd.views;

import com.example.psswd.SceneController;
import com.example.psswd.alert.AlertBuilder;
import com.example.psswd.crypto.CryptoController;
import com.example.psswd.dao.sqlite.SqliteDataSourceDAOFactory;
import com.example.psswd.model.Password;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
import java.util.function.IntPredicate;

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
        this.passwordText.set(cryptoController.decrypt(password.getPassword()));
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
    @FXML

    /**
     * Instancja SqliteDataSourceDAOFactory
     */
    private final SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();

    /**
     * Instancja CryptoController
     */
    private final CryptoController cryptoController = CryptoController.getInstance();

    /**
     * Funkcja do zapisania zmian we wpisie bazy danych po kliknięciu przycisku "SAVE"
     * @param actionEvent event wywołujący funkcję (kliknięcie SAVE) [ActionEvent]
     */
    public void onSaveClick(ActionEvent actionEvent) {
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
        try {
            // Próba stworzenia DAO w celu dodania wpisu do bazy danych
            sqliteDataSourceDAOFactory.getPasswordsDao().updatePassword(passwordId.get(), password);
        } catch (Exception exception) {
            // Wyświetlenie błędu w razie niepowodzenia
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Could not add password to the database.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
        } finally {
            SceneController.destroyStage(actionEvent);
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
