package io.github.frc5024.lib5k.hardware.ni.roborio.fpga;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SafeNotifierTest {

    @Test
    public void testNotifierRethrowsException() {
        // Build a safe notifier
        SafeNotifier n = new SafeNotifier("RethrowTestNotifier", () -> {
            throw new RuntimeException("Test");
        });

        // Do some low-level work to get the underlying thread of the notifier
        Thread underlyingThread = n.getUnderlyingThread();

        // Write a custom error handler
        underlyingThread.setUncaughtExceptionHandler((thread, error) -> {
            assertTrue(error instanceof RuntimeException);
        });

        // Run the thread (This should throw an error)
        n.startSingle(0.0);

        
    }

}