package com.example.psswd.Server.dao;

import com.example.psswd.Server.model.Password;

import java.util.List;

/**
 * Interfejs reprezentujący kontrakt definiujący operacje dostępu do danych
 * dotyczących zapisanych haseł
 */
public interface PasswordsDao {

    /**
     * Zwraca listę wpisów w bazie danych haseł
     * @return lista wpisów z hasłami
     */
    public List<Password> getPasswords();

    /**
     * Funkcja obsługująca proces dodawania wpisu do bazy danych
     * @param password wpis do dodania [Password]
     */
    public void insertPassword(Password password);

    /**
     * Funkcja obsługująca proces aktualizacji danych we wpisie
     * @param id id wpisu do modyfikacji [int]
     * @param password zmodyfikowany wpis [Password]
     */
    public void updatePassword(int id, Password password);

    /**
     * Funkcja obsługująca proces usuwania wpisu z bazy danych
     * @param id id wpisu do usunięcia [int]
     */
    public void deletePassword(int id);
}
