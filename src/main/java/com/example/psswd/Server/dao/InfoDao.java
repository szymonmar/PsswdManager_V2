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
     * Funkcja pobierająca dane z kontraktu
     */
    public Info getInfo();
}

