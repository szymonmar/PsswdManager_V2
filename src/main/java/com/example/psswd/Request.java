package com.example.psswd;

import java.io.Serializable;

/** klasa serializująca request w celu wysłania go do serwera */
public class Request implements Serializable {
    private String request;

    public Request(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
