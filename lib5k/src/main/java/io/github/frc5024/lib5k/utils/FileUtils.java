package io.github.frc5024.lib5k.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;

/**
 * Tools for working with the filesystem
 */
public class FileUtils {
    /**
     * Reads a file, and returns it's contents as a UTF8 string
     * 
     * @param path File path
     * @return File contents
     * @throws IOException Thrown if the file does not exist
     */
    public static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    /**
     * Query WPIlib's filesystem for deploy path
     * 
     * @return File path of deploy folder
     */
    public static String getDeployPath() {
        return Filesystem.getDeployDirectory().getPath() + "/";
    }

    /**
     * Append a filename to the deploy path
     * 
     * @param filename File name
     * @return Deploy path + filename
     */
    public static String constructDeployPath(String filename) {
        return getDeployPath() + filename;
    }

    /**
     * Get the robot home directory, or simulation directory
     * 
     * @return Robot home directory
     */
    @Deprecated(since = "Aug 2020")
    public static String getHome() {

        // Check if the robot is real
        if (RobotBase.isReal()) {
            // Return the real env home directory path
            return "/home/lvuser/";
        } else {
            // Determine the simulated code directory
            return Filesystem.getLaunchDirectory().getPath() + "/simulated/home/lvuser/";
        }
    }
}