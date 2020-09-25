package io.github.frc5024.lib5k.extras.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.common.io.Files;
import com.google.gson.Gson;

import edu.wpi.first.wpilibj.Filesystem;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.FileManagement;

public class ConfigHandler {
    private RobotLogger logger = RobotLogger.getInstance();
    private static ConfigHandler instance;

    // The deploy folder
    private final String CONFIGURATION_BASE_PATH = Filesystem.getDeployDirectory();

    // The folder in which configs sit
    private final String DEFAULT_CONFIG_FOLDER_NAME = "configs";
    private String configFolderName = DEFAULT_CONFIG_FOLDER_NAME;

    private ConfigHandler() {

    }

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    public <T> T loadConfigToObject(String configName, Class<T> clazz) throws FileNotFoundException {
        logger.log("Loading configuration from: %s", (Object) configName);

        // If there is a USB connected, copy the file
        if (FileManagement.isUSBAttached()) {
            copyConfigToSession(configName);
        }

        // Resolve the actual file path
        String absFilePath = getConfigurationFolderPath() + "/" + configName;
        logger.log("Resolved config file path as: %s", (Object) absFilePath);

        // Attempt to load the config file
        FileReader configReader = new FileReader(absFilePath);

        // Build JSON parser
        Gson gson = new Gson();

        return gson.fromJson(configReader, clazz);

    }

    public void setCustomConfigFolder(String folder) {
        logger.log("The config folder has been overridden");
        logger.log("New config folder: %s", (Object) folder);
        configFolderName = folder;
    }

    public boolean hasCustomConfigFolder() {
        return !configFolderName.equals(DEFAULT_CONFIG_FOLDER_NAME);
    }

    public String getConfigurationFolderPath() {
        return String.format("%s/%s", CONFIGURATION_BASE_PATH, configFolderName);
    }

    public String getSnapshotFolderPath() {
        return String.format("%s/%s", FileManagement.getSessionDirectoryPath(), configFolderName);
    }

    private void copyConfigToSession(String configName) throws IOException {
        logger.log("Taking a snapshot of the config file: %s", (Object) configName);

        // Todo: Make the correct subfolder here

        // Build a File for the result of the copy op
        File resultFile = new File(getSnapshotFolderPath() + "/" + configName);

        // Build a File for the input of the copy
        File inputFile = new File(getConfigurationFolderPath() + "/" + configName);

        // Use Guava to copy the file
        Files.copy(inputFile, resultFile);

        logger.log("Copy success");

    }

}