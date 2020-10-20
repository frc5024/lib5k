package io.github.frc5024.lib5k.examples.statespace_flywheel;

import edu.wpi.first.wpilibj.RobotBase;
import io.github.frc5024.lib5k.autonomous.RobotProgram;
import io.github.frc5024.lib5k.examples.statespace_flywheel.subsystems.Flywheel;
import io.github.frc5024.lib5k.logging.USBLogger;

public class Main extends RobotProgram {

    public static void main(String[] args) {
        RobotBase.startRobot(Main::new);
    }

    public Main() {
        super(false, true);

        // Enable USBLogging
        logger.enableUSBLogging(new USBLogger());

        // Set up subsystems
        Flywheel.getInstance().register();

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
            logger.log("Stopping systems");
            Flywheel.getInstance().stop();
        }

    }

    @Override
    public void test(boolean init) {

    }

}