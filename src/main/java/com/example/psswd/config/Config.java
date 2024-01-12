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

// klasa wewnętrzna , serializacja
public class Config {
    @JacksonXmlRootElement(localName = "PasswdConfig")
    private static class ConfigXML {
        @JacksonXmlProperty(localName = "Database")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<DatabaseRecord> databases = new ArrayList<>();

    }

    private ConfigXML configXML = new ConfigXML();
    private final File configFile;

    private static Config configInstance;

    private Config() throws IOException {
        String userHome = System.getProperty("user.home");
        Path configPath = Paths.get(userHome,".passwd", "config.xml");

        boolean configExists = Files.exists(configPath);
        if(!configExists) {
            Files.createDirectories(configPath.getParent());
            Files.createFile(configPath);
        }
        configFile = new File(configPath.toString());

        if(configExists) {
            XmlMapper xmlMapper = new XmlMapper();
            configXML = xmlMapper.readValue(configFile, ConfigXML.class);
        }
    }

    public static Config getInstance() throws IOException {
        if(configInstance == null) {
            configInstance = new Config();
        }
        return configInstance;
    }

    public void addDatabase(String name, String path) throws IOException {
        DatabaseRecord databaseRecord = new DatabaseRecord(name, path);
        configXML.databases.add(databaseRecord);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(configFile, configXML);
    }

    public void removeDatabase(DatabaseRecord databaseRecord) throws IOException {
        Boolean removed = configXML.databases.remove(databaseRecord);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(configFile, configXML);
        if(removed.equals(false)) {
            throw new RuntimeException("No such database");
        }
    }

    public List<DatabaseRecord> getDatabases() {
        return configXML.databases;
    }
}
