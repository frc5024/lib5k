package io.github.frc5024.lib5k.hardware.common.motors.interfaces;

/**
 * Common interface for devices that with current output controls
 */
public interface ICurrentController {

    /**
     * Configure a current limiting
     * 
     * @param threshold Threshold to trigger limit
     * @param hold      Amperage to hold the controller at while limiting
     * @param duration  How long the value must pass the threshold to be limited
     * @param timeout   Timeout (can be 0)
     */
    public void setCurrentLimit(int threshold, int duration, int hold, int timeout);

    /**
     * Set if voltage compensation should be enabled
     * 
     * @param on Enable compensation
     */
    public void setCompensation(boolean on);

    /**
     * Enable current limiting
     * 
     * @param on Enable
     */
    public void enableCurrentLimit(boolean on);
}