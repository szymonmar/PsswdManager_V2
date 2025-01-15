package com.example.psswd.Client.alert;

/**
 * Interface for alert-related functions
 */
public interface Alert {

    /**
     * Sets title for the alert
     * @param title Alert title [String]
     * @return Alert with title
     */
    Alert setTitle(String title);

    /**
     * Sets heading for the alert
     * @param headerText Heading text for the alert [String]
     * @return Alert with a heading
     */
    Alert setHeaderText(String headerText);
}
