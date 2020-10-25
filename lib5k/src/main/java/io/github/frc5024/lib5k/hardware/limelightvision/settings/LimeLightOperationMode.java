package io.github.frc5024.lib5k.hardware.limelightvision.settings;

/**
 * Limelight operation mode setting
 */
public enum LimeLightOperationMode {

    VISION(0), // Vision processor
    DRIVER(1); // Driver Camera (Increases exposure, disables vision processing)

    private int val;

    /**
     * Create an LimeLightOperationMode
     * 
     * @param val Limelight control byte
     */
    LimeLightOperationMode(int val) {
        this.val = val;
    }

    /**
     * Get the LimeLightOperationMode setting value
     * 
     * @return Setting value
     */
    public int getValue() {
        return val;
    }

}