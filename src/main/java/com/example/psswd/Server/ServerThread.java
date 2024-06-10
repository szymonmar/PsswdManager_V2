package com.example.psswd.Server;

import com.example.psswd.CommPsswd;
import com.example.psswd.LoginCredentials;
import com.example.psswd.Request;
import com.example.psswd.Server.crypto.CryptoController;
import com.example.psswd.Server.dao.sqlite.SqliteDataSourceDAOFactory;
import com.example.psswd.Server.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


public class ServerThread extends Thread {
    private Socket socket;
    private ArrayList<ServerThread> threadList;
    private BufferedReader input;
    private PrintWriter output;
    private ObjectInputStream objectInput;
    private ObjectOutputStream objectOutput;
    private LoginCredentials loginCredentials;
    private final SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();


    public ServerThread(Socket socket, ArrayList<ServerThread> threads) {
        this.socket = socket;
        this.threadList = threads;
    }


    @Override
    public void run() {
        try {
            // Tworzenie strumieni wejścia i wyjścia
            input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(),true);
            objectInput = new ObjectInputStream(socket.getInputStream());
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            boolean loggedIn = false;
            Request request;
            ObservableList<Password> passwords = FXCollections.observableArrayList();


            while(true) {
                // odczyt żądania od clienta
                System.out.println("Waiting for request");
                request = (Request) objectInput.readObject();
                System.out.println(request.getRequest());
                switch (request.getRequest()) {
                    case ("login"):
                        loginCredentials = (LoginCredentials) objectInput.readObject();
                        loggedIn = openLocalDatabase(loginCredentials);
                        if(loggedIn) {
                            passwords = FXCollections.observableArrayList(sqliteDataSourceDAOFactory.getPasswordsDao().getPasswords());
                            objectOutput.writeObject(CryptoController.decryptPasswordsArray(
                                    Converters.convertToStrings(passwords)));
                        }
                        break;
                    case ("newuser"):
                        loggedIn = createLocalDatabase((LoginCredentials) objectInput.readObject());
                        if(loggedIn) {
                            passwords = FXCollections.observableArrayList(sqliteDataSourceDAOFactory.getPasswordsDao().getPasswords());
                            objectOutput.writeObject(CryptoController.decryptPasswordsArray(
                                    Converters.convertToStrings(passwords)));
                        }
                        break;
                    case("edit"):
                        if(loggedIn){
                            CommPsswd password = (CommPsswd) objectInput.readObject();
                            editPassword(password);
                        }
                        break;
                    case("add"):
                        if(loggedIn){
                            CommPsswd password = (CommPsswd) objectInput.readObject();
                            addPassword(password);
                        }
                        break;
                    case("delete"):
                        if(loggedIn) {
                            CommPsswd password = (CommPsswd) objectInput.readObject();
                            deletePassword(password);
                        }
                        break;
                    case("logout"):
                        if(loggedIn) {
                            try {
                                loginCredentials = null;
                                loggedIn = false;
                                objectOutput.writeObject(new Request("success"));
                                System.out.println("Logged out");
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                try {
                                    objectOutput.writeObject(new Request("Failed to log out"));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        break;
                    case("update"):
                        if(loggedIn) {
                            passwords = FXCollections.observableArrayList(sqliteDataSourceDAOFactory.getPasswordsDao().getPasswords());
                            objectOutput.writeObject(CryptoController.decryptPasswordsArray(
                                    Converters.convertToStrings(passwords)));                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection(){
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
            if (objectInput != null) objectOutput.close();
            if (objectOutput != null) objectOutput.close();
        } catch (IOException e) {
            System.out.println("Already closed!");
        }
    }

    private boolean openLocalDatabase(LoginCredentials loginCredentials) {

        SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
        try {
            // tworzy połączenie pomiędzy kontraktem i bazą danych
            sqliteDataSourceDAOFactory.establishConnection(System.getProperty("user.dir") + "/databases/" + loginCredentials.getLogin() + ".pass");
        } catch (Exception exception) {
            System.out.println("Could not open the database");
            try {
                objectOutput.writeObject(new Request("Failed to log in"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        Info info;
        try {
            // pobiera informacje z kontraktu o bazie danych
            info = sqliteDataSourceDAOFactory.getInfoDao().getInfo();
            System.out.println(Arrays.toString(info.getChallenge()));

        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                objectOutput.writeObject(new Request("Failed to log in"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        CryptoController cryptoController = CryptoController.getInstance();
        cryptoController.setDatabaseName(loginCredentials.getLogin());
        boolean isValidPassword;
        try {
            // ustawia zmienne potrzebne do odkodowania bazy danych
            cryptoController.setSalt(info.getSalt());
            cryptoController.initializeKey(loginCredentials.getHaslo());
            // sprawdza czy podano dobre hasło do bazy danych
            isValidPassword = cryptoController.verify(info.getChallenge());
        } catch (Exception exception) {
            System.out.println("Failed to initialize key from password");
            try {
                objectOutput.writeObject(new Request("Failed to log in"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        // jeśli hasło jest niepoprawne
        if(!isValidPassword) {
            System.out.println("Wrong password");
            try {
                objectOutput.writeObject(new Request("Failed to log in"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        System.out.println("zalogowano do bazy danych");
        try {
            objectOutput.writeObject(new Request("success"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    /**
     * Funkcja do tworzenia nowej bazy danych (nowego użytkownika) po kliknięciu "CREATE"
     */
    public boolean createLocalDatabase(LoginCredentials newUserCredentials) {

        String dbName = newUserCredentials.getLogin();
        String passwd = newUserCredentials.getHaslo();

        if (new File("databases/" + dbName +".pass").exists()) {
            System.out.println("Username already exists");
            try {
                objectOutput.writeObject(new Request("Username already exists"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }

        // Tworzenie nowego pliku z bazą danych dla użytkownika
        File file = new File("databases/" + dbName + ".pass");
        System.out.println("Utworzono nowego usera");


        SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
        try {
            // Tworzy połączenie pomiędzy kontraktem a plikiem z bazą danych
            sqliteDataSourceDAOFactory.establishConnection(file.getAbsolutePath());
        } catch (Exception exception) {
            System.out.println("Could not create a database");
            try {
                objectOutput.writeObject(new Request("Failed to create a database"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }

        Info info = new Info();
        info.setName(dbName); // Zapis nazwy bazy danych do obiektu z informacjami o bazie
        CryptoController cryptoController = CryptoController.getInstance();
        cryptoController.setDatabaseName(dbName);
        try {
            cryptoController.initializeKey(passwd); // Tworzymy klucz na podstawie hasła
            info.setChallenge(cryptoController.encrypt(dbName)); // Szyfrujemy nazwę bazy danych, aby stworzych "challenge" i zapisujemy do obiektu z informacjami o bazie
            info.setSalt(cryptoController.getSalt()); //Tworzymy i zapisujemy salt do obiektu z informacjami o bazie

            sqliteDataSourceDAOFactory.getInfoDao().insertInfo(info); // Zapisujemy informacje do kontraktu
        } catch (Exception exception) {
            System.out.println("Failed to initialize key from password");
            try {
                objectOutput.writeObject(new Request("Failed to create a database"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        try {
            objectOutput.writeObject(new Request("success"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Połączono z bazą danych");
        return true;
    }

    // wysłać hasło jako string!!!
    public void addPassword(CommPsswd addedPsswd) {
        System.out.println(addedPsswd.toString());
        Password password = new Password();
        password.setName(addedPsswd.getName());
        password.setUrl(addedPsswd.getUrl());
        CryptoController cryptoController = CryptoController.getInstance();
        try {
            // Próba zakodowania hasła i zapisania go w obiekcie password
            password.setPassword(cryptoController.encrypt(addedPsswd.getPassword()));
        } catch (Exception exception) {
            // Wyświetlenie błędu w razie niepowodzenia
            System.out.println("Failed to add password to database");
            try {
                objectOutput.writeObject(new Request("Failed to add a password"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            // Próba stworzenia DAO w celu dodania wpisu do bazy danych
            sqliteDataSourceDAOFactory.getPasswordsDao().insertPassword(password);
        } catch (Exception exception) {
            // Wyświetlenie błędu w razie niepowodzenia
            System.out.println("Failed to add password to database");
            try {
                objectOutput.writeObject(new Request("Failed to add a password"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            objectOutput.writeObject(new Request("success"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePassword(CommPsswd commPsswd) {
        Password password = Converters.convertToPassword(commPsswd);
        try {
            SqliteDataSourceDAOFactory sqliteDataSourceDAOFactory = SqliteDataSourceDAOFactory.getInstance();
            sqliteDataSourceDAOFactory.getPasswordsDao().deletePassword(password.getId());
        } catch (Exception exception) {
            System.out.println("Failed to delete from database");
            try {
                objectOutput.writeObject(new Request("Failed to delete password"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            objectOutput.writeObject(new Request("success"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Hasło zostało usunięte.");
    }

    public void editPassword(CommPsswd commPsswd) {
        Password password = new Password();
        password.setId(commPsswd.getId());
        password.setName(commPsswd.getName());
        password.setUrl(commPsswd.getUrl());
        CryptoController cryptoController = CryptoController.getInstance();
        try {
            // Próba zakodowania hasła i zapisania go w obiekcie password
            password.setPassword(cryptoController.encrypt(commPsswd.getPassword()));
        } catch (Exception exception) {
            // Wyświetlenie błędu w razie niepowodzenia
            System.out.println("Failed to initialize key from password");
            try {
                objectOutput.writeObject(new Request("Failed to edit password"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            // Próba stworzenia DAO w celu dodania wpisu do bazy danych
            sqliteDataSourceDAOFactory.getPasswordsDao().updatePassword(password.getId(), password);
        } catch (Exception exception) {
            // Wyświetlenie błędu w razie niepowodzenia
            System.out.println("Failed to update the database");
            try {
                objectOutput.writeObject(new Request("Failed to edit password"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            objectOutput.writeObject(new Request("success"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Hasło zostało zmodyfikowane.");

    }

}
