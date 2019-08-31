package frc.lib5k.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import edu.wpi.first.wpilibj.Filesystem;

/**
 * Tools for working with the filesystem
 */
public class FileUtils {
    /**
     * Reads a file, and returns it's contents as a UTF8 string
     * 
     * @param path File path
     * @return File contents
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
}