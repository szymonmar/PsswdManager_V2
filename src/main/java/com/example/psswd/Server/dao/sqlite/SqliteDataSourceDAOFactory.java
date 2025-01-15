package com.example.psswd.Server.dao.sqlite;

import com.example.psswd.Server.dao.DAOFactory;
import com.example.psswd.Server.dao.InfoDao;
import com.example.psswd.Server.dao.PasswordsDao;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles the database connection and creating of data access objects
 */
public class SqliteDataSourceDAOFactory extends DAOFactory {

    /**
     * SqliteDataSourceDAOFactory instance
     */
    private static SqliteDataSourceDAOFactory sqliteDataSourceDAOFactoryInstance;

    /**
     * Data source for database connection
     */
    private static SQLiteConnectionPoolDataSource dataSource;

    private SqliteDataSourceDAOFactory() {}

    /**
     * Creates SqliteDataSourceDAOFactory instance if one does not exist
     * and returns it
     * @return SqliteDataSourceDAOFactory instance
     */
    public static SqliteDataSourceDAOFactory getInstance() {
        if (sqliteDataSourceDAOFactoryInstance == null) {
            sqliteDataSourceDAOFactoryInstance = new SqliteDataSourceDAOFactory();
        }
        return sqliteDataSourceDAOFactoryInstance;
    }

    /**
     * Establishes connection to the database
     * @param path path to the database file
     * @throws SQLException if database error occured
     */
    public void establishConnection(String path) throws SQLException {
        boolean isNewDatabase = Files.notExists(Paths.get(path));
        String url = "jdbc:sqlite:" + path;
        dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl(url);
        // jeśli to jest nowa baza danych
        if(isNewDatabase) {
            String query = """
                            CREATE TABLE passwords (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                "name" TEXT NOT NULL,
                                "url" TEXT,
                                "password" BLOB NOT NULL
                            );
                            """;
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)){
                ps.executeUpdate();
            }
            query = """
                    CREATE TABLE info (
                        "name" TEXT NOT NULL,
                        "challenge" BLOB NOT NULL,
                        "salt" BLOB NOT NULL
                    );
                    """;
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)){
                ps.executeUpdate();
            }
        }
    }

    /**
     * Returns data access object for the info table
     * @return data access object for the info table
     */
    public InfoDao getInfoDao() {
        return new SqliteDataSourceInfoDAOImpl();
    }

    /**
     * Returns data access object for the password table
     * @return Data access object for the password table
     */
    public PasswordsDao getPasswordsDao() {
        return new SqliteDataSourcePasswordsDAOImpl();
    }

    /**
     * Returns connection to the database
     * @return database connection
     * @throws SQLException if database error occured
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
