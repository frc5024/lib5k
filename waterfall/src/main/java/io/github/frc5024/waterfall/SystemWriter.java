package io.github.frc5024.waterfall;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * The SystemWriter class is to be used by single-instance objects (like robot
 * subsystems) to write continuous streams of data out to a file.
 */
public class SystemWriter {

    // Internal data storage and tracking
    private HashMap<String, Object> internalMap = new HashMap<>();
    private String[] keys;

    // File
    private boolean hasWrittenFile = false;
    private Path filePath;
    private FileWriter writer;

    /**
     * Create a SystemWriter
     * 
     * @param filePath Path to output CSV
     * @param keys     All datapoint keys
     * @throws IOException
     */
    public SystemWriter(Path filePath, String... keys) throws IOException {

        // Save keys
        this.keys = keys;

        // Set up file
        this.filePath = filePath;
        this.writer = new FileWriter(filePath.toFile());
    }

    /**
     * Access the internal hashmap to add data. (Don't forget to call writeOut() periodically)
     * @return Reference to internal map
     */
    public HashMap<String, Object> accessMap() {
        return this.internalMap;
    }

    /**
     * Write 1 row of data to output file
     * @param timestamp Current time
     */
    public void writeOut(double timestamp) {

        // String builder for output
        StringBuilder sb = new StringBuilder();

        // Add each datapoint to the builder
        for (String key : this.keys) {
            sb.append(this.internalMap.get(key).toString().replace(',', '_'));
            sb.append((','));
        }

        // If the file has not been written to yet, add a header to it
        if (!this.hasWrittenFile) {
            this.hasWrittenFile = true;

            // Build header
            StringBuilder headerBuilder = new StringBuilder();
            for (String key : this.keys) {
                sb.append(key.replace(',', '_'));
                sb.append((','));
            }

            // Write header
            try {
                this.writer.append(headerBuilder.toString());
            } catch (IOException e) {
                System.out.println("Failed to write header to file: " + this.filePath.toString());
                e.printStackTrace();
            }

        }

        // Write data row
        try {
            this.writer.append(sb.toString());
        } catch (IOException e) {
            System.out.println("Failed to write header to file: " + this.filePath.toString());
            e.printStackTrace();
        }

    }

}