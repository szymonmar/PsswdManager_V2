package com.example.psswd.Client;

import com.example.psswd.Client.alert.AlertBuilder;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/** Class handling server connection */
public class ConnectionHandler {

    /** Connection handler */
    private static ConnectionHandler connectionHandlerInstance;
    /** Output stream for sending objects to server ( client -> server ) */
    private static ObjectOutputStream objectOutput;
    /** Input stream for reading objects from server ( server -> client ) */
    private static ObjectInputStream objectInput;

    private  ConnectionHandler(){}

    /** Returns connection handler instance */
    public static ConnectionHandler getInstance() {
        if(connectionHandlerInstance == null) {
            connectionHandlerInstance = new ConnectionHandler();
        }
        return connectionHandlerInstance;
    }

    /** Establishes connection between client and server */
    public void establishConnection() {
        try {
            Socket socket = new Socket("localhost", 2137);
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectInput = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            AlertBuilder alertBuilder = new AlertBuilder(Alert.AlertType.ERROR);
            alertBuilder
                    .setTitle("Error")
                    .setHeaderText("Server connection error");
            alertBuilder.getAlert().showAndWait();
            System.exit(0);
        }
    }

    /** Sends object to the server via objectOutput
     * @param object object to be sent
     */
    public void sendObjectToServer(Object object) {
        try {
            objectOutput.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Receives object from the server via objectInput
     * @return received object
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

    /** Closes connection to server - used on logout */
    public void closeConnection() {
        objectInput = null;
        objectOutput = null;
        connectionHandlerInstance = null;
    }
}
