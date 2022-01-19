package io.github.frc5024.lib5k.hardware.generic.motors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import io.github.frc5024.lib5k.hardware.generic.motors.LinearActuator.ActuatorState;

public class LinearActuatorTest {

    @Test
    public void testLinearActuator() {

        // Set up actuator
        LinearActuator actuator = new LinearActuator(PneumaticsModuleType.CTREPCM, 1);

        // Check deploy
        actuator.set(ActuatorState.kDEPLOYED);
        assertTrue("Output", actuator.isDeployed());

        // Check retract
        actuator.set(ActuatorState.kINACTIVE);
        assertFalse("Output", actuator.isDeployed());

        // Clean up
        actuator.close();
    }

}