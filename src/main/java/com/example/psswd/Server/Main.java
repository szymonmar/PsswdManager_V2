package com.example.psswd.Server;

import com.example.psswd.Server.dictTester.DictionaryDAO;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        // Sprawdzamy, czy katalogi istnieją
        File directory = new File(System.getProperty("user.dir") + "/databases/");
        if (!directory.exists()) {
            // Próba utworzenia katalogu
            if (!directory.mkdirs()) {
                System.out.println("Nie udało sie utworzyć katalogu głównego!");
                return;
            }
        }
        directory = new File(System.getProperty("user.dir") + "/dict/");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.out.println("Nie udało sie utworzyć katalogu dla słownika!");
                return;
            }
        }

        DictionaryDAO dictionaryDAO = DictionaryDAO.getInstance();
        try {
            dictionaryDAO.establishConnection();
        } catch(Exception exception) {
            System.out.println("Błąd podczas inicjalizacji słownika!");
        }

        //list to add all the clients thread
        ArrayList<ServerThread> threadList = new ArrayList<>();
        try (ServerSocket serversocket = new ServerSocket(2137)){
            while(true) {
                Socket socket = serversocket.accept();
                ServerThread serverThread = new ServerThread(socket, threadList);
                //starting the thread
                threadList.add(serverThread);
                serverThread.start();
            }
        } catch (Exception e) {
            System.out.println("Error occured in main: " + e.getStackTrace());
        }
    }
}

