package io.github.frc5024.lib5k.hardware.generic.ledstrips;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import io.github.frc5024.lib5k.utils.RobotMath;

public class BlinkinDriverTest {

    @Test
    public void testAllDriverOutputs() {

        // Set up the driver
        BlinkinDriver driver = new BlinkinDriver(0);

        // Get a list of all possible status settings
        List<BlinkinDriver.LEDSetting> allPossibleSettings = new ArrayList<>(
                EnumSet.allOf(BlinkinDriver.LEDSetting.class));

        // Iterate every possible setting
        for (BlinkinDriver.LEDSetting setting : allPossibleSettings) {

            // Write the setting to the driver
            driver.set(setting);

            // Ensure the setting is correctly written
            assertEquals(String.format("%s PWM output", setting.toString()), setting.get(), driver.m_controller.get(), RobotMath.kVerySmallNumber);
        }

        // Clean up
        driver.close();
    }

}