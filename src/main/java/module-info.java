module com.example.psswd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.databind;
    requires org.xerial.sqlitejdbc;
    exports com.example.psswd.config to com.fasterxml.jackson.databind;
    opens com.example.psswd.config to com.fasterxml.jackson.databind;
    opens com.example.psswd.model to javafx.base;
    opens com.example.psswd to javafx.fxml;
    opens com.example.psswd.views to javafx.fxml;
    opens com.example.psswd.crypto to javafx.fxml;
    exports com.example.psswd;
}