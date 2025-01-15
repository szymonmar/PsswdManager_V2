package com.example.psswd;
import java.io.Serializable;

/** Class representing login credentials used for server-client communication */
public class LoginCredentials implements Serializable {

    /**
     * Username
     */
    private String login;

    /**
     * Password
     */
    private String haslo;

    /**
     * New password
     */
    private String noweHaslo;


    public LoginCredentials(String login, String haslo) {
        this.login = login;
        this.haslo = haslo;
    }

    public LoginCredentials(String haslo) {
        this.haslo = haslo;
    }

    /**
     * @return login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return haslo
     */
    public String getHaslo() {
        return haslo;
    }

    /**
     * @return noweHaslo
     */
    public String getNoweHaslo() {
        return noweHaslo;
    }

    /**
     * Sets login
     * @param login username
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Sets haslo
     * @param haslo password
     */
    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    /**
     * Sets noweHaslo
     * @param noweHaslo new password
     */
    public void setNoweHaslo(String noweHaslo) {
        this.noweHaslo = noweHaslo;
    }
}
