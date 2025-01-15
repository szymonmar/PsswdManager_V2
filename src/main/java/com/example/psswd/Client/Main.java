package com.example.psswd.Client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    /**
     * Starts the JavaFX application
     * @param stage
     * @throws IOException if problem occurs while opening fxml file
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 400);
        stage.setTitle("Passwd Manager");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
    * Main program input
    */
    public static void main(String[] args) {
        launch();
    }
}
