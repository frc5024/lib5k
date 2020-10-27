package io.github.frc5024.lib5k.hardware.limelightvision.settings;

/**
 * Limelight LED mode setting
 */
public enum LimeLightLEDMode {

    DEFAULT(0), // Use pipeline default setting
    ON(3), // Force-enable LEDs
    OFF(1), // Force-disable LEDs
    BLINK(2); // Blink LEDs

    private int val;

    /**
     * Create an LED mode
     * 
     * @param val Limelight control byte
     */
    LimeLightLEDMode(int val) {
        this.val = val;
    }

    /**
     * Get the LimeLightLEDMode setting value
     * 
     * @return Setting value
     */
    public int getValue() {
        return val;
    }

}