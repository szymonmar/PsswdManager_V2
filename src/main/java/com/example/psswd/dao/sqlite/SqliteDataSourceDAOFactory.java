package com.example.psswd.dao.sqlite;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import com.example.psswd.dao.DAOFactory;
import com.example.psswd.dao.InfoDao;
import com.example.psswd.dao.PasswordsDao;
import org.sqlite.SQLiteConfig;
import org.sqlite.javax.*;

public class SqliteDataSourceDAOFactory extends DAOFactory {
    private static SqliteDataSourceDAOFactory sqliteDataSourceDAOFactoryInstance;
    private static SQLiteConnectionPoolDataSource dataSource;

    private SqliteDataSourceDAOFactory(){

    }

    public static SqliteDataSourceDAOFactory getInstance() {
        if (sqliteDataSourceDAOFactoryInstance == null) {
            sqliteDataSourceDAOFactoryInstance = new SqliteDataSourceDAOFactory();
        }
        return sqliteDataSourceDAOFactoryInstance;
    }

    public void establishConnection(String path) throws SQLException {
        boolean isNewDatabase = Files.notExists(Paths.get(path));
        String url = "jdbc:sqlite:" + path;
        dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl(url);
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

    public InfoDao getInfoDao() {
        return new SqliteDataSourceInfoDAOImpl();
    }
    public PasswordsDao getPasswordsDao() {
        return new SqliteDataSourcePasswordsDAOImpl();
    }


    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
