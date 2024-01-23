package com.example.psswd.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Klasa obsługująca edycję wpisu w bazie danych z GUI
 */
public class EditPasswordController {

    /**
     * Pole tekstowe do wpisania hasła
     */
    @FXML
    private PasswordField passwordField;
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
     * Funkcja do zapisania zmian we wpisie bazy danych po kliknięciu przycisku "SAVE"
     * @param actionEvent event wywołujący funkcję (kliknięcie SAVE) [ActionEvent]
     */
    public void onSaveClick(ActionEvent actionEvent) {
    }

    /**
     * Funkcja do zamykania okna dodawania hasła po kliknięciu przycisku "CANCEL"
     * @param actionEvent event wywołujący funkcję (kliknięcie CANCEL) [ActionEvent]
     */
    public void onCancelClick(ActionEvent actionEvent) {
    }
}
