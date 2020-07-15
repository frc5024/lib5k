package io.github.frc5024.lib5k.csvlogging;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import io.github.frc5024.lib5k.logging.RobotLogger;

import edu.wpi.first.wpilibj.Notifier;

public class StatusLogger {
    private static StatusLogger instance;

    // Init time in seconds
    private static final double INIT_TIMEOUT_SECONDS = 5.0;

    // List of objects to log
    ArrayList<LoggingObject> objects = new ArrayList<>();

    // Thread
    private Notifier thread;
    private double bootTime;
    private boolean hasInitFile = false;

    // FIO
    private FileWriter writer;

    private StatusLogger() {
        // Set up thread
        thread = new Notifier(this::update);
        bootTime = System.currentTimeMillis() / 1000;
        

    }

    public static StatusLogger getInstance() {
        if (instance == null) {
            instance = new StatusLogger();
        }
        return instance;
    }

    /**
     * Start logging CSV data to a specific file
     * @param filepath File to write to
     */
    public void startLogging(String filepath) {

        // Create writer
        try{
            this.writer = new FileWriter(filepath, false);
        } catch (IOException e) {
            RobotLogger.getInstance().log("Failed to create FileWriter for %s", (Object) filepath);
            return;
        }

        // Start thread
        thread.startPeriodic(0.02);        
    }

    /**
     * Create a logging object. This will only work for the first 5 seconds after
     * the program starts to prevent CSV corruption during a match.
     * 
     * @param name   Unique object name
     * @param fields Names of all data fields
     * @return Logging object
     */
    public LoggingObject createLoggingObject(String name, String... fields) {
        // Handle calls after the timeout
        if ((System.currentTimeMillis() / 1000) - bootTime > INIT_TIMEOUT_SECONDS) {
            RobotLogger.getInstance().log(
                    "%s tried to create a logging object after %.2f seconds. It has been given a dummy object as not to cause an NPE.",
                    name, INIT_TIMEOUT_SECONDS);
            return new LoggingObject(name, fields);
        }

        RobotLogger.getInstance().log("%s has been added to the StatusLogger with %d fields", name, fields.length);

        // Create a logging object
        LoggingObject o = new LoggingObject(name, fields);

        // Add to internal list
        this.objects.add(o);
        return o;

    }

    private void update() {

        // Handle file setup
        if ((System.currentTimeMillis() / 1000) - bootTime > INIT_TIMEOUT_SECONDS && !hasInitFile) {

            // Get all object headers
            StringBuilder fileHeader = new StringBuilder();

            for (LoggingObject o : objects) {
                fileHeader.append(o.getHeader());
            }

            // Write file header to file
            try{
                writer.append(fileHeader + "\n");
                writer.flush();
            } catch (IOException e) {
                RobotLogger.getInstance().log("Failed to write file header");
            }

            // Set lock
            hasInitFile = true;
        }

        // Handle data logging
        if (hasInitFile) {

            // Get all object data
            StringBuilder row = new StringBuilder();

            for (LoggingObject o : objects) {
                for (Object dataPoint : o.getRow()) {
                    row.append(dataPoint.toString());
                    row.append(",");
                }
            }

            // Write row to file
            try{
                writer.append(row + "\n");
                writer.flush();
            } catch (IOException e) {
                
            }
        }

    }

}