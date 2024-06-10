package com.example.psswd.Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        //using serversocket as argument to automatically close the socket
        //the port number is unique for each server

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

