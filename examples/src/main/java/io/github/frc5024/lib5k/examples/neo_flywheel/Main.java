package io.github.frc5024.lib5k.examples.neo_flywheel;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

/**
 * This example robot program contains a simple flywheel controller and support for user input to control it. This file is build on top of the "usblogging" example
 */
public class Main extends TimedRobot {

    // Loggers
    private RobotLogger logger = RobotLogger.getInstance();
    private USBLogger usbLogger;

    public static void main(String[] args) {
        // Start the robot code
        RobotBase.startRobot(Main::new);
    }

    public Main() {

        // Set up logging
        usbLogger = new USBLogger("RobotLogs-2020/live");
        logger.enableUSBLogging(usbLogger);
        logger.start(0.02);

        // Here, we need to start the Flywheel subsystem
        

    }

    @Override
    public void teleopInit() {
        logger.log("Teleop started");

    }

    @Override
    public void disabledInit() {
        logger.log("Robot disabled");
    }
}