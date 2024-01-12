package com.example.psswd.alert;

public interface Alert {
    Alert setTitle(String title);
    Alert setHeaderText(String headerText);
    default Alert setException(Exception exception) {return this;};
}
