package com.example.psswd;

import java.io.Serializable;

/** klasa reprezenyująca pojedynczy rekord hasła przystosowana do komunikacji z serwerem
 */
public class CommPsswd implements Serializable {

    private static final long serialVersionUID = 420692137;

    /** id wpisu z hasłem w bazie danych */
    private int id;
    /** nazwa wpisu */
    private String name;
    /** url wpisu */
    private String url;
    /** hasło we wpisie - odszyfrowane */
    private String password;
    /** hasło we wpisie - zaszyfrowane */
    private byte[] encryptedPassword;

    public CommPsswd() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AddedPsswd{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(byte[] encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
