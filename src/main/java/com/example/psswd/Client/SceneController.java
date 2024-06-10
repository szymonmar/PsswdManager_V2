package com.example.psswd.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Klasa obsługująca okno programu
 */
public class SceneController {

    /**
     * Ustawia określoną scenę w oknie, w którym wystąpił event
     * @param event akcja wykonana przez użytkownika [ActionEvent]
     * @param resource nazwa pliku fxml do ustawienia jako scena [String]
     * @throws NullPointerException jeśli resource == null
     * @throws IOException jeśli wystąpił błąd w strumieniu wejścia / wyjścia (funkcja load())
     */
    public static void setScene(ActionEvent event, String resource) throws NullPointerException, IOException {
        URL url = SceneController.class.getResource(resource);
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        Stage stage = getStage(event);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Pobiera i zwraca okno, w którym będziemy rysować scenę
     * @param event event pochodzący z okna, którego zawartość będziemy modyfikować [ActionEvent]
     * @return okno, którego zawartość będziemy modyfikować
     */
    public static Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    /**
     * Funkcja zamykająca okno
     * @param event event wywołujący funkcję [ActionEvent]
     */
    public static void destroyStage(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
