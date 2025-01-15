package com.example.psswd.Client.model;

import com.example.psswd.CommPsswd;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/** Class grouping converters used in the program */
public class Converters {

    /** Converts observable list of Password objects to serializable list of CommPsswd objects
     * (use before sending list to the server)
     * @param passwords ObservableList of passwords
     * @return ArrayList of CommPsswds
     */
    public static ArrayList<CommPsswd> convertToStrings(ObservableList<Password> passwords) {
        ArrayList<CommPsswd> commPsswds = new ArrayList<>();
        for (Password password : passwords) {
            CommPsswd commPsswd = new CommPsswd();
            commPsswd.setId(password.getId());
            commPsswd.setName(password.getName());
            commPsswd.setUrl(password.getUrl());
            commPsswd.setEncryptedPassword(password.getPassword());
            commPsswds.add(commPsswd);
        }
        return commPsswds;
    }

    /** Converts single Password object to CommPsswd
     * @param password Password for conversion
     * @return CommPsswd object
     */
    public static CommPsswd convertToString(Password password) {
        CommPsswd commPsswd = new CommPsswd();
        commPsswd.setId(password.getId());
        commPsswd.setName(password.getName());
        commPsswd.setUrl(password.getUrl());
        commPsswd.setEncryptedPassword(password.getPassword());
        return commPsswd;
    }

    /** Converts serializable list of CommPsswd objects into observable list of Password objects
     * (use after receiving list from server, before displaying)
     * @param commPsswdArrayList ArrayList of CommPsswds
     * @return ObservableList of Passwords
     */
    public static ObservableList<Password> convertToObservable(ArrayList<CommPsswd> commPsswdArrayList) {
        ObservableList<Password> passwords = FXCollections.observableArrayList();
        for (CommPsswd commPsswd : commPsswdArrayList) {
            Password password = new Password();
            password.setId(commPsswd.getId());
            password.setName(commPsswd.getName());
            password.setUrl(commPsswd.getUrl());
            password.setPassword(commPsswd.getEncryptedPassword());
            password.setDecryptedPassword(commPsswd.getPassword());
            passwords.add(password);
        }
        return FXCollections.observableArrayList(passwords);
    }
}
