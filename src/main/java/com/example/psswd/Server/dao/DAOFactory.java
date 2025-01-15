package com.example.psswd.Server.dao;

import com.example.psswd.Server.dao.sqlite.SqliteDataSourceDAOFactory;

/**
 * Represents factory of data access objects
 */
public abstract class DAOFactory {

    /**
     * Returns Info table data access object
     * (used for encryption / decryption and checking user password)
     * @return data access object for Info
     */
    public abstract InfoDao getInfoDao();

    /**
     * Returns SqliteDataSourceDAOFactory instance
     * @return instance of SqliteDataSourceDAOFactory
     */
    public static DAOFactory getDAOFactory() {
        return SqliteDataSourceDAOFactory.getInstance();
    }
}
