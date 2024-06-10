package com.example.psswd;
import java.io.Serializable;

/** klasa serializująca dane logowania w celu wysłania ich do serwera */
public class LoginCredentials implements Serializable {
    private String login;
    private String haslo;

    public LoginCredentials(String login, String haslo) {
        this.login = login;
        this.haslo = haslo;
    }

    public String getLogin() {
        return login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }
}
