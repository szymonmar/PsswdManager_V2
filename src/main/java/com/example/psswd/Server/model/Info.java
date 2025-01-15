package com.example.psswd.Server.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Class representing Info data set used for encrypting / decrypting
 */
public class Info  implements Serializable {

    private static final long serialVersionUID = 2137420;

    /**
     * Database name (username)
     */
    private String name;

    /**
     * Challenge - encrypted name used to check the password
     */
    private byte[] challenge;

    /**
     * Salt - random data added to password before hashing
     */
    private byte[] salt;

    /**
     * @return database name (username)
     */
    public String getName() {
        return name;
    }

    /**
     * Sets database name
     * @param name database name (username)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return challenge
     */
    public byte[] getChallenge() {
        return challenge;
    }

    /**
     * Sets challenge
     * @param challenge challenge
     */
    public void setChallenge(byte[] challenge) {
        this.challenge = challenge;
    }

    /**
     * @return salt
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Sets salt
     * @param salt salt
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "Info{" +
                "name='" + name + '\'' +
                ", challenge=" + Arrays.toString(challenge) +
                ", salt=" + Arrays.toString(salt) +
                '}';
    }
}
