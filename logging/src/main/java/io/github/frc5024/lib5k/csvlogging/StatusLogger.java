package io.github.frc5024.lib5k.csvlogging;

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

    private StatusLogger() {
        // Set up thread
        thread = new Notifier(this::update);
        bootTime = System.currentTimeMillis() / 1000;
        thread.startPeriodic(0.02);

    }

    public static StatusLogger getInstance() {
        if (instance == null) {
            instance = new StatusLogger();
        }
        return instance;
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
                    "%s tried to create a logging object after %.2f seconds. This will not work. Worst case, try allowing a longer timeout in StatusLogger.java",
                    name, INIT_TIMEOUT_SECONDS);
            return null;
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
            // TODO: Write file

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
            // TODO: Write file
        }

    }

}