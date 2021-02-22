package io.github.frc5024.lib5k.hardware.generic.sensors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.simulation.DIOSim;

public class LimitSwitchTest {
    
    @Test
    public void testLimitSwitch() {

        // Hall effect sensor
        LimitSwitch sensor = new LimitSwitch(10);

        // Simulator
        DIOSim sim = new DIOSim(sensor);

        // Check sensor being pulled low
        sim.setValue(false);
        assertTrue("Sensor triggered", sensor.get());

        // Check sensor being pulled high
        sim.setValue(true);
        assertFalse("Sensor triggered", sensor.get());

        // Clean up
        sensor.close();

    }
    
}