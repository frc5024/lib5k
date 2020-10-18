package io.github.frc5024.lib5k.utils.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import edu.wpi.first.wpilibj.Filesystem;

/**
 * Tools for loading JSON files
 */
public class JSONLoader {

    /**
     * Load a JSON file from the FRC deploy directory
     * 
     * @param <T>      Object type
     * @param filename File name
     * @param clazz    Type of the object that should be loaded
     * @return Loaded object
     * @throws FileNotFoundException Thrown if the file does not exist
     * @throws JsonSyntaxException   Thrown if the JSON file has invalid syntax
     * @throws JsonIOException       Thrown if there is a JSON IO issue
     */
    public static <T> T loadJsonObjectFromDeployDirectory(String filename, Class<T> clazz)
            throws FileNotFoundException, JsonSyntaxException, JsonIOException {
        return loadJsonObject(Paths.get(Filesystem.getDeployDirectory().getAbsolutePath(), filename).toFile(), clazz);
    }

    /**
     * Load a JSON file
     * 
     * @param <T>   Object type
     * @param file  File
     * @param clazz Type of the object that should be loaded
     * @return Loaded object
     * @throws FileNotFoundException Thrown if the file does not exist
     * @throws JsonSyntaxException   Thrown if the JSON file has invalid syntax
     * @throws JsonIOException       Thrown if there is a JSON IO issue
     */
    public static <T> T loadJsonObject(File file, Class<T> clazz)
            throws FileNotFoundException, JsonSyntaxException, JsonIOException {

        // Open the file from the deploy directory
        FileReader jsonFile = new FileReader(file);

        // Parse the object
        return new Gson().fromJson(jsonFile, clazz);

    }

}