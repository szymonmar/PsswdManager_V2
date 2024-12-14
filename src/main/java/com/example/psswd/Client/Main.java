package com.example.psswd.Client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    /**
     * Funkcja wykonująca start programu
     * @param stage
     * @throws IOException jeśli wystąpi błąd strumienia wejścia / wyjścia
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("database-selector-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 400);
        stage.setTitle("Passwd Manager");
        stage.setScene(scene);
        stage.show();
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
        connectionHandlerInstance.establishConnection();
    }

    /**
    * Główna funkcja programu
    * Wywołuje metodę launch(), która rozpoczyna cykl życia aplikacji JavaFX
    */
    public static void main(String[] args) {
        launch();
    }
}
