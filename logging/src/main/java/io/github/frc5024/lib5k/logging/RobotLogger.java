package io.github.frc5024.lib5k.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.utils.annotations.FieldTested;
import io.github.frc5024.lib5k.utils.annotations.Tested;
import io.github.frc5024.lib5k.utils.annotations.TestedInSimulation;

/**
 * A threaded logger for use by all robot functions
 */
@FieldTested(year = 2019)
@Tested
@TestedInSimulation
public class RobotLogger {
    private static RobotLogger instance = null;
    private Notifier notifier;
    ArrayList<String> periodic_buffer = new ArrayList<String>();
    private USBLogger m_usbLogger;
    private double bootTime;

    // Simulation logfile
    private FileWriter simWriter;

    /**
     * Log level
     * 
     * The kRobot level will immediately push to the console, everything else is
     * queued until the next notifier cycle
     */
    public enum Level {
        kRobot("INFO"), kInfo("INFO"), kWarning("WARNING"), kDebug("DEBUG"), kLibrary("INFO");

        public String name;

        /**
         * Create a log level
         * 
         * @param name String name
         */
        private Level(String name) {
            this.name = name;
        }
    }

    /**
     * Create the RobotLogger instance
     */
    private RobotLogger() {
        this.notifier = new Notifier(this::pushLogs);

        // set boot time
        this.bootTime = (double) System.currentTimeMillis() / 1000.0;

        // Try to load sim logger
        if (RobotBase.isSimulation()) {
            try {
                simWriter = new FileWriter("./FRC_UserProgram.log");
                simWriter.write("");
            } catch (IOException e) {
                System.out.println("Not writing to simulation logfile because of error");
                e.printStackTrace();
            }
        }

    }

    /**
     * Enable logging to a USB
     * 
     * @param logger USB logger object
     */
    public void enableUSBLogging(USBLogger logger) {
        m_usbLogger = logger;
    }

    /**
     * Start the periodic logger
     * 
     * @param period The logging notifier period time in seconds
     */
    public void start(double period) {
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
     * Write a log message to the logfile and message buffer.
     * 
     * @param msg  Log message (String.format style)
     * @param args Format arguments
     */
    public void log(String msg, Object... args) {
        // Get stack trace
        StackTraceElement lastMethod = Thread.currentThread().getStackTrace()[2];

        // Log
        log(lastMethod, Level.kInfo, msg, args);
    }

    /**
     * Write a log message to the logfile and message buffer.
     * 
     * @param msg  Log message (String.format style)
     * @param lvl  Log level
     * @param args Format arguments
     */
    public void log(String msg, Level lvl, Object... args) {
        // Get stack trace
        StackTraceElement lastMethod = Thread.currentThread().getStackTrace()[2];

        // Log
        log(lastMethod, lvl, msg, args);
    }

    /**
     * Write a log message to the logfile and message buffer. This is deprecated,
     * and acts as a binding for old programs.
     * 
     * @param component Calling component name
     * @param msg       Log message
     */
    @Deprecated(since = "July 2020", forRemoval = false)
    public void log(String component, String msg) {

        // Get stack trace
        StackTraceElement lastMethod = Thread.currentThread().getStackTrace()[2];

        // Log
        log(lastMethod, Level.kInfo, msg);
    }

    /**
     * Write a log message to the logfile and message buffer. This is deprecated,
     * and acts as a binding for old programs.
     * 
     * @param component Calling component name
     * @param msg       Log message
     * @param log_level Log level
     */
    @Deprecated(since = "July 2020", forRemoval = false)
    public void log(String component, String msg, Level log_level) {

        // Get stack trace
        StackTraceElement lastMethod = Thread.currentThread().getStackTrace()[2];

        // Log
        log(lastMethod, log_level, msg);
    }

    /**
     * Write a log message to the logfile and message buffer
     * 
     * @param lastMethod Stack trace to calling method
     * @param lvl        Log level
     * @param messageF   String.format style string / message
     * @param args       Any format arguments
     */
    private void log(StackTraceElement lastMethod, Level lvl, String messageF, Object... args) {

        // Build message
        String message = String.format(messageF, args);

        // Get the caller's package name
        String packageName = getPackageName(lastMethod);

        // Get the current system time
        double time = (double) System.currentTimeMillis() / 1000.0;

        // Determine time-since-boot
        double tsb = time - this.bootTime;

        // Build log string
        String log = String.format("%s at %.2fs: %s -> %s", lvl.name, tsb, packageName, message);

        // If the log is robot level, push to console
        if (lvl.equals(Level.kRobot)) {

            // Write log NOW
            System.out.println(log);

            // Try to reflect to USB
            if (m_usbLogger != null) {
                m_usbLogger.writeln(log);
            }

        } else {
            // Add log to buffer
            this.periodic_buffer.add(log);
        }

        // If simulation, write to sim file
        if (RobotBase.isSimulation() && simWriter != null) {
            try {
                simWriter.append(log + "\n");
                simWriter.flush();
            } catch (IOException e) {
                System.out.println("Failed to reflect sim log");
            }
        }

    }

    /**
     * Gets a friendly package name for a StackTraceElement
     * 
     * @param element Element to get name for
     * @return Friendly Name
     */
    protected static String getPackageName(StackTraceElement element) {

        // Build the package and method string
        String fullPackageName = element.getClassName();
        String[] packageSegments = fullPackageName.split("[.]");
        StringBuilder packageNameBuilder = new StringBuilder();

        // Handle the package name being too short
        if (packageSegments.length <= 3) {

            // Just build the full package name
            packageNameBuilder.append(fullPackageName);

        } else {
            // The idea here is to turn a package name that might look like:
            // io.github.frc5024.y2020.darthraider.commands.autonomous.actions.cells.SetShooterOutput
            // Into one that looks like:
            // io...cells.SetShooterOutput

            // Append the root package
            packageNameBuilder.append(packageSegments[0]);

            // Append the seperator
            packageNameBuilder.append("...");

            // Append the class's parent package
            packageNameBuilder.append(packageSegments[packageSegments.length - 2]);
            packageNameBuilder.append(".");

            // Append the class name
            packageNameBuilder.append(packageSegments[packageSegments.length - 1]);
        }

        // Get the method name
        String methodName = element.getMethodName();

        return String.format("%s::%s()", packageNameBuilder.toString(), methodName.toString());

    }

    /**
     * Manually flush the logs (only use this in unit tests)
     */
    public void flush(){
        pushLogs();
    }

    /**
     * Push all queued messages to netconsole, the clear the buffer
     */
    private void pushLogs() {
        try {
            for (String x : this.periodic_buffer) {

                System.out.println(x);

                // Check if we should log to USB
                if (m_usbLogger != null) {
                    m_usbLogger.writeln(x);
                }
            }
            periodic_buffer.clear();
        } catch (Exception e) {
            System.out.println("Tried to push concurrently");
        }

    }

}