package io.github.frc5024.lib5k.examples.tbh_flywheel;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import io.github.frc5024.lib5k.examples.neo_flywheel.subsystems.Flywheel;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.USBLogger;

/**
 * This example robot program contains a simple TBH flywheel controller and
 * support for user input to control it. This file is build on top of the
 * "neo_flywheel" example
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
        usbLogger = new USBLogger();
        logger.enableUSBLogging(usbLogger);
        logger.start(0.02);

        // Here, we need to start the Flywheel subsystem
        Flywheel.getInstance().register();

    }

    @Override
    public void teleopInit() {
        logger.log("Teleop started");

        // In actual robot code, we would set the flywheel goal from another subsystem,
        // or a command. For this example, we can just set it to spin up when we start
        // teleop.
        Flywheel.getInstance().setGoalVelocity(3000.0);
    }

    @Override
    public void teleopPeriodic() {
        // Update the scheduler
        CommandScheduler.getInstance().run();
    }

    @Override
    public void disabledInit() {
        logger.log("Robot disabled");

        // Stop the flywheel
        Flywheel.getInstance().stop();
    }

    @Override
    public void disabledPeriodic() {
        // Update the scheduler
        CommandScheduler.getInstance().run();
    }
}