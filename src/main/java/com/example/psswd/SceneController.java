package com.example.psswd;

import com.example.psswd.config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Objects;

public class SceneController {

    public static void setScene(ActionEvent event, String resource) throws NullPointerException, IOException {
        URL url = SceneController.class.getResource(resource);
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        Stage stage = getStage(event);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    public static void destroyStage(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
