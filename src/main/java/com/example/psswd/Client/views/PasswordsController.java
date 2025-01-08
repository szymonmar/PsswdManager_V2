package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.CommPsswd;
import com.example.psswd.Client.model.Converters;
import com.example.psswd.Client.model.Password;
import com.example.psswd.Request;
import com.example.psswd.Server.dao.sqlite.SqliteDataSourceDAOFactory;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Klasa obsługująca wyświetlanie bazy danych z hasłami w GUI
 * Ekran główny użytkownika
 */
public class PasswordsController implements Initializable {

    /**
     * Pole tekstowe wyszukiwania
     */
    @FXML
    private TextField searchField;

    /**
     * Górny toolbar
     */
    @FXML
    private HBox hboxSearchToolbar;
    @FXML
    private HBox hboxToolbar;

    /**
     * Kolumna tabeli z nazwami serwisów we wpisach
     */
    @FXML
    private TableColumn<Password, String> nameCol;

    /**
     * Kolumna tabeli z url do serwisu we wpisach
     */
    @FXML
    private TableColumn<Password, String> urlCol;

    /**
     * Kolumna tabeli z hasłami
     */
    @FXML
    private TableColumn<Password, String> passwordCol;

    /**
     * Tabela z wpisami
     */
    @FXML
    private TableView<Password> passwordsTable;
    private ObservableList<Password> passwords = FXCollections.observableArrayList();

    public static ArrayList<CommPsswd> pass;


    /**
     * Funkcja wyświetlająca GUI i zawartość bazy danych w tabeli
     * @param url parametry użyte aby implementacja interfejsu działała
     * @param rb parametry użyte aby implementacja interfejsu działała
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HBox.setHgrow(hboxSearchToolbar, Priority.ALWAYS);

        // ustawianie nagłówków dla kolumn
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));

        Callback<TableColumn<Password, String>, TableCell<Password, String>> cellFactory
                =
                new Callback<TableColumn<Password, String>, TableCell<Password, String>>() {
                    @Override
                    public TableCell<Password,String> call(final TableColumn<Password, String> param) {

                        // obsługa funkcji pokaż / ukryj hasło
                        final TableCell<Password, String> cell = new TableCell<Password, String>() {
                            final Button btn = new Button("Show");
                            final TextField passwordField = new TextField();
                            final HBox hbox = new HBox(2);

                            /**
                             * Funkcja odświeżająca pole hasła w zależności od wybranego trybu (SHOW / HIDE)
                             * @param item wartość, którą będziemy zastępować dany element (hasło lub ciąg kropek) [String]
                             * @param empty czy pole hasło jest puste [Boolean]
                             */
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                /**
                                 * ciąg znaków zastępujący hasło, aby je zamaskować
                                 */
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
                                                passwordField.setText(password.getDecryptedPassword());
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
        ConnectionHandler connectionHandler = ConnectionHandler.getInstance();
        pass = (ArrayList<CommPsswd>) connectionHandler.readObjectFromServer();
        passwords = FXCollections.observableArrayList(Converters.convertToObservable(pass));

        // obsługa wyszukiwania
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

        // obsługa sortowania
        SortedList<Password> sortedPasswords = new SortedList<>(filteredPasswords);
        sortedPasswords.comparatorProperty().bind(passwordsTable.comparatorProperty());

        passwordsTable.setItems(sortedPasswords);

    }

    /**
     * Funkcja odświeżająca dane pobrane z bazy danych haseł
     */
    private void update() {
        ConnectionHandler connectionHandler = ConnectionHandler.getInstance();
        connectionHandler.sendObjectToServer(new Request("update"));
        pass = (ArrayList<CommPsswd>) connectionHandler.readObjectFromServer();
        passwords.setAll(FXCollections.observableArrayList(Converters.convertToObservable(pass)));
    }

    /**
     * Funkcja do edytowania pojedynczego wpisu bazy danych po kliknięciu "EDIT"
     * @param actionEvent event wywołujący funkcję (kliknięcie EDIT) [ActionEvent]
     * @throws IOException jeśli wystąpi błąd strumienia wejścia / wyjścia
     */
    @FXML
    public void onEditClick(ActionEvent actionEvent) {
        if(passwordsTable.getSelectionModel().isEmpty()) {
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("edit-password-dialog.fxml"));
            EditPasswordController controller = new EditPasswordController(
                    passwordsTable.getSelectionModel().getSelectedItem().getId(),
                    passwordsTable.getSelectionModel().getSelectedItem()
            );
            fxmlLoader.setController(controller);
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 760, 360);
            Stage stage = new Stage();
            stage.setTitle("Edit password");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception exception) {
        AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
        alertBuilder
                .setTitle("Error")
                .setHeaderText("Failed to edit password.")
                .setException(exception);
        alertBuilder.getAlert().showAndWait();
        } finally {
            update();
        }
    }

    /**
     * Funkcja do usuwania wpisu z bazy danych po kliknięciu przycisku "DELETE"
     * @param actionEvent event wywołujący funkcję (kliknięcie DELETE) [ActionEvent]
     */
    @FXML
    public void onDeleteClick(ActionEvent actionEvent) {
        if(passwordsTable.getSelectionModel().isEmpty()) {
            return;
        }
        AlertBuilder confirm = new AlertBuilder(Alert.AlertType.CONFIRMATION);
        confirm
                .setTitle("Confirm")
                .setHeaderText("Are You sure You want to delete this password?\nThis action cannot be reversed");
        // Wyświetlenie alertu i oczekiwanie na odpowiedź użytkownika
        Optional<ButtonType> result = confirm.getAlert().showAndWait();
        // Sprawdzenie wyboru użytkownika
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // pobranie instancji połączenia, przesłanie request i danych hasła do usunięcia
            ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();
            connectionHandlerInstance.sendObjectToServer(new Request("delete"));
            connectionHandlerInstance.sendObjectToServer(Converters.convertToString(passwordsTable.getSelectionModel().getSelectedItem()));
            Request reply = (Request) connectionHandlerInstance.readObjectFromServer();
            if (reply.getRequest().equals("success")) {
                update();
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
                alertBuilder
                        .setTitle("Error")
                        .setHeaderText(reply.getRequest());
                alertBuilder.getAlert().showAndWait();
            }
        }
        update();
    }

    /**
     * Funkcja do otwierania okna tworzenia dodawania wpisu do bazy danych po kliknięciu przycisku "ADD"
     * @param actionEvent event wywołujący funkcję (kliknięcie ADD) [ActionEvent]
     * @throws IOException jeśli wystąpi błąd strumienia wejścia / wyjścia
     */
    @FXML
    public void onAddClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("add-password-dialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 760, 360);
        Stage stage = new Stage();
        stage.setTitle("Add password");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        update();
    }

    /**
     * Funkcja do zamknięcia bazy, wywołująca powrót do menu wyboru baz "BACK"
     * @param actionEvent event wywołujący funkcję (kliknięcie BACK) [ActionEvent]
     */
    @FXML
    public void onBackClick(ActionEvent actionEvent) {
        ConnectionHandler connectionHandler = ConnectionHandler.getInstance();
        connectionHandler.sendObjectToServer(new Request("logout"));
        Request reply = (Request) connectionHandler.readObjectFromServer();
        if(reply.getRequest().equals("success")) {
            try {
                SceneController.setScene(actionEvent, "database-selector-view.fxml");
            } catch (Exception exception) {
                AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
                alertBuilder
                        .setTitle("Error")
                        .setHeaderText("Fatal error.")
                        .setException(exception);
                alertBuilder.getAlert().showAndWait();
            }
        } else {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText(reply.getRequest());
            alertBuilder.getAlert().showAndWait();
        }
    }

    @FXML
    public void onAccountClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("edit-account-dialog.fxml"));
            EditAccountController controller = new EditAccountController();
            fxmlLoader.setController(controller);
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 770, 570);
            Stage stage = new Stage();
            stage.setTitle("Account settings");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            if(controller.isAccDeleted()) {
                SceneController.setScene(actionEvent, "database-selector-view.fxml");
                return;
            }
        } catch (Exception exception) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Failed to edit account.")
                    .setException(exception);
            alertBuilder.getAlert().showAndWait();
        }
    }

    /**
     * Funkcja do wyświetlania okna CSV
     */
    @FXML
    private void onCsvClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneController.class.getResource("csv-dialog.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 380, 240);
        Stage stage = new Stage();
        stage.setTitle("CSV");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        update();
    }
}
