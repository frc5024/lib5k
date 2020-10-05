package frc.common.tasks;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

import frc.common.utils.RobotLogger;
import frc.common.utils.RobotLogger.Level;
import frc.common.network.ConnectionMonitor;

/**
 * Back up /home/lvuser/FRCUserProgram.log to a USB stick when each match ends
 * 
 * WARNING: Unfinished
 */
public class RiologBackupTask {
    private static RiologBackupTask instance = null;

    DriverStation ds;
    ConnectionMonitor net_monitor;
    Notifier notifier;
    RobotLogger logger;

    private boolean robot_enabled_state = false;
    private boolean last_robot_enabled_state = false;


    private RiologBackupTask() {
        // Allow this class to read directly from the driverstation
        this.ds = DriverStation.getInstance();

        // Allow this class to check if the robot is connected to a driverstation
        this.net_monitor = ConnectionMonitor.getInstance();

        // Set up a new notifier to run the watch() method
        this.notifier = new Notifier(this::watch);
        this.logger = RobotLogger.getInstance();
    }

    /**
     * Get the current RiologBackupTask instance
     * 
     * @return Current instance
     */
    public static RiologBackupTask getInstance() {
        if (instance == null) {
            instance = new RiologBackupTask();
        }

        return instance;
    }

    /**
     * Start the internal notifier
     * 
     * @param period Loop time
     */
    public void start(double period) {
        // Start the notifier
        notifier.startPeriodic(period);
        logger.log("[RIOlog Backup] Started backing up log files to external storage", Level.kRobot);
    }

    /**
     * Stop the internal notifier
     */
    public void stop() {
        notifier.stop();
        logger.log("[RIOlog Backup] Stopped backing up log files", Level.kRobot);
    }

    
    private void watch() {
        this.robot_enabled_state = ds.isEnabled();

        // If the robot just got disabled, but did not loose connection:
        if (robot_enabled_state != last_robot_enabled_state && robot_enabled_state == false
                && net_monitor.isConnected()) {

            // Create a backup with a timestanp

        }

        this.last_robot_enabled_state = robot_enabled_state;
    }
    
    private void backup(String file, String date, double time) {
        logger.log("[RIOlog Backup] Backing up log to: RobotLog-" + date + "-" + time);
    }
    
}