package frc.common.utils;

import java.util.ArrayList;
import edu.wpi.first.wpilibj.Notifier;

public class RobotLogger{
    private static RobotLogger instance = null;
    private Notifier notifier;
    ArrayList<String> periodic_buffer = new ArrayList<String>();
    
    /**
     * Log level
     * 
     * The kRobot level will immeadiatly push to the console, everything else is queued until the next notifier cycle
     */
    public enum Level {
        kRobot, kInfo, kWarning, kLibrary
    }

    private RobotLogger() {
        this.notifier = new Notifier(this::pushLogs);
    }

    /**
     * Start the periodic logger
     * 
     * @param period The logging notifier period time in seconds
     */
    public void start(double period){
        this.notifier.startPeriodic(period);
    }
    
    /**
     * Get a RobotLogger instance
     * 
     * @return The current RobotLogger
     */
    public static RobotLogger getInstance() {
        if (instance == null) {
            instance = new RobotLogger();
        }
        return instance;
    }
    
    /**
     * Log a message to netconsole with the INFO log level.
     * This message will not be logged immeadiatly, but instead through the notifier.
     * 
     * @param msg The messge to log
     */
    public void log(String msg) {
        this.log(msg, Level.kInfo);
    }

    /**
     * Logs a message to netconsole with a custom log level.
     * The kRobot level will immeadiatly push to the console, everything else is queued until the next notifier cycle
     * 
     * @param msg The message to log
     * @param log_level the Level to log the message at
     */
    public void log(String msg, Level log_level) {
        String display_string = toString(msg, log_level);

        // If the log level is kRobot, just print to netconsole, then return
        if (log_level == Level.kRobot) {
            System.out.println(display_string);
            return;
        }

        // Add log to the periodic_buffer
        this.periodic_buffer.add(display_string);

    }

    /**
     * Push all queued messages to netocnsole, the clear the buffer
     */
    private void pushLogs() {
        for (String x : this.periodic_buffer){
            System.out.println(x);
        }
        this.periodic_buffer = new ArrayList<String>();
    }

    /**
     * Convert a message and Level to a string
     * 
     * @param msg The message
     * @param log_level The Level to log at
     * 
     * @return The formatted output string
     */
    private String toString(String msg, Level log_level) {
        String level_str;

        // Turn enum level into string
        switch (log_level) {
        case kInfo:
            level_str = "INFO: ";
            break;
        case kWarning:
            level_str = "WARNING: ";
            break;
        case kRobot:
            level_str = "ROBOT: ";
            break;
        case kLibrary:
            level_str = "LIBRARY: ";
            break;
        default:
            level_str = "UNK: ";
            break;
        }
        
        return level_str + msg;
    }
}