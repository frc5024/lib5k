package io.github.frc5024.lib5k.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import com.google.common.io.Files;
import com.google.gson.Gson;

import edu.wpi.first.wpilibj.Filesystem;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.FileManagement;

/**
 * SingleInstanceJSONConfig is a tool for loading a JSON file into a Java object
 * 
 * @param <T> Type of object to load
 */
public class SingleInstanceJSONConfig<T> {

    // Logging
    private RobotLogger logger = RobotLogger.getInstance();

    // Settings
    private final String filename;
    private final boolean save;

    // Config object
    private final Class<T> clazz;
    private T obj = null;

    /**
     * Create a SingleInstanceJSONConfig with default settings
     * 
     * @param clazz Class of the config
     */
    public SingleInstanceJSONConfig(Class<T> clazz) {
        this(clazz, true);
    }

    /**
     * Create a SingleInstanceJSONConfig
     * 
     * @param clazz         Class of the config
     * @param saveToSession Should save a copy of the config for debugging?
     */
    public SingleInstanceJSONConfig(Class<T> clazz, boolean saveToSession) {
        this(clazz, "robotconfig.json", saveToSession);
    }

    /**
     * Create a SingleInstanceJSONConfig
     * 
     * @param clazz         Class of the config
     * @param filename      Config JSON filename
     * @param saveToSession Should save a copy of the config for debugging?
     */
    public SingleInstanceJSONConfig(Class<T> clazz, String filename, boolean saveToSession) {
        this.filename = filename;
        this.save = saveToSession;
        this.clazz = clazz;
    }

    /**
     * Builds a file path for the config inside /home/lvuser/deploy/
     * 
     * @return Path to config file
     */
    private File getConfigFilePath() {
        return Paths.get(Filesystem.getDeployDirectory().getAbsolutePath(), this.filename).toFile();
    }

    /**
     * Builds a file path for the config inside session folder
     * 
     * @return Path to config file backup location
     */
    protected File getBackupFilePath() {
        return Paths.get(FileManagement.getSessionDirectoryPath(), this.filename).toFile();
    }

    /**
     * Parse the JSON config into the local object
     * 
     * @throws FileNotFoundException Thrown if the config is not found
     */
    private void parseConfig() throws FileNotFoundException {

        // Get ABS filepath to the config
        File file = getConfigFilePath();
        logger.log(String.format("Parsing logfile at %s", file.getAbsolutePath()));

        // Open a reader
        FileReader configReader = new FileReader(file);

        // Parse
        Gson gson = new Gson();
        this.obj = gson.fromJson(configReader, this.clazz);

    }

    /**
     * Backs up the config to the robot session directory
     */
    private void backupConfig() {

        // Get ABS filepath to the config
        File inFile = getConfigFilePath();

        // Get ABS filepath to the new config
        File outFile = getBackupFilePath();

        logger.log(String.format("Copying config %s to %s", inFile.getAbsolutePath(), outFile.getAbsolutePath()));

        // Copy
        try {
            Files.copy(inFile, outFile);
        } catch (IOException e) {
            logger.log(String.format("Failed to save config to %s. Maybe there is no USB attached?",
                    outFile.getAbsolutePath()));
        }

    }

    /**
     * Get an instance of the loaded config, or load the config
     * 
     * @return Config object
     * @throws IOException Thrown if there is a read issue
     */
    public T getConfig() throws IOException {
        if (obj == null) {
            parseConfig();
            if (save) {
                backupConfig();
            }
        }
        return obj;
    }

    /**
     * Get if the config has been loaded yet
     * 
     * @return Has been loaded?
     */
    public boolean hasLoadedConfig() {
        return obj != null;
    }

}