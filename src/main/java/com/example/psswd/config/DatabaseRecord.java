package com.example.psswd.config;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record DatabaseRecord(@JacksonXmlProperty(localName = "name") String name,
                             @JacksonXmlProperty(localName = "path") String path) {
    public DatabaseRecord(String name, String path) {
        this.name = name;
        this.path = path;
    }

}
