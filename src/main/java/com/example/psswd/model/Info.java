package com.example.psswd.model;

/**
 * Klasa przechowująca informacje o bazie danych potrzebne do odkodowania i zakodowania jej
 */
public class Info {

    /**
     * Nazwa bazy danych
     */
    private String name;

    /**
     * Challenge - zakodowana nazwa potrzebna do sprawdzania poprawności hasła
     */
    private byte[] challenge;

    /**
     * Sól - losowe dane dodawane do hasła przed hashowaniem
     */
    private byte[] salt;

    /**
     * Zwraca nazwę bazy danych
     * @return nazwa bazy danych
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwę bazy danych
     * @param name nazwa bazy danych do ustawienia [String]
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Zwraca challenge
     * @return challenge
     */
    public byte[] getChallenge() {
        return challenge;
    }

    /**
     * Ustawia challenge
     * @param challenge challenge do ustawienia [ byte[] ]
     */
    public void setChallenge(byte[] challenge) {
        this.challenge = challenge;
    }

    /**
     * Zwraca sól
     * @return sól
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Ustawia sól
     * @param salt sól do ustawienia [ byte[] ]
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}
