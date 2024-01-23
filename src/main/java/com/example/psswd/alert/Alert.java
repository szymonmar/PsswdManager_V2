package com.example.psswd.alert;

/**
 * Interfejs reprezentujący alert wyświetlany w programie
 */
public interface Alert {

    /**
     * Służy do ustawienia tytułu alertu
     * @param title Tytuł alertu [String]
     * @return Obiekt typu Alert z ustawionym tytułem
     */
    Alert setTitle(String title);

    /**
     * Służy do ustawienia tekstu nagłówka alertu
     * @param headerText Tekst nagłówka alertu [String]
     * @return Obiekt typu Alert z ustawionym tekstem nagłówka
     */
    Alert setHeaderText(String headerText);

    /**
     * Przygotowuje Exception Stacktrace do wyświetlenia w oknie alertu
     * @param exception Wyjątek do wyświetlenia [Exception]
     * @return Obiekt typu Alert zawierający wyjątek do wyświetlenia
     */
    default Alert setException(Exception exception) {return this;};
}
