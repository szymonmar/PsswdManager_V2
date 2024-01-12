package com.example.psswd.dao;

import com.example.psswd.dao.sqlite.SqliteDataSourceDAOFactory;
import com.example.psswd.model.Info;


public abstract class DAOFactory {
    public abstract InfoDao getInfoDao();

    public static DAOFactory getDAOFactory() {
        return SqliteDataSourceDAOFactory.getInstance();
    }
}
