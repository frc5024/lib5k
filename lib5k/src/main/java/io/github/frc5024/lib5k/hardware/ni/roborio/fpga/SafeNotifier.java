package io.github.frc5024.lib5k.hardware.ni.roborio.fpga;

import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;

import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.utils.FileManagement;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * SafeNotifier is a wrapper around the system Notifier with some extra error
 * handling built in.
 */
public class SafeNotifier extends Notifier {

    // Custom period
    private double period;

    // Logger
    // This must be static so we can access it from the wrapper
    private static RobotLogger logger = RobotLogger.getInstance();

    // For backend access
    private String name;
    private Runnable underlyingRunnable;

    /**
     * Create a SafeNotifier with a period of 20ms
     * 
     * @param name   Thread name
     * @param action Action to be run
     */
    public SafeNotifier(String name, Runnable action) {
        this(name, 0.20, action);
    }

    /**
     * Create a SafeNotifier with a custom period
     * 
     * @param name          Thread name
     * @param periodSeconds Period time in seconds
     * @param action        Action to be run
     */
    public SafeNotifier(String name, double periodSeconds, Runnable action) {

        // Set up the notifier to run the action
        super(() -> {
            // Run the action with a wrapper around it
            SafeNotifier.safetyWrapper(name, action);
        });
        this.underlyingRunnable = () -> {
            SafeNotifier.safetyWrapper(name, action);
        };

        // Name the notifier
        setName(name);
        this.name = name;

        // Set the period
        this.period = periodSeconds;
    }

    /**
     * ADVANCED USE ONLY. Get the underlying thread used by the notifier. This can
     * undermine the alarm system if needed.
     * 
     * @return Underlying thread
     */
    protected Thread getUnderlyingThread() {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(this.name))
                return t;
        }
        return null;
    }

    /**
     * ADVANCED USE ONLY. Get the underlying Runnable
     * 
     * @return Underlying runnable
     */
    protected Runnable getUnderlyingRunnable() {
        return underlyingRunnable;
    }

    /**
     * Start the notifier with a pre-configured period. This will be 20ms by
     * default, or whatever was passed into the constructor, or whatever was passed
     * in to the last call to startPeriodic()
     */
    public void start() {
        startPeriodic(this.period);
    }

    @Override
    public void startPeriodic(double period) {
        // Override the internal period
        this.period = period;
        super.startPeriodic(period);
    }

    /**
     * Wrapper that gets called and delegates tasks
     * 
     * @param name   Thread name
     * @param action Action to run
     */
    private static void safetyWrapper(String name, Runnable action) {
        // Wrap the runnable with an error handler
        try {
            action.run();
        } catch (Throwable t) {
            // Handle possible errors
            String errorMessage = String.format("SafeNotifier for %s caught the following error: %s", name,
                    t.getMessage());
            logger.log(errorMessage, Level.kWarning);
            DriverStation.reportError(errorMessage, t.getStackTrace());

            // Attempt to write the error and stack trace to the session folder
            double currentTime = FPGAClock.getFPGASeconds();

            try {

                // Get a session file
                String fileName = String.format("SafeNotifier_%s_StackTrace_%d.txt", name, (int) currentTime);
                FileWriter stackTraceWriter = FileManagement.createFileWriter(fileName);

                // Write the stack trace
                stackTraceWriter.append(String.format("Stack trace:%n"));
                stackTraceWriter.append(t.getMessage() + "\n");
                stackTraceWriter.append(t.getLocalizedMessage() + "\n");
                stackTraceWriter.append(ExceptionUtils.getStackTrace(t));

                // Inform the user what happened
                stackTraceWriter.close();
                logger.log(String.format("Saved stack trace in the current session folder as: %s", fileName));

            } catch (IOException e) {
                logger.log("Failed to save stack trace to the session directory", Level.kWarning);
            }

            // Re-throw the error
            throw t;
        }
    }

}