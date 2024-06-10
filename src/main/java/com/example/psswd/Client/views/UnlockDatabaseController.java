package com.example.psswd.Client.views;

import com.example.psswd.Client.SceneController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Klasa obsługująca okno podania hasła do bazy danych
 */
public class UnlockDatabaseController {

    /**
     * Pole tekstowe do wpisania hasła
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Pole tekstowe do wpisania loginu
     */
    @FXML
    private TextField loginField;
    /**
     * Zmienna przechowująca wpisane hasło
     */
    private SimpleStringProperty unlockPassword;

    /**
     * Zmienna przechowująca wpisany login
     */
    private SimpleStringProperty unlockLogin;

    /**
     * Funkcja ustawia zmienną unlockPassword na wartość podaną w argumencie
     * @param unlockPassword hasło do ustawienia [SimpleStringProperty]
     */
    public void setUnlockPassword(SimpleStringProperty unlockPassword, SimpleStringProperty unlockLogin) {
        this.unlockPassword = unlockPassword;
        this.unlockLogin = unlockLogin;
    }

    /**
     * Funkcja do zamykania okna wprowadzania hasła po kliknięciu przycisku "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }

    /**
     * Funkcja ustawiająca zmienną przechowującą hasło na wpisaną wartość po kliknięciu "UNLOCK"
     * @param actionEvent event wywołujący funkcję (kliknięcie UNLOCK) [ActionEvent]
     */
    @FXML
    private void onUnlockClick(ActionEvent actionEvent) {
        unlockPassword.set(passwordField.getText());
        unlockLogin.set(loginField.getText());
        SceneController.destroyStage(actionEvent);
    }



}
