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
import javafx.scene.control.Alert;

import java.io.FileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVController {


    // todo add file path field
    public void onImportClick(ActionEvent actionEvent) {
        String csvFile = System.getProperty("user.dir") + "/dict/output.csv";
        ConnectionHandler connectionHandlerInstance = ConnectionHandler.getInstance();

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] line;
            line = reader.readNext();
            int nameIdx, urlIdx, passwdIdx;

            // walidacja formatu CSV
            if(line == null) {
                throw new CsvValidationException();
            }
            if(line[0].equalsIgnoreCase("name")) {
                nameIdx = 0;
            }
            else if(line[1].equalsIgnoreCase("name")) {
                nameIdx = 1;
            }
            else if(line[2].equalsIgnoreCase("name")) {
                nameIdx = 2;
            } else {
                throw new CsvValidationException();
            }

            if(line[0].equalsIgnoreCase("url")) {
                urlIdx = 0;
            }
            else if(line[1].equalsIgnoreCase("url")) {
                urlIdx = 1;
            }
            else if(line[2].equalsIgnoreCase("url")) {
                urlIdx = 2;
            } else {
                throw new CsvValidationException();
            }

            if(line[0].equalsIgnoreCase("password")) {
                passwdIdx = 0;
            }
            else if(line[1].equalsIgnoreCase("password")) {
                passwdIdx = 1;
            }
            else if(line[2].equalsIgnoreCase("password")) {
                passwdIdx = 2;
            } else {
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

    // todo add file path field
    public void onExportClick(ActionEvent actionEvent) {
        String csvFile = System.getProperty("user.dir") + "/dict/output.csv";

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

    public void onCancelClick(ActionEvent actionEvent) {
        SceneController.destroyStage(actionEvent);
    }
}
