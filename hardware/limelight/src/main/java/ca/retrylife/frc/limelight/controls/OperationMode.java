package ca.retrylife.frc.limelight.controls;

/**
 * Limelight operation mode setting
 */
public enum OperationMode {

    VISION(0), // Vision processor
    DRIVER(1); // Driver Camera (Increases exposure, disables vision processing)

    private int val;

    /**
     * Create an OperationMode
     * 
     * @param val Limelight control byte
     */
    OperationMode(int val) {
        this.val = val;
    }

    /**
     * Get the OperationMode setting value
     * 
     * @return Setting value
     */
    public int getValue() {
        return val;
    }

}