package com.example.psswd.views;
import com.example.psswd.SceneController;
import com.example.psswd.alert.AlertBuilder;
import com.example.psswd.crypto.CryptoController;
import com.example.psswd.dao.sqlite.SqliteDataSourceDAOFactory;
import com.example.psswd.model.Password;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordsController implements Initializable {
    @FXML
    private TextField searchField;
    @FXML
    private HBox hboxSearchToolbar;
    @FXML
    private HBox hboxToolbar;
    @FXML
    private TableColumn<Password, String> nameCol;
    @FXML
    private TableColumn<Password, String> urlCol;
    @FXML
    private TableColumn<Password, String> passwordCol;
    @FXML
    private TableView<Password> passwordsTable;
    private ObservableList<Password> passwords = FXCollections.observableArrayList();

    private static final SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory =
            SqliteDataSourceDAOFactory.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HBox.setHgrow(hboxSearchToolbar, Priority.ALWAYS);

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));

        Callback<TableColumn<Password, String>, TableCell<Password, String>> cellFactory
                =
                new Callback<TableColumn<Password, String>, TableCell<Password, String>>() {
                    @Override
                    public TableCell<Password,String> call(final TableColumn<Password, String> param) {
                        final TableCell<Password, String> cell = new TableCell<Password, String>() {
                            final Button btn = new Button("Show");
                            final TextField passwordField = new TextField();
                            final HBox hbox = new HBox(2);
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                String maskedPassword = "•".repeat(8);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    hbox.alignmentProperty().set(Pos.CENTER);
                                    passwordField.setText(maskedPassword);
                                    btn.setPrefWidth(50);
                                    btn.setOnAction(event -> {
                                        if(passwordField.getText().equals(maskedPassword)) {

                                            Password password = getTableView().getItems().get(getIndex());
                                            try {
                                                CryptoController cryptoController = CryptoController.getInstance();
                                                String passwordPlaintext = cryptoController.decrypt(password.getPassword());
                                                passwordField.setText(passwordPlaintext);
                                            } catch (Exception exception) {
                                                AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
                                                alertBuilder
                                                        .setTitle("Error")
                                                        .setHeaderText("Failed to decrypt password.")
                                                        .setException(exception);
                                                alertBuilder.getAlert().showAndWait();
                                                return;
                                            }
                                            btn.setText("Hide");
                                        } else {
                                            passwordField.setText(maskedPassword);
                                            btn.setText("Show");
                                        }

                                    });
                                    hbox.getChildren().setAll(passwordField, btn);

                                    setGraphic(hbox);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        passwordCol.setCellFactory(cellFactory);
        passwords = FXCollections.observableArrayList(sqliteDataSourceDAOFactory.getPasswordsDao().getPasswords());

        FilteredList<Password> filteredPasswords = new FilteredList<>(passwords, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPasswords.setPredicate(password -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (password.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (password.getUrl().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<Password> sortedPasswords = new SortedList<>(filteredPasswords);
        sortedPasswords.comparatorProperty().bind(passwordsTable.comparatorProperty());

        passwordsTable.setItems(sortedPasswords);

    }

    private void update() {
        passwords.setAll(sqliteDataSourceDAOFactory.getPasswordsDao().getPasswords());
    }

    public void onEditClick(ActionEvent actionEvent) throws IOException {
        if(passwordsTable.getSelectionModel().isEmpty()) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("edit-password-dialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 380, 300);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        update();
    }

    public void onDeleteClick(ActionEvent actionEvent) {
        if(passwordsTable.getSelectionModel().isEmpty()) {
            return;
        }
        try {
        SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
        sqliteDataSourceDAOFactory.getPasswordsDao().deletePassword(
                passwordsTable.getSelectionModel().getSelectedItem().getId()
        );
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Failed to delete password.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
        } finally {
            update();
        }
    }

    public void onAddClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("add-password-dialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 380, 300);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        update();
    }

}
