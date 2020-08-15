package ca.retrylife.frc.limelight.controls;

/**
 * Limelight LED mode setting
 */
public enum LEDMode {

    DEFAULT(0), // Use pipeline default setting
    ON(3), // Force-enable LEDs
    OFF(1), // Force-disable LEDs
    BLINK(2); // Blink LEDs

    private int val;

    LEDMode(int val) {
        this.val = val;
    }

    /**
     * Get the LEDMode setting value
     * 
     * @return Setting value
     */
    public int getValue() {
        return val;
    }

}