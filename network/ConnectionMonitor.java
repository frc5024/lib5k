package frc.common.network;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

import frc.common.utils.RobotLogger;
import frc.common.utils.RobotLogger.Level;

/**
 * Monitor the connection status to the driversttion
 * 
 * Inspired by 254's 2017 codebase:
 * https://github.com/Team254/FRC-2017-Public/blob/master/src/com/team254/frc2017/subsystems/ConnectionMonitor.java
 */
public class ConnectionMonitor {
    private static ConnectionMonitor instance = null;

    DriverStation ds;
    Notifier notifier;
    RobotLogger logger;

    private double timeout = 0.5;
    private double timestamp = 0.0;
    private boolean is_connected, just_connected, just_disconnected = false;
    private boolean last_connection_state = true; // Set to true to force a log to be pushed on boot


    private ConnectionMonitor() {
        // Allow this class to read directly from the driverstation
        this.ds = DriverStation.getInstance();

        // Set up a new notifier to run the update() method
        this.notifier = new Notifier(this::update);
        this.logger = RobotLogger.getInstance();
    }

    /**
     * Get the current ConnectionMonitor instance
     * 
     * @return Current instance
     */
    public static ConnectionMonitor getInstance() {
        if (instance == null) {
            instance = new ConnectionMonitor();
        }

        return instance;
    }

    /**
     * Start the internal notifier
     * 
     * @param period Loop time
     */
    public void start(double period) {
        // Get the current timestamp
        this.timestamp = Timer.getFPGATimestamp();

        // Start the notifier
        notifier.startPeriodic(period);
        logger.log("[ConnectionMonitor] Started monitoring driverstation connection", Level.kRobot);
    }

    /**
     * Stop the internal notifier
     */
    public void stop() {
        notifier.stop();
        logger.log("[ConnectionMonitor] Stopped monitoring driverstation connection", Level.kRobot);
    }

    /**
     * Updates connection status
     */
    private void update() {
        ds.waitForData(timeout);

        // Check if the wait timed out
        if ((Timer.getFPGATimestamp() - timestamp) > timeout) {
            // Set connection state to false
            this.is_connected = false;
        } else {
            this.timestamp = Timer.getFPGATimestamp();
            this.is_connected = true;
        }

        // If this is a new state, push a message to the log
        if (is_connected != last_connection_state) {
            String connection_action = (is_connected) ? "regained" : "lost";

            // set the "justs"
            if (is_connected) {
                just_connected = true;
            } else {
                just_disconnected = true;
            }

            logger.log("[ConnectionMonitor] The robot has " + connection_action + " connection!", Level.kRobot);
        }

        // Set the previous state
        this.last_connection_state = is_connected;
    }
    
    /**
     * Get the current connection status
     * 
     * @return Is the robot connected to a driverstation
     */
    public boolean isConnected() {
        return is_connected;
    }
    
    /**
     * Has the robot connected since the last time this function was called?
     * (sticky)
     */
    public boolean justConnected() {
        boolean output = just_connected;
        just_connected = false;
        return output;
    }

    /**
     * Has the robot disconnected since the last time this function was called?
     * (sticky)
     */
    public boolean justDisconnected() {
        boolean output = just_disconnected;
        just_disconnected = false;
        return output;
    }
}