package io.github.frc5024.lib5k.logging;

import java.io.FileWriter;
import java.io.IOException;

import io.github.frc5024.lib5k.utils.FileManagement;

/**
 * CSVFile is a class designed for one-time use. Creating an object will open a
 * new session file, and write the CSV headers. You can then push rows to the
 * file, and finally close it.
 */
public class CSVFile implements AutoCloseable {

    // The underlying file
    private FileWriter writer;

    // When this is true, this object can no longer be used
    private boolean isClosed = false;

    /***
     * Create a new CSVFile
     * 
     * @param filename      File name
     * @param columnHeaders The names of all the columns
     * @throws IOException Thrown if there is an issue opening the file
     */
    public CSVFile(String filename, String... columnHeaders) throws IOException {
        this(filename, true, columnHeaders);
    }

    /***
     * Create a new CSVFile
     * 
     * @param filename                  File name
     * @param appendTimestampToFileName Should the current timestamp be appended to
     *                                  the end of this file?
     * @param columnHeaders             The names of all the columns
     * @throws IOException Thrown if there is an issue opening the file
     */
    public CSVFile(String filename, boolean appendTimestampToFileName, String... columnHeaders) throws IOException {

        // Build a filename
        String generatedFileName;
        if (appendTimestampToFileName) {
            generatedFileName = String.format("%s_%s.csv", filename, Long.toString(System.currentTimeMillis()));
        } else {
            generatedFileName = String.format("%s.csv", filename);
        }

        // Open a new file
        this.writer = FileManagement.createFileWriter(generatedFileName);

        // Write the header
        writeRow(columnHeaders);
    }

    /**
     * Write a row to the file
     * 
     * @param values Row of values
     * @throws IOException Thrown if there is an issue writing to the file
     */
    public void writeRow(String... values) throws IOException {
        writeRow((Object[]) values);
    }

    /**
     * Write a row to the file
     * 
     * @param values Row of values
     * @throws IOException Thrown if there is an issue writing to the file
     */
    public void writeRow(Object... values) throws IOException {

        // Ensure writing is possible
        if (isClosed) {
            throw new IllegalStateException("Cannot write to a closed file");
        }

        // Build the values into a row
        StringBuilder rowBuilder = new StringBuilder();
        for (Object value : values) {
            rowBuilder.append(value.toString());
            rowBuilder.append(",");
        }

        // Write the row
        rowBuilder.append("\n");
        writer.append(rowBuilder.toString());

    }

    /**
     * Close the file
     * @throws IOException Thrown if there is an issue closing the file
     */
    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
        isClosed = true;
    }

}