package com.example.psswd.config;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Record reprezentuje bazę danych
 * @param name Nazwa bazy danych / Nazwa użytkownika [String]
 * @param path Ścieżka do pliku, w którym zapisana jest baza danych haseł użytkownika [String]
 */
public record DatabaseRecord(@JacksonXmlProperty(localName = "name") String name,
                             @JacksonXmlProperty(localName = "path") String path) {
    public DatabaseRecord(String name, String path) {
        this.name = name;
        this.path = path;
    }

}
