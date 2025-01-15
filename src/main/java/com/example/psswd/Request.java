package com.example.psswd;

import java.io.Serializable;

/** Class representing request used for server-client communication */
public class Request implements Serializable {

    /**
     * Request content
     */
    private String request;

    /**
     * Request constructor
     * @param request request content
     */
    public Request(String request) {
        this.request = request;
    }

    /**
     * @return request content
     */
    public String getRequest() {
        return request;
    }

    /**
     * Sets request content
     * @param request request content
     */
    public void setRequest(String request) {
        this.request = request;
    }
}
