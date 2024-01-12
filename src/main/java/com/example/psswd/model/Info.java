package com.example.psswd.model;

public class Info {
    private String name;
    private byte[] challenge;

    private byte[] salt;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getChallenge() {
        return challenge;
    }

    public void setChallenge(byte[] challenge) {
        this.challenge = challenge;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}
