package com.example.psswd.dao;

import com.example.psswd.dao.sqlite.SqliteDataSourceDAOFactory;
import com.example.psswd.model.Info;

/**
 * Klasa abstarkcyjna reprezentująca fabrykę obiektów dostępu do danych
 */
public abstract class DAOFactory {

    /**
     * Funkcja zwracająca obiekt umożliwiający dostęp do tabeli z informacjami o bazie danych
     * (z informacjami potrzebnymi do rozszyfrowania)
     * @return obiekt dostępu do danych
     */
    public abstract InfoDao getInfoDao();

    /**
     * Zwraca instancję SqliteDataSourceDAOFactory
     * @return instancja SqliteDataSourceDAOFactory
     */
    public static DAOFactory getDAOFactory() {
        return SqliteDataSourceDAOFactory.getInstance();
    }
}
