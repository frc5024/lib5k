package io.github.frc5024.lib5k.hardware.generic.pneumatics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.frc5024.lib5k.utils.RobotMath;

public class LazySolenoidTest {

    @Test
    public void testLazySolenoid() {

        // Create a lazy solenoid
        LazySolenoid solenoid = new LazySolenoid(8, 1);

        // Check that the last packet does not exist
        assertEquals("Last CAN timestamp", solenoid.getLastCANWriteTimestampSeconds(), 0.0, RobotMath.kVerySmallNumber);

        // Write an output
        solenoid.set(true);
        assertTrue("CAN Message sent", solenoid.getLastCANWriteTimestampSeconds() > 0.0);

        // Write again, and ensure timestamp did not change
        double lastTime = solenoid.getLastCANWriteTimestampSeconds();
        solenoid.set(true);
        assertEquals("Last CAN timestamp", solenoid.getLastCANWriteTimestampSeconds(), lastTime,
                RobotMath.kVerySmallNumber);

        // Write an output
        solenoid.set(false);
        assertTrue("CAN Message sent", solenoid.getLastCANWriteTimestampSeconds() > lastTime);

        // Write again, and ensure timestamp did not change
        lastTime = solenoid.getLastCANWriteTimestampSeconds();
        solenoid.set(false);
        assertEquals("Last CAN timestamp", solenoid.getLastCANWriteTimestampSeconds(), lastTime,
                RobotMath.kVerySmallNumber);

        // Clean up
        solenoid.close();
    }

}