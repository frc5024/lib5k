package io.github.frc5024.lib5k.hardware.common.sensors.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.math.system.plant.DCMotor;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.hardware.generic.motors.MockSpeedController;
import io.github.frc5024.lib5k.utils.TimeScale;

public class EncoderSimUtilTest {

    @Test
    public void testNormalFunction() {

        // Controller
        MockSpeedController controller = new MockSpeedController();

        // Encoder sim
        EncoderSimUtil sim = new EncoderSimUtil("TestEncoder", 0, 1440, controller, 8.45,
                new DCBrushedMotor(DCMotor.getCIM(1)).getFreeSpeedRPM(), 0.12);

        // Set a custom time scale for this test
        TimeScale.globallyOverrideCalculationOutput(0.02);

        // Ensure the encoder increments when moving forwards
        controller.stopMotor();
        sim.reset();
        controller.set(1.0);
        sim.update();
        sim.update();
        assertTrue("Encoder incremented", sim.getRotations() > 0.0);
        assertFalse("Encoder not inverted", sim.getInverted());
        assertTrue("Encoder velocity", sim.getVelocity() > 0.0);

        // Ensure the encoder decrements when moving backwards
        controller.stopMotor();
        sim.reset();
        controller.set(-1.0);
        sim.update();
        sim.update();
        assertTrue("Encoder decremented", sim.getRotations() < 0.0);
        assertFalse("Encoder not inverted", sim.getInverted());
        assertTrue("Encoder velocity", sim.getVelocity() < 0.0);

        // Unset global time scale
        TimeScale.globallyOverrideCalculationOutput(null);

    }

    @Test
    public void testControllerInverted() {

        // Controller
        MockSpeedController controller = new MockSpeedController();
        controller.setInverted(true);

        // Encoder sim
        EncoderSimUtil sim = new EncoderSimUtil("TestEncoder", 1, 1440, controller, 8.45,
                new DCBrushedMotor(DCMotor.getCIM(1)).getFreeSpeedRPM(), 0.12);

        // Set a custom time scale for this test
        TimeScale.globallyOverrideCalculationOutput(0.02);

        // Ensure the encoder increments when moving backwards
        controller.stopMotor();
        sim.reset();
        controller.set(1.0);
        sim.update();
        sim.update();
        assertTrue("Controller inverted", controller.getInverted());
        assertTrue("Encoder incremented", sim.getRotations() > 0.0);
        assertFalse("Encoder not inverted", sim.getInverted());
        assertTrue("Encoder velocity", sim.getVelocity() > 0.0);

        // Ensure the encoder decrements when moving forwards
        controller.stopMotor();
        sim.reset();
        controller.set(-1.0);
        sim.update();
        sim.update();
        assertTrue("Encoder decremented", sim.getRotations() < 0.0);
        assertTrue("Controller inverted", controller.getInverted());
        assertFalse("Encoder not inverted", sim.getInverted());
        assertTrue("Encoder velocity", sim.getVelocity() < 0.0);

        // Unset global time scale
        TimeScale.globallyOverrideCalculationOutput(null);
        sim.close();

    }

    @Test
    public void testEncoderInverted() {

        // Controller
        MockSpeedController controller = new MockSpeedController();

        // Encoder sim
        EncoderSimUtil sim = new EncoderSimUtil("TestEncoder", 2, 1440, controller, 8.45,
                new DCBrushedMotor(DCMotor.getCIM(1)).getFreeSpeedRPM(), 0.12);
        sim.setInverted(true);

        // Set a custom time scale for this test
        TimeScale.globallyOverrideCalculationOutput(0.02);

        // Ensure the encoder increments when moving backwards
        controller.stopMotor();
        sim.reset();
        controller.set(-1.0);
        sim.update();
        sim.update();
        assertTrue("Encoder incremented", sim.getRotations() > 0.0);
        assertTrue("Encoder inverted", sim.getInverted());
        assertTrue("Encoder velocity", sim.getVelocity() > 0.0);

        // Ensure the encoder decrements when moving forwards
        controller.stopMotor();
        sim.reset();
        controller.set(1.0);
        sim.update();
        sim.update();
        assertTrue("Encoder decremented", sim.getRotations() < 0.0);
        assertTrue("Encoder inverted", sim.getInverted());
        assertTrue("Encoder velocity", sim.getVelocity() < 0.0);

        // Unset global time scale
        TimeScale.globallyOverrideCalculationOutput(null);
        sim.close();

    }

    @Test
    public void testBothInverted() {

        // Controller
        MockSpeedController controller = new MockSpeedController();
        controller.setInverted(true);

        // Encoder sim
        EncoderSimUtil sim = new EncoderSimUtil("TestEncoder", 3, 1440, controller, 8.45,
                new DCBrushedMotor(DCMotor.getCIM(1)).getFreeSpeedRPM(), 0.12);
        sim.setInverted(true);

        // Set a custom time scale for this test
        TimeScale.globallyOverrideCalculationOutput(0.02);

        // Ensure the encoder increments when moving forwards
        controller.stopMotor();
        sim.reset();
        controller.set(-1.0);
        sim.update();
        sim.update();
        assertTrue("Encoder incremented", sim.getRotations() > 0.0);
        assertTrue("Controller inverted", controller.getInverted());
        assertTrue("Encoder inverted", sim.getInverted());
        assertTrue("Encoder velocity", sim.getVelocity() > 0.0);

        // Ensure the encoder decrements when moving backwards
        controller.stopMotor();
        sim.reset();
        controller.set(1.0);
        sim.update();
        sim.update();
        assertTrue("Encoder decremented", sim.getRotations() < 0.0);
        assertTrue("Controller inverted", controller.getInverted());
        assertTrue("Encoder inverted", sim.getInverted());
        assertTrue("Encoder velocity", sim.getVelocity() < 0.0);

        // Unset global time scale
        TimeScale.globallyOverrideCalculationOutput(null);
        sim.close();

    }

}