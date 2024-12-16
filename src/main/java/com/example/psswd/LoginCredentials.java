package com.example.psswd;
import java.io.Serializable;

/** klasa serializująca dane logowania w celu wysłania ich do serwera */
public class LoginCredentials implements Serializable {
    private String login;
    private String haslo;
    private String noweHaslo;

    public LoginCredentials(String login, String haslo) {
        this.login = login;
        this.haslo = haslo;
    }

    public LoginCredentials(String haslo) {
        this.haslo = haslo;
    }

    public String getLogin() {
        return login;
    }

    public String getHaslo() {
        return haslo;
    }

    public String getNoweHaslo() {
        return noweHaslo;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public void setNoweHaslo(String noweHaslo) {
        this.noweHaslo = noweHaslo;
    }
}
