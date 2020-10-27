package io.github.frc5024.lib5k.unittest;

import java.io.IOException;

import com.google.gson.stream.MalformedJsonException;

import io.github.frc5024.lib5k.config.SingleInstanceJSONConfig;

/**
 * A tool for validating JSON configs
 */
public class ConfigValidator {

    private final SingleInstanceJSONConfig loader;

    /**
     * Create a JSON config validator
     * 
     * @param configLoader SingleInstanceJSONConfig to validate
     */
    public ConfigValidator(SingleInstanceJSONConfig configLoader) {
        this.loader = configLoader;
    }

    /**
     * Validate the config
     */
    public void validate() {

        // Validate the JSON file
        try {
            this.loader.getConfig();
        } catch (MalformedJsonException e) {
            System.out.println("Failed to validate JSON file");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to load JSON file");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

}