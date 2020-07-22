package io.github.frc5024.lib5k.examples.autonomous_path_following;

import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.autonomous.RobotProgram;
import io.github.frc5024.lib5k.examples.autonomous_path_following.commands.DriveCommand;
import io.github.frc5024.lib5k.examples.autonomous_path_following.sequences.ForwardOneMeter;
import io.github.frc5024.lib5k.examples.autonomous_path_following.subsystems.DriveTrain;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
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

        // Autonomous
        logger.log("Adding autonomous paths", Level.kRobot);
        addAutonomous(new ForwardOneMeter());

        // Set up subsystems
        DriveTrain.getInstance().register();

        // Set up commands
        driveCommand = new DriveCommand();
        DriveTrain.getInstance().setDefaultCommand(driveCommand);
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