package io.github.frc5024.lib5k.examples.drivebase_simulation;

import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.autonomous.RobotProgram;
import io.github.frc5024.lib5k.examples.drivebase_simulation.commands.DriveCommand;
import io.github.frc5024.lib5k.examples.drivebase_simulation.subsystems.DriveTrain;
import io.github.frc5024.lib5k.logging.USBLogger;

public class Main extends RobotProgram {

    // Commands
    private DriveCommand driveCommand;

    public static void main(String[] args) {
        RobotBase.startRobot(Main::new);
    }

    public Main() {
        super(false, true);

        // Enable USBLogging
        logger.enableUSBLogging(new USBLogger());

        // Set up subsystems
        DriveTrain.getInstance().register();

        // Set up commands
        driveCommand = new DriveCommand();
        DriveTrain.getInstance().setDefaultCommand(driveCommand);
    }

    @Override
    public void periodic(boolean init) {

    }

    @Override
    public void autonomous(boolean init) {

    }

    @Override
    public void teleop(boolean init) {

    }

    @Override
    public void disabled(boolean init) {
        if (init) {
            logger.log("Stopping drivetrain");
            DriveTrain.getInstance().stop();
        }

    }

    @Override
    public void test(boolean init) {

    }

}