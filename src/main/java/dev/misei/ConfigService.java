package dev.misei;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigService {
    private static ConfigService instance;
    private final PropertiesConfiguration config;

    private ConfigService() {
        try {
            Configurations configurations = new Configurations();
            config = configurations.properties("config.properties");
        } catch (ConfigurationException e) {
            throw new RuntimeException("Error loading configuration file: config.properties", e);
        }
    }

    public static synchronized ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService();
        }
        return instance;
    }

    public String getApiKey() {
        String apiKey = config.getString("api.key");

        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key not found or is empty in the configuration file.");
        }

        return apiKey;
    }
}
