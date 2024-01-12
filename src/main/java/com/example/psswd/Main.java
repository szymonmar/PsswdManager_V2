package com.example.psswd;

import com.example.psswd.config.Config;
import com.example.psswd.config.DatabaseRecord;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("database-selector-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 700, 400);
        stage.setTitle("Passwd Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}