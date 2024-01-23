package com.example.psswd.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;



/**
 * Klasa obsługująca plik z konfiguracją (plik przechowujący zapamiętane bazy danych)
 * serializacja
 */
public class Config {
    @JacksonXmlRootElement(localName = "PasswdConfig")

    /**
     * Klasa przechowująca bazy danych w formie przygotowanym pod obsługę pliku XML
     * Klasa wewnętrzna
     */
    private static class ConfigXML {
        @JacksonXmlProperty(localName = "Database")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<DatabaseRecord> databases = new ArrayList<>();

    }

    /**
     * Tworzy obiekt klasy wewnętrznej ConfigXML
     */
    private ConfigXML configXML = new ConfigXML();

    /**
     * Uchwyt do pliku z konfiguracją
     */
    private final File configFile;

    /**
     * Instancja Config
     */
    private static Config configInstance;


    /**
     * Konstruktor obiektu Config, przygotowuje środowisko do obsługi pliku
     * @throws IOException jeśli wystąpi błąd strumienia wejścia / wyjścia
     */
    private Config() throws IOException {
        // tworzy ścieżkę dostępu do pliku
        String userHome = System.getProperty("user.home");
        Path configPath = Paths.get(userHome,".passwd", "config.xml");


        boolean configExists = Files.exists(configPath);

        // sprawdza czy plik istnieje
        if(!configExists) {
            Files.createDirectories(configPath.getParent());
            Files.createFile(configPath);
        }

        // otwiera plik
        configFile = new File(configPath.toString());

        // mapuje plik
        if(configExists) {
            XmlMapper xmlMapper = new XmlMapper();
            configXML = xmlMapper.readValue(configFile, ConfigXML.class);
        }
    }

    /**
     * Zwraca instancję Configa (jeśli nie istnieje to ją tworzy)
     * @return instancja Config [Config]
     * @throws IOException jeśli wystąpi błąd strumienia wejścia / wyjścia
     */
    public static Config getInstance() throws IOException {
        if(configInstance == null) {
            configInstance = new Config();
        }
        return configInstance;
    }

    /**
     * Dodaje bazę danych do pliku config.xml
     * @param name nazwa bazy danych [String]
     * @param path ścieżka do bazy danych [String]
     * @throws IOException jeśli wystąpi błąd strumienia wejścia / wyjścia
     */
    public void addDatabase(String name, String path) throws IOException {
        DatabaseRecord databaseRecord = new DatabaseRecord(name, path);
        configXML.databases.add(databaseRecord);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(configFile, configXML);
    }

    /**
     * Usuwa bazę danych z pliku config.xml
     * @param databaseRecord baza danych do usunięcia
     * @throws IOException jeśli wystąpi błąd strumienia wejścia / wyjścia
     */
    public void removeDatabase(DatabaseRecord databaseRecord) throws IOException {
        Boolean removed = configXML.databases.remove(databaseRecord);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(configFile, configXML);
        if(removed.equals(false)) {
            throw new RuntimeException("No such database");
        }
    }

    /**
     * Tworzy listę baz danych
     * @return lista baz danych [List]
     */
    public List<DatabaseRecord> getDatabases() {
        return configXML.databases;
    }
}
