package com.example.psswd.Client.views;

import com.example.psswd.Client.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;

public class DictionaryAttackDialog {

    @FXML
    private ProgressBar progressBar;

    public void onCancelClick(ActionEvent actionEvent) {
        // todo send cancel to server
        SceneController.destroyStage(actionEvent);
    }

}
