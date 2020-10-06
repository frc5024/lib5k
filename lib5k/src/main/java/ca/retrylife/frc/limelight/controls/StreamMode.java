package ca.retrylife.frc.limelight.controls;

/**
 * Limelight stream mode setting
 */
public enum StreamMode {

    STANDARD(0), // Side-by-side streams if a webcam is attached to Limelight
    PIP_MAIN(1), // The secondary camera stream is placed in the lower-right corner of the
                 // primary camera stream
    PIP_SECONDARY(2); // The primary camera stream is placed in the lower-right corner of the
                      // secondary camera stream

    private int val;

    /**
     * Create a StreamMode
     * 
     * @param val Limelight control byte
     */
    StreamMode(int val) {
        this.val = val;
    }

    /**
     * Get the StreamMode setting value
     * 
     * @return Setting value
     */
    public int getValue() {
        return val;
    }

}