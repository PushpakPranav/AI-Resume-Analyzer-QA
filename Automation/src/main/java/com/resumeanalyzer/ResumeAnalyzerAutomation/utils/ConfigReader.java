package com.resumeanalyzer.ResumeAnalyzerAutomation.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private final Properties properties;

    public ConfigReader() throws IOException {

        properties = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new FileNotFoundException("config.properties not found");
            }

            properties.load(input);
        }
    }

    public String getProperty(String key) {

        String value = properties.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException(
                    "Property not found: " + key);
        }

        return value;
    }
}