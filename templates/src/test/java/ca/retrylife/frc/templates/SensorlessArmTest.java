package ca.retrylife.frc.templates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.retrylife.ewmath.MathUtils;
import io.github.frc5024.lib5k.hardware.generic.motors.MockSpeedController;
import io.github.frc5024.lib5k.hardware.generic.sensors.MockDigitalInput;
import io.github.frc5024.lib5k.logging.RobotLogger;

public class SensorlessArmTest {

    // System components & settings
    private final static MockSpeedController motor = new MockSpeedController();
    private final static MockDigitalInput lowerLimit = new MockDigitalInput(0);
    private final static MockDigitalInput upperLimit = new MockDigitalInput(1);
    private final static double speed = 0.5;
    private final static double FF = 0.25;

    @Test
    public void testDownwardMovement() {

        // Reset all I/O
        motor.set(0.0);
        lowerLimit.set(false);
        upperLimit.set(false);

        // Create a SensorlessArm
        SensorlessArm arm = new SensorlessArm(motor, lowerLimit, upperLimit, speed, FF);
        RobotLogger.getInstance().flush();

        // Tell the arm to go down
        arm.setDesiredPosition(SensorlessArm.SystemState.kLowered);
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Check that the motor is moving down at the correct speed
        assertEquals("Arm speed", speed * -1, motor.get(), MathUtils.kVerySmallNumber);

        // Trigger the top sensor
        upperLimit.set(true);
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Make sure nothing changed (sensor has no affect)
        assertEquals("Arm speed", speed * -1, motor.get(), MathUtils.kVerySmallNumber);

        // Trigger the bottom sensor
        lowerLimit.set(true);

        // It takes 40ms for one state to call another
        arm.periodic();
        RobotLogger.getInstance().flush();
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Make sure the arm is stopped, and the position is correct
        assertEquals("Arm speed", 0.0, motor.get(), MathUtils.kVerySmallNumber);
        assertEquals("Arm position", SensorlessArm.SystemState.kStopped, arm.getLastKnownPosition());

    }

    @Test
    public void testUpwardMovement() {

        // Reset all I/O
        motor.set(0.0);
        lowerLimit.set(false);
        upperLimit.set(false);

        // Create a SensorlessArm
        SensorlessArm arm = new SensorlessArm(motor, lowerLimit, upperLimit, speed, FF);
        RobotLogger.getInstance().flush();

        // Tell the arm to go up
        arm.setDesiredPosition(SensorlessArm.SystemState.kRaised);
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Check that the motor is moving up at the correct speed
        assertEquals("Arm speed", speed + FF, motor.get(), MathUtils.kVerySmallNumber);

        // Trigger the bottom sensor
        lowerLimit.set(true);
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Make sure nothing changed (sensor has no affect)
        assertEquals("Arm speed", speed + FF, motor.get(), MathUtils.kVerySmallNumber);

        // Trigger the upper sensor
        upperLimit.set(true);

        // It takes 40ms for one state to call another
        arm.periodic();
        RobotLogger.getInstance().flush();
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Make sure the arm is stopped, and the position is correct
        assertEquals("Arm speed", 0.0, motor.get(), MathUtils.kVerySmallNumber);
        assertEquals("Arm position", SensorlessArm.SystemState.kStopped, arm.getLastKnownPosition());

    }

    @Test
    public void testDriftingDownFromStop() {

        // Reset all I/O
        motor.set(0.0);
        lowerLimit.set(false);
        upperLimit.set(false);

        // Create a SensorlessArm
        SensorlessArm arm = new SensorlessArm(motor, lowerLimit, upperLimit, speed, FF);
        RobotLogger.getInstance().flush();

        // Tell the arm to go up
        arm.setDesiredPosition(SensorlessArm.SystemState.kRaised);

        // Set the top sensor
        upperLimit.set(true);

        // Let the state machine get to the stopped & holding up state
        arm.periodic();
        RobotLogger.getInstance().flush();
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Make sure the arm is stopped, and the position is correct
        assertEquals("Arm speed", 0.0, motor.get(), MathUtils.kVerySmallNumber);
        assertEquals("Arm position", SensorlessArm.SystemState.kStopped, arm.getLastKnownPosition());

        // Turn off the top sensor
        upperLimit.set(false);
        arm.periodic();
        RobotLogger.getInstance().flush();
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Check that the arm is trying to get back to the top again
        assertEquals("Arm speed", speed + FF, motor.get(), MathUtils.kVerySmallNumber);

    }

    @Test
    public void testDriftingUpFromStop() {
        // Reset all I/O
        motor.set(0.0);
        lowerLimit.set(false);
        upperLimit.set(false);

        // Create a SensorlessArm
        SensorlessArm arm = new SensorlessArm(motor, lowerLimit, upperLimit, speed, FF);
        RobotLogger.getInstance().flush();

        // Tell the arm to go down
        arm.setDesiredPosition(SensorlessArm.SystemState.kLowered);

        // Set the bottom sensor
        lowerLimit.set(true);

        // Let the state machine get to the stopped & holding up state
        arm.periodic();
        RobotLogger.getInstance().flush();
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Make sure the arm is stopped, and the position is correct
        assertEquals("Arm speed", 0.0, motor.get(), MathUtils.kVerySmallNumber);
        assertEquals("Arm position", SensorlessArm.SystemState.kStopped, arm.getLastKnownPosition());

        // Turn off the bottom sensor
        lowerLimit.set(false);
        arm.periodic();
        RobotLogger.getInstance().flush();
        arm.periodic();
        RobotLogger.getInstance().flush();

        // Check that the arm is trying to get back to the bottom again
        assertEquals("Arm speed", speed * -1, motor.get(), MathUtils.kVerySmallNumber);
    }

}
