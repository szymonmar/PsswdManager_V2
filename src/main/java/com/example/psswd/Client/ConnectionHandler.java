package com.example.psswd.Client;

import com.example.psswd.Client.alert.AlertBuilder;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/** klasa obsługująca połączenie z serwerem */
public class ConnectionHandler {

    /** instancja */
    private static ConnectionHandler connectionHandlerInstance;
    /** strumień wyjścia z klienta do serwera */
    private static ObjectOutputStream objectOutput;
    /** strumień wejścia z serwera do klienta */
    private static ObjectInputStream objectInput;

    private  ConnectionHandler(){}

    /** zwraca instancję connectionHandlera */
    public static ConnectionHandler getInstance() {
        if(connectionHandlerInstance == null) {
            connectionHandlerInstance = new ConnectionHandler();
        }
        return connectionHandlerInstance;
    }

    /** funkcja do tworzenia połączenia z serwerem */
    public void establishConnection() {
        try {
            Socket socket = new Socket("localhost", 2137);
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectInput = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Server connection error")
                    .setException(e);
            alertBuilder.getAlert().showAndWait();
            System.exit(0);
        }
    }

    /** funkcja wysyłająca zserializowany obiekt na serwer
     * @param object - obiekt do wysłania
     */
    public void sendObjectToServer(Object object) {
        try {
            objectOutput.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** funkcja odczytująca zserializowany obiekt wysłany przez serwer
     * @return przysłany obiekt
     */
    public Object readObjectFromServer() {
        Object object;
        try {
            object = objectInput.readObject();
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
