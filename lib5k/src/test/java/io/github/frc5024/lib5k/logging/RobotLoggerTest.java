package io.github.frc5024.lib5k.logging;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RobotLoggerTest {

    /**
     * Test that the logger correctly truncates class names
     */
    @Test
    public void testPackageNameConstruction() {

        // Get a stack trace to this method
        StackTraceElement thisMethod = Thread.currentThread().getStackTrace()[1];

        // Get the friendly package string
        String friendlyPackageName = RobotLogger.getPackageName(thisMethod);

        // Check if correct
        assertEquals("Package name", "io...logging.RobotLoggerTest::testPackageNameConstruction()",
                friendlyPackageName);

    }

}