package io.github.frc5024.lib5k.examples.gyroscopes;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import io.github.frc5024.lib5k.autonomous.RobotProgram;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.IGyroscope;
import io.github.frc5024.lib5k.hardware.ctre.sensors.ExtendedPigeonIMU;
import io.github.frc5024.lib5k.hardware.generic.gyroscopes.ADGyro;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

/**
 * This example shows how to interface with a gyroscope, and how easy it is to
 * hot-swap gyros
 */
public class Main extends RobotProgram {

    // NOTE: RobotProgram comes with it's own logger instance. We don't need to
    // create another.

    // We will create a gyroscope
    private IGyroscope gyro;

    public static void main(String[] args) {
        RobotBase.startRobot(Main::new);
    }

    public Main() {
        // Here, we configure the robot program with the default settings:
        // - Disable scheduler in test mode
        // - Kill autonomous when teleop starts
        // - Use the "main" tab in {@link Shuffleboard}
        super(false, true, Shuffleboard.getTab("Main"));

        // * NOTE: you can un-comment any of these lines to use the respective gyro
        // * type. They all share the same common interface

        // Creates a NavX gyro connected to the RoboRIO MXP port
        gyro = new NavX(Port.kMXP);

        // Creates an ADXR gyro connected to onboard SPI CS0
        // gyro = new ADGyro();

        // Creates a PigeonIMU gyro connected on the can bus with id 0
        // gyro = new ExtendedPigeonIMU(0);
    }

    @Override
    public void autonomous(boolean init) {

    }

    @Override
    public void teleop(boolean init) {

        // Print out gyro data
        logger.log("Angle: %.2f, Rate: %.2f, Heading: %.2f", gyro.getAngle(), gyro.getRate(), gyro.getHeading());

    }

    @Override
    public void disabled(boolean init) {

        // Calibrate gyro if needed
        // This process is quick, but must happen while the robot is sitting still.
        // We generally don't do this for most gyros
        if (init && !gyro.getCalibrated()) {
            logger.log("Gyroscope is calibrating. Don't interfere with the robot!", Level.kWarning);
            gyro.calibrate();
        }

    }

    @Override
    public void test(boolean init) {

    }

}