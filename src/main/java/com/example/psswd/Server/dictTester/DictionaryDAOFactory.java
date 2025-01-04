package com.example.psswd.Server.dictTester;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DictionaryDAOFactory {
    /**
     * Instancja SqliteDataSourceDAOFactory
     */
    private static DictionaryDAOFactory dictionaryDAOFactoryInstance;

    /**
     * źródło danych
     */
    private static SQLiteConnectionPoolDataSource dataSource;

    /**
     * Konstruktor obiektów typu SqliteDataSourceDAOFactory
     */
    private DictionaryDAOFactory(){

    }

    /**
     * Tworzy i zwraca instancję SqliteDataSourceDAOFactory
     * @return instancja SqliteDataSourceDAOFactory
     */
    public static DictionaryDAOFactory getInstance() {
        if (dictionaryDAOFactoryInstance == null) {
            dictionaryDAOFactoryInstance = new DictionaryDAOFactory();
        }
        return dictionaryDAOFactoryInstance;
    }

    /**
     * Funkcja nawiązująca połączenie z bazą danych
     * @throws SQLException jeśli wystąpił błąd związany z bazą danych
     */
    public void establishConnection() throws SQLException {
        String path = System.getProperty("user.dir") + "/dict/dictionary.pass";
        boolean isNewDatabase = Files.notExists(Paths.get(path));
        String url = "jdbc:sqlite:" + path;
        dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl(url);
        // jeśli to jest nowa baza danych
        if(isNewDatabase) {
            String query = """
                            CREATE TABLE dictionary (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                "password" VARCHAR
                            );
                            """;
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)){
                ps.executeUpdate();
            }

            fillDatabase();
        }
        createIndex();

    }

    private void fillDatabase() throws SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(
                System.getProperty("user.dir") + "/dictionary.txt"))) {
            System.out.println("Dictionary initialization: start");
            String line;
            while ((line = reader.readLine()) != null) {
                // Przetwarzanie każdej linii
                String query = """
                INSERT INTO dictionary ("password")
                VALUES (?);
                """;
                try(Connection conn = getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)
                ) {
                    ps.setString(1, line);
                    ps.executeUpdate();
                }
            }
        } catch (IOException e) {
            System.err.println("Wystąpił błąd podczas czytania pliku: " + e.getMessage());
        }
        System.out.println("Dicitonary initialization: finish");
    }

    private void createIndex() throws SQLException{
        String query = """
                CREATE INDEX passwdIdx ON dictionary("password")
                """;
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.executeUpdate();
        }
    }

    public boolean passwordInDB(String passwd) throws SQLException{
        String query = """
                SELECT id FROM dictionary WHERE password = ?
                """;
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, passwd);
            ResultSet rs = ps.executeQuery();
            if (rs.getInt("id") == 0) {
                return false;
            } else {
                return true;
            }
        }
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
