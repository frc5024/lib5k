package io.github.frc5024.lib5k.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.utils.FileManagement;

/**
 * The USBLogger is a class that is used to save a copy of all logs written to
 * {@link RobotLogger} to their own file. This saved file is located in
 * "robot.log" in the current session directory (see {@link FileManagement}).
 * 
 * To link a USBLogger object to RobotLogger, use
 * RobotLogger.getInstance().enableUSBLogging()
 */
public class USBLogger implements AutoCloseable {

    private Notifier m_thread;
    private ArrayList<String> m_messageBuffer = new ArrayList<>();
    private FileWriter m_file;

    @Deprecated(since = "July 2020", forRemoval = true)
    public USBLogger(String unused) {
        this();
    }

    /**
     * Create a USBLogger
     */
    public USBLogger() {

        // Create file writer
        try {
            m_file = FileManagement.createFileWriter("robot.log");
        } catch (IOException e) {
            RobotLogger.getInstance().log("Failed to create robot.log file!!", Level.kWarning);
        }

        // Start the thread
        m_thread = new Notifier(this::update);
        m_thread.startPeriodic(0.5);

    }

    /**
     * Write a line to the USB log
     * 
     * @param line Line to write
     */
    protected void writeln(String line) {
        m_messageBuffer.add(line);
    }

    private void update() {

        // Check if a USB storage device is connected to the RoboRIO
        boolean usbAttached = FileManagement.isUSBAttached();
        if (!usbAttached) {

            // Stop the logging by blocking the thread
            return;

        }

        // Write data buffer to logfile
        try {
            if (m_file != null) {
                for (String line : m_messageBuffer) {
                    m_file.append(String.format("%s%n", line));
                    m_file.flush();
                }
            }
        } catch (IOException e) {
            DriverStation.reportError("Failed to write message buffer to USB", true);
        } catch (ConcurrentModificationException e) {
            RobotLogger.getInstance().log("A CMOD occured");
        }

        // Clear the message buffer
        m_messageBuffer.clear();

    }

    @Override
    public void close() throws IOException {
        if (m_file != null) {
            m_file.close();
        }
    }
}