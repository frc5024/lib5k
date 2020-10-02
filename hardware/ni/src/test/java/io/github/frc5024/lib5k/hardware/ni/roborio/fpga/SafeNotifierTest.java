package io.github.frc5024.lib5k.hardware.ni.roborio.fpga;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.frc5024.lib5k.logging.RobotLogger;

public class SafeNotifierTest {

    @Test
    public void testNotifierRethrowsException() {
        // Build a safe notifier
        SafeNotifier n = new SafeNotifier("RethrowTestNotifier", () -> {
            RobotLogger.getInstance().log("Running test task");
            RobotLogger.getInstance().flush();
            throw new RuntimeException("Test");
        });

        // Run the thread (This should throw an error)
        Exception ex = assertThrows(RuntimeException.class, () -> n.getUnderlyingRunnable().run());
        RobotLogger.getInstance().flush();
        assertEquals("Test", ex.getMessage());
        

    }

}