package io.github.frc5024.lib5k.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import io.github.frc5024.lib5k.config.types.JSONEncoder;
import io.github.frc5024.lib5k.config.types.JSONMotor;
import io.github.frc5024.lib5k.unittest.ConfigValidator;

public class SingleInstanceJSONConfigTest {

    class TestConfig {
        JSONMotor motor;
        JSONEncoder encoder;
    }

    @Test
    public void validateConfigFile() {
        // Create a config
        SingleInstanceJSONConfig<TestConfig> config = new SingleInstanceJSONConfig<>(TestConfig.class);

        new ConfigValidator(config).validate();
    }

    @Test
    public void testFileLoading() throws IOException {

        // Create a config
        SingleInstanceJSONConfig<TestConfig> config = new SingleInstanceJSONConfig<>(TestConfig.class);

        // Ensure no config is loaded yet
        assertFalse("Config unloaded", config.hasLoadedConfig());

        // Load config
        TestConfig loadedConfig = config.getConfig();

        // Ensure the config is loaded
        assertTrue("Config loaded", config.hasLoadedConfig());

        // Ensure the backup file exists
        assertTrue("Config backed up", config.getBackupFilePath().exists());

        // Ensure single-instance loading works
        assertEquals(loadedConfig, config.getConfig());

        // Ensure data is read correctly
        assertEquals(1440, loadedConfig.encoder.cpr);

    }

}