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
     * DictionaryDAOFactory instance
     */
    private static DictionaryDAOFactory dictionaryDAOFactoryInstance;

    /**
     * Data source
     */
    private static SQLiteConnectionPoolDataSource dataSource;

    /**
     * Creates and returns DictionaryDAOFactory instance
     * @return DictionaryDAOFactory instance
     */
    public static DictionaryDAOFactory getInstance() {
        if (dictionaryDAOFactoryInstance == null) {
            dictionaryDAOFactoryInstance = new DictionaryDAOFactory();
        }
        return dictionaryDAOFactoryInstance;
    }

    /**
     * Establishes database connection
     * @throws SQLException if SQLite database error occurred
     */
    public void establishConnection() throws SQLException {
        String path = System.getProperty("user.dir") + "/dict/dictionary.pass";
        boolean isNewDatabase = Files.notExists(Paths.get(path));
        String url = "jdbc:sqlite:" + path;
        dataSource = new SQLiteConnectionPoolDataSource();
        dataSource.setUrl(url);
        // if it's a new database
        if(isNewDatabase) {
            System.out.println("Dictionary initialization in progress...");
            System.out.println("Wait for the process to finish for the server to be available");
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
            createIndex();
            System.out.println("Dictionary initialization finished");
        }
    }

    /**
     * Fills dictionary database with data from dictionary.txt
     * @throws SQLException if SQLite database error occurred
     */
    private void fillDatabase() throws SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(
                System.getProperty("user.dir") + "/dictionary.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // processing of each line
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
            System.err.println("Dictionary initialization error: " + e.getMessage());
        }
    }

    /**
     * Creates index on password field in the dictionary database
     * @throws SQLException if SQLite database error occurred
     */
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

    /**
     * Checks if password is in the database (dictionary attack check)
     * @param passwd password to be checked
     * @return  false if password not in the database (dict test passed)
     *          true if password is in the database (dict test failed)
     * @throws SQLException if SQLite database error occurred
     */
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
     * Gets database connection
     * @return database connection
     * @throws SQLException if SQLite database error occurred
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
