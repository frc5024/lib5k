package io.github.frc5024.lib5k.examples.autonomous_framework;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import io.github.frc5024.lib5k.autonomous.RobotProgram;
import io.github.frc5024.lib5k.examples.autonomous_framework.autonomous.ActionA;
import io.github.frc5024.lib5k.examples.autonomous_framework.autonomous.ActionB;

/**
 * This example showcases how to add autonomous sequences to the robot. All
 * possible options will automatically be displayed inside ShuffleBoard
 */
public class Main extends RobotProgram {
    // NOTE: RobotProgram comes with it's own logger instance. We don't need to
    // create another.

    public static void main(String[] args) {
        RobotBase.startRobot(Main::new);
    }

    public Main() {
        // Here, we configure the robot program with the default settings:
        // - Disable scheduler in test mode
        // - Kill autonomous when teleop starts
        // - Use the "main" tab in {@link Shuffleboard}
        super(false, true, Shuffleboard.getTab("Main"));

        // Here, we add our autonomous sequences
        setDefaultAutonomous(new ActionA());
        addAutonomous(new ActionB());
    }

    @Override
    public void autonomous(boolean init) {

        // Unlike TimedRobot, RobotProgram uses an "init" variable to run once at the
        // start of a mode
        if (init) {
            logger.log("Robot", "Autonomous started");
        }

        // Running the scheduler is also handled in the background

    }

    @Override
    public void teleop(boolean init) {

        // Unlike TimedRobot, RobotProgram uses an "init" variable to run once at the
        // start of a mode
        if (init) {
            logger.log("Robot", "Teleop started");
        }

        // Running the scheduler is also handled in the background

    }

    @Override
    public void disabled(boolean init) {

        // Unlike TimedRobot, RobotProgram uses an "init" variable to run once at the
        // start of a mode
        if (init) {
            logger.log("Robot", "Robot disabled");
        }

        // Running the scheduler is also handled in the background

    }

    @Override
    public void test(boolean init) {

        // Unlike TimedRobot, RobotProgram uses an "init" variable to run once at the
        // start of a mode
        if (init) {
            logger.log("Robot", "In test mode");
        }

        // Running the scheduler is also handled in the background

    }

}