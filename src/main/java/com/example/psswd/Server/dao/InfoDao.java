package com.example.psswd.Server.dao;

import com.example.psswd.Server.model.Info;

/**
 * Interfejs reprezentujący kontrakt definiujący operacje dostępu do danych
 * dla obiektów typu Info
 */
public interface InfoDao {

    /**
     * Funkcja dodająca dane do kontraktu
     * @param info dane do zapisania [Info]
     */
    public void insertInfo(Info info);

    /**
     * Funkcja usuwająca dane z tabeli info, potrzebna do zmiany hasła do konta użytkownika
     */
    public void deleteInfo();

    /**
     * Funkcja pobierająca dane z kontraktu
     */
    public Info getInfo();
}

