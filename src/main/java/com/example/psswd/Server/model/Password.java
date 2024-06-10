package com.example.psswd.Server.model;

import javafx.beans.property.SimpleStringProperty;


/**
 * Klasa do reprezentowania wpisu w managerze haseł
 */
public class Password {

    /**
     * Przechowuje ID wpisu w managerze haseł
     */
    private Integer id;

    /**
     * Przechowuje nazwę wpisu w managerze haseł, np. Facebook
     */
    private final SimpleStringProperty name = new SimpleStringProperty();

    /**
     * Przechowuje URL w danym wpisie w managerze haseł, np. facebook.com
     */
    private final SimpleStringProperty url = new SimpleStringProperty();

    /**
     * Przechowuje hasło w danym wpisie w managerze haseł
     */
    private byte[] password;


    /**
     * Zwraca ID wpisu w managerze
     * @return ID wpisu [Integer]
     */
    public Integer getId() {
        return id;
    }

    /**
     * Służy do ustawienia ID wpisu w managerze
     * @param id ID do ustawienia [Integer]
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Zwraca nazwę wpisu w managerze
     * @return Nazwa wpisu [String]
     */
    public String getName() {
        return name.get();
    }

    /**
     * Służy do ustawienia nazwy wpisu w managerze
     * @param name Żądana nazwa wpisu [String]
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Zwraca URL wpisu w managerze
     * @return URL wpisu [String]
     */
    public String getUrl() {
        return url.get();
    }

    /**
     * Służy do ustawienia URL wpisu w managerze
     * @param url Żądany URL [String]
     */
    public void setUrl(String url) {
        this.url.set(url);
    }

    /**
     * Zwraca hasło z danego wpisu w managerze
     * @return Hasło [ byte[] ]
     */
    public byte[] getPassword() {
        return password;
    }

    /**
     * Służy do ustawienia hasła w danym wpisie w managerze
     * @param password Żądane hasło [ byte[] ]
     */
    public void setPassword(byte[] password) {
        this.password = password;
    }
}
