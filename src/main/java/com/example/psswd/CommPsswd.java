package com.example.psswd;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serializable class representing single password entity
 * essential for server-client communication
 */
public class CommPsswd implements Serializable {

    @Serial
    private static final long serialVersionUID = 420692137;

    /**
     * ID of a single password entity in the manager
     */
    private int id;

    /**
     * Name of a single password entity in the manager (e.g. Facebook)
     */
    private String name;

    /**
     * URL of a single password entity in the manager (e.g. facebook.com)
     */
    private String url;

    /**
     * Decrypted password of a single password entity in the manager
     */
    private String password;

    /**
     * Encrypted password of a single password entity in the manager
     */    private byte[] encryptedPassword;

    public CommPsswd() {}

    /**
     * Getter for name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for password name
     * @param name password name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for url
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter for password url
     * @param url password url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter for decrypted password
     * @return decrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for decrypted password
     * @param password decrypted password
     */
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

    /**
     * Getter for password id
     * @return password id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for password id
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for encrypted password
     * @return encrypted password
     */
    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * Setter for encrypted password
     * @param encryptedPassword encrypted password
     */
    public void setEncryptedPassword(byte[] encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
