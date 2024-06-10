package com.example.psswd.Server.model;

import com.example.psswd.CommPsswd;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/** klasa przechowuje konwertery używane do konwersji observable na serializable i vice versa */
public class Converters {

    /** konwertuje listę observable [Password] na listę przystosowaną do komunikacji
     * @param passwords observable list do konwersji [Password]
     * @return lista do komunikacji [CommPsswd]
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

    /** konwertuje pojedynczy rekord Password na CommPsswd
     * @param password Password do konwersji
     * @return CommPsswd
     */
    public static CommPsswd convertToString(Password password) {
        CommPsswd commPsswd = new CommPsswd();
        commPsswd.setId(password.getId());
        commPsswd.setName(password.getName());
        commPsswd.setUrl(password.getUrl());
        commPsswd.setEncryptedPassword(password.getPassword());
        return commPsswd;
    }

    /**
     * konwertuje pojedynczy rekord CommPsswd na Password
     * @param commPsswd CommPsswd do konwersji
     * @return Password
     */
    public static Password convertToPassword(CommPsswd commPsswd) {
        Password password = new Password();
        password.setId(commPsswd.getId());
        password.setName(commPsswd.getName());
        password.setUrl(commPsswd.getUrl());
        password.setPassword(commPsswd.getEncryptedPassword());
        return password;
    }

    /** konwertuje listę seralizowalną [CommPsswd] na listę observable
     * @param commPsswdArrayList - lista przygotowana do komunikacji
     * @return - lista observable do wyświetlania
     */
    public static ObservableList<Password> convertToObservable(ArrayList<CommPsswd> commPsswdArrayList) {
        ObservableList<Password> passwords = FXCollections.observableArrayList();
        for (CommPsswd commPsswd : commPsswdArrayList) {
            Password password = new Password();
            password.setId(commPsswd.getId());
            password.setName(commPsswd.getName());
            password.setUrl(commPsswd.getUrl());
            password.setPassword(commPsswd.getEncryptedPassword());
            passwords.add(password);
        }
        return FXCollections.observableArrayList(passwords);
    }
}
