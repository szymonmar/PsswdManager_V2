package com.example.psswd.Server.model;

import javafx.beans.property.SimpleStringProperty;


/**
 * Class representing single password in the manager
 */
public class Password {

    /**
     * ID of a single password entity in the manager
     */
    private Integer id;

    /**
     * Name of a single password entity in the manager (e.g. Facebook)
     */
    private final SimpleStringProperty name = new SimpleStringProperty();

    /**
     * URL of a single password entity in the manager (e.g. facebook.com)
     */
    private final SimpleStringProperty url = new SimpleStringProperty();

    /**
     * Encrypted password of a single password entity in the manager
     */
    private byte[] password;


    /**
     * @return password entity ID [Integer]
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets ID of a password entity
     * @param id [Integer]
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return name of a password entity [String]
     */
    public String getName() {
        return name.get();
    }

    /**
     * Sets name of a password entity
     * @param name [String]
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * @return URL of a password entity [String]
     */
    public String getUrl() {
        return url.get();
    }

    /**
     * Sets URL of a password entity
     * @param url [String]
     */
    public void setUrl(String url) {
        this.url.set(url);
    }

    /**
     * @return encrypted password [ byte[] ]
     */
    public byte[] getPassword() {
        return password;
    }

    /**
     * Sets encrypted password
     * @param password Żądane hasło [ byte[] ]
     */
    public void setPassword(byte[] password) {
        this.password = password;
    }
}
