package com.example.psswd.Client.views;

import com.example.psswd.Client.ConnectionHandler;
import com.example.psswd.Client.SceneController;
import com.example.psswd.Client.alert.AlertBuilder;
import com.example.psswd.CommPsswd;
import com.example.psswd.Request;
import com.opencsv.ICSVWriter;
import javafx.event.ActionEvent;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * FXML Controller for CSV import / export dialog
 */
public class CSVController {

    /**
     * Text field for path to the CSV file to import
     */
    @FXML
    public TextField importFilePathField;

    /**
     * Text field for the path to the directory for CSV export
     */
    @FXML
    public TextField exportFilePathField;

    /**
     * Reads data from CSV file and imports them to the database
     * @param actionEvent event that triggers the function
     */
    @FXML
    public void onImportClick(ActionEvent actionEvent) {
        String csvFile = importFilePathField.getText();
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] line;
            line = reader.readNext();
            int nameIdx = -1;
            int urlIdx = -1;
            int passwdIdx = -1;

            // walidacja formatu CSV
            if(line == null) {
                throw new CsvValidationException();
            }
            for (int i = 0; i < line.length; i++) {
                if(line[i].equalsIgnoreCase("name")) {
                    nameIdx = i;
                }
                if(line[i].equalsIgnoreCase("url")) {
                    urlIdx = i;
                }
                if(line[i].equalsIgnoreCase("password")) {
                    passwdIdx = i;
                }
            }

            if(nameIdx == -1 || urlIdx == -1 || passwdIdx == -1) {
                throw new CsvValidationException();
            }

            while ((line = reader.readNext()) != null) {
                CommPsswd commPsswd = new CommPsswd();
                commPsswd.setName(line[nameIdx]);
                commPsswd.setUrl(line[urlIdx]);
                commPsswd.setPassword(line[passwdIdx]);
                connectionHandlerInstance.sendObjectToServer(new Request("add"));
                connectionHandlerInstance.sendObjectToServer(commPsswd);
                Request reply = (Request) connectionHandlerInstance.readObjectFromServer();
                if(!reply.getRequest().equals("success")) {
                    AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
                    alertBuilder
                            .setTitle("Error")
                            .setHeaderText(reply.getRequest());
                    alertBuilder.getAlert().showAndWait();
                    break;
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Password data has been imported from CSV");
            alert.showAndWait();
        } catch (IOException | CsvValidationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("CSV read error");
            alert.showAndWait();
        }
    }

    /**
     * Exports all passwords to the csv file
     * @param actionEvent event that triggers the function
     */
    @FXML
    public void onExportClick(ActionEvent actionEvent) {
        String csvFile = exportFilePathField.getText() + "/passwdExport.csv";
        System.out.println(csvFile);

        try (CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(csvFile))
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER).build()) {

            // Nagłówki
            String[] header = {"name", "url", "password"};
            writer.writeNext(header);

            ArrayList<CommPsswd> passwds = PasswordsController.pass;
            // Dane
            for (CommPsswd password : passwds) {
                String[] row = {password.getName(), password.getUrl(), password.getPassword()};
                writer.writeNext(row);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Password data has been exported to CSV");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("CSV save error");
            alert.showAndWait();
        }
    }

    /**
     * Opens directory chooser window for export file directory
     * @param actionEvent event that triggers the function
     */
    @FXML
    public void chooseExportFile(ActionEvent actionEvent){
        // Tworzymy okno wyboru folderu
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose folder for export");

        // Opcjonalnie ustawiamy domyślny katalog
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Pobierz scenę z TextField
        Stage stage = (Stage) exportFilePathField.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            exportFilePathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Opens file chooser window to select the CSV file to import
     * @param actionEvent event that triggers the function
     */
    @FXML
    public void chooseImportFile(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to import");

        // Opcjonalne: Ustaw domyślny katalog
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Opcjonalne: Filtry plików
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        // Pobierz scenę z TextField
        Stage stage = (Stage) importFilePathField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            importFilePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Closes the window after clicking 'Cancel'
     * @param actionEvent event triggering the action
     */
    @FXML
    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }
}
