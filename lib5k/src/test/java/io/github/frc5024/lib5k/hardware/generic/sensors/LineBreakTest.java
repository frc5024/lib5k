package io.github.frc5024.lib5k.hardware.generic.sensors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.simulation.DIOSim;

public class LineBreakTest {

    @Test
    public void testSolenoidPoweredLineBreakSensor() {

        // Solenoid
        Solenoid powerSource = new Solenoid(0);

        // Ensure there is no power flowing though the solenoid
        assertFalse("Solenoid disabled", powerSource.get());

        // Set up a line break sensor
        LineBreak sensor = new LineBreak(10, powerSource);

        // Set up simulation
        DIOSim sim = new DIOSim(sensor);

        // Ensure the solenoid is now producing power
        assertTrue("Solenoid enabled", powerSource.get());

        // Check line unbroken
        sim.setValue(false);
        assertFalse("LineBreak disabled", sensor.get());

        // Check line broken
        sim.setValue(true);
        assertTrue("LineBreak enabled", sensor.get());

        sensor.close();

    }

}