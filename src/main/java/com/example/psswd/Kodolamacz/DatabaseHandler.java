package com.example.psswd.Kodolamacz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseHandler {

    private static final String kindOfDatabase = "jdbc:mysql://";
    private static final String dbAddress = "localhost";
    private static final String dbDriver = "com.mysql.cj.jdbc.Driver";
    private static final String dbUsername = "root";
    private static final String dbPassword = "";
    private static final int dbPort = 3306;
    private Connection connection;
    private Statement statement;

    public DatabaseHandler() { //
        try {
            if(checkDriver()) {
                this.connection = getConnection();
                this.statement = connection.createStatement();
            }
        } catch (SQLException e) {
            System.out.println("Błąd połączenia z bazą danych: " + e.getMessage() + " : " + e.getErrorCode());
            System.exit(2);
        }
    }



    public static boolean checkDriver() {
        try {
            Class.forName(dbDriver);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Błąd sterownika bazy danych");
            return false;
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", dbUsername);
        connectionProps.put("password", dbPassword);
        try {
            conn = DriverManager.getConnection(kindOfDatabase + dbAddress + ":" + dbPort, connectionProps);
        } catch (SQLException e) {
            System.out.println("Błąd połączenia z bazą danych: " + e.getMessage() + " : " + e.getErrorCode());
            System.exit(2);
        }
        return conn;
    }

    public void initialize(){
        try{
            statement.execute("USE kodolamacz");
        }catch (SQLException e){
            createDatabase();
        }
    }

    /** tworzy bazę danych
     */
    private void createDatabase(){
        try{
            System.out.println("Tworzenie bazy danych");
            statement.execute("CREATE DATABASE kodolamacz");
            statement.execute("USE kodolamacz");
            System.out.println("success");
            createTables();
        }catch (SQLException ex){
            System.err.println("Nie można outworzyć bazy danych");
        }
    }

    /** tworzy tabele w bazie danych */
    private void createTables(){
        try{
            System.out.println("Tworzenie tabeli");
            statement.execute("CREATE TABlE dictionary (ID bigint primary key NOT NULL AUTO_INCREMENT, word varchar(70) not null);");
            System.out.println("Utworzono tabele dictionary");
            statement.execute("CREATE TABlE dictionaryInfo (ID int primary key NOT NULL, numOfRecords bigint NOT NULL);");
            System.out.println("Utworzono tabele dictionaryInfo");
            System.out.println("success");
            loadFromTXT();
        }catch (SQLException e){
            System.err.println("Błąd w trakcie tworzenia tabel: " + e.toString());
        }
    }

    /** ładuje zawartość pliku txt do bazy danych */
    private void loadFromTXT(){
        System.out.println("Rozpoczynam wprowadzanie danych...");
        try{
            BufferedReader reader = new BufferedReader(
                    new FileReader("D:projekt java/Kodolamacz/Kodolamacz/beznazwy-kopia.txt"));
            String line;
            long numOfLines = 0;

            while(true){


                line = reader.readLine();
                if(line == null) {
                    break;
                }


                statement.executeUpdate("INSERT INTO dictionary (word) " +
                                "VALUES (\"" + line + "\")",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet r = statement.getGeneratedKeys();
                r.next();
                numOfLines++;
            }
            statement.executeUpdate("INSERT INTO dictionaryInfo (ID, numOfRecords) " +
                            "VALUES (0, \"" + numOfLines + "\")",
                    Statement.RETURN_GENERATED_KEYS);
            ResultSet r = statement.getGeneratedKeys();
            r.next();
            System.out.println("Zakończono wprowadzanie danych");
        }catch(FileNotFoundException e){
            System.err.println("Bład podczas otwierania pliku: " + e.toString());
        }catch(IOException ioException){
            System.err.println("Bład w trakcie odczytywania pliku: " + ioException.toString());
        }catch (SQLException sqlException){
            System.err.println("Bład w trakcie wprowadzania danych: " + sqlException.toString());
        }
    }

    /** zwraca słowo z bazy danych
     * @param wordId
     * @return słowo
     */
    public String getWord(long wordId){
        String word = "";
        try{
            statement.executeQuery("SELECT word from dictionary where ID=\"" + wordId +  "\"");
            ResultSet set = statement.getResultSet();
            while (set.next()){
                word = set.getString("word");
            }
        }catch (SQLException e){
            System.err.println("Nie można pobrać słowa z bazy danych" + e.toString());
        }

        return word;
    }

    /** zwraca liczbę rekordów z tabeli dictionaryInfo */
    public long getNumOfLines(){
        long nol = 0;
        try{
            statement.executeQuery("SELECT numOfRecords from dictionaryInfo where ID=0");
            ResultSet set = statement.getResultSet();
            while (set.next()){
                nol = set.getLong("numOfRecords");
            }
        }catch (SQLException e){
            System.err.println("Nie można pobrać słowa z bazy danych" + e.toString());
        }

        return nol;
    }

    /** dodaje odpowiedź uzytkownika do bazy danych
     * @param userID
     * @param answer A, B, C, D lub '' ''
     * @param queID
     */

    /** kończy połączenie z bazą danych */
    public void dbShutdown(){
        try{
            statement.close();
            connection.close();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
