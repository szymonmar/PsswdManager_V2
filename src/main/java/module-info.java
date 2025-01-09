module com.example.psswd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.databind;
    requires org.xerial.sqlitejdbc;
    requires com.opencsv;
    exports com.example.psswd.Server;


    opens com.example.psswd.Client.model to javafx.base;
    opens com.example.psswd.Client to javafx.fxml;
    opens com.example.psswd.Client.views to javafx.fxml;
    exports com.example.psswd.Client;
    opens com.example.psswd to javafx.base;
}