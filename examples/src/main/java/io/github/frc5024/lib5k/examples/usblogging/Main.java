package io.github.frc5024.lib5k.examples.usblogging;

import java.time.LocalDate;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.USBLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

/**
 * This is an extension of the "logging" example. Here, we will configure all
 * log messages to be reflected to a USB stick in the background
 */
public class Main extends TimedRobot {

    // Here, we get a logger instance to work with.
    // This is generally done at the top of every file that needs to do logging
    private RobotLogger logger = RobotLogger.getInstance();

    // We also will create a USBLogger object
    private USBLogger usbLogger;

    public static void main(String[] args) {
        // Start the robot code
        RobotBase.startRobot(Main::new);
    }

    public Main() {

        // In the constructor, if we need to log something, we must use a special flag.
        // This is just due to a quirk with the scheduler system.
        logger.log("Hello from the constructor", Level.kRobot);

        // We need a USBLogger to work with.
        // The log file will be saved on the robot's USB stick in a timestamped folder
        usbLogger = new USBLogger();

        // When reflecting to a USB, we need to tell the logger where to reflect
        logger.enableUSBLogging(usbLogger);

        // *From now on, all logs will be written both to the console, and to the USB*

        // It is very important to start the logger in the robot constructor (do not do
        // this anywhere else)
        // 0.02ms was chosen as the period because it matches the main robot thread, and
        // is the most reliable. but really anything can be used
        logger.start(0.02);

    }

    @Override
    public void autonomousInit() {
        // Since we are no longer in the constructor, we can log normally
        logger.log("Autonomous has started");

    }

    @Override
    public void teleopInit() {
        // Here, we are going to log the timestamp when the robot enters teleop
        logger.log("Teleop started %.2f seconds after robot boot", FPGAClock.getFPGASeconds());

    }

    @Override
    public void disabledInit() {
        // Here, we will log a warning. This message will both be written to the log,
        // and displayed in driverstation
        logger.log("Robot disabled", Level.kWarning);
    }
}