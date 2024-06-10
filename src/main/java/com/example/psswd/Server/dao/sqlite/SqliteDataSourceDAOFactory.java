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
 * Klasa obsługująca tworzenie obiektów dostępu do danych
 * potrzebna do komunikacji z bazą danych
 */
public class SqliteDataSourceDAOFactory extends DAOFactory {

    /**
     * Instancja SqliteDataSourceDAOFactory
     */
    private static SqliteDataSourceDAOFactory sqliteDataSourceDAOFactoryInstance;

    /**
     * źródło danych
     */
    private static SQLiteConnectionPoolDataSource dataSource;

    /**
     * Konstruktor obiektów typu SqliteDataSourceDAOFactory
     */
    private SqliteDataSourceDAOFactory(){

    }

    /**
     * Tworzy i zwraca instancję SqliteDataSourceDAOFactory
     * @return instancja SqliteDataSourceDAOFactory
     */
    public static SqliteDataSourceDAOFactory getInstance() {
        if (sqliteDataSourceDAOFactoryInstance == null) {
            sqliteDataSourceDAOFactoryInstance = new SqliteDataSourceDAOFactory();
        }
        return sqliteDataSourceDAOFactoryInstance;
    }

    /**
     * Funkcja nawiązująca połączenie z bazą danych
     * @param path ścieżka do bazy danych
     * @throws SQLException jeśli wystąpił błąd związany z bazą danych
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
     * Zwraca kontrakt dostępu do tablicy info w bazie danych
     * @return kontrakt dostępu do tablicy info w bazie danych
     */
    public InfoDao getInfoDao() {
        return new SqliteDataSourceInfoDAOImpl();
    }

    /**
     * Zwraca kontrakt dostępu do tablicy passwords w bazie danych
     * @return kontrakt dostępu do tablicy passwords w bazie danych
     */
    public PasswordsDao getPasswordsDao() {
        return new SqliteDataSourcePasswordsDAOImpl();
    }

    /**
     * Zwraca uchwyt do bazy danych
     * @return uchwyt do bazy danych
     * @throws SQLException jeśli wystąpił błąd związany z bazą danych
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
