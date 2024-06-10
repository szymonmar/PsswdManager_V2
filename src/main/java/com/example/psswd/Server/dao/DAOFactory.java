package com.example.psswd.Server.dao;

import com.example.psswd.Server.dao.sqlite.SqliteDataSourceDAOFactory;

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
