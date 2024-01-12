package com.example.psswd.model;

import javafx.beans.property.SimpleStringProperty;

public class Password {
    private Integer id;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty url = new SimpleStringProperty();
    private byte[] password;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }
}
