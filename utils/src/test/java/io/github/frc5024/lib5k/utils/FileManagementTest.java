package io.github.frc5024.lib5k.utils;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class FileManagementTest {

    @Test
    public void testSessionPathResolution() {

        // Make sure the path isn't null
        assert FileManagement.getSessionDirectoryPath() != null;
    }

    @Test
    public void testSessionFileWriterOpening() throws IOException {
        
        // Get a filewriter
        FileWriter writer = FileManagement.createFileWriter("UnitTestWriter.txt");

        // Write a line
        writer.write("Hello from the unit test world!");
        writer.close();
    }

}