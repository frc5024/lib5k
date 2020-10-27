package io.github.frc5024.lib5k.utils;

import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

/**
 * TimeScale is a utility for calculating time-based scaling values. This simply
 * wraps a DT calculation, but lets users completely override it in unit tests.
 */
public class TimeScale {

    /**
     * Timing modes
     */
    public enum Mode {
        SECONDS, MILLISECONDS;
    }

    // Last time recorded
    private double lastTime = 0;

    // Overrides
    private Double override = null;
    private static Double globalOverride = null;

    // Mode
    private final Mode calculationMode;

    /**
     * Create a TimeScale for Seconds
     */
    public TimeScale() {
        this(Mode.SECONDS);
    }

    /**
     * Create a TimeScale
     * 
     * @param mode Timing mode
     */
    public TimeScale(Mode mode) {
        this.calculationMode = mode;
        this.lastTime = getCurrentTime();
    }

    /**
     * Get the current time in the correct unit
     * 
     * @return Current time
     */
    private double getCurrentTime() {
        if (calculationMode.equals(Mode.SECONDS)) {
            return FPGAClock.getFPGASeconds();
        } else {
            return FPGAClock.getFPGAMilliseconds();
        }

    }

    /**
     * Calculate a scaling factor since the last time this function was called
     * 
     * @return Scaling factor
     */
    public double calculate() {

        // Handle an override
        if (globalOverride != null) {
            return globalOverride;
        }
        if (override != null) {
            return override;
        }

        // Calc DT
        double curTime = getCurrentTime();
        double dt = curTime - lastTime;
        lastTime = curTime;

        return dt;

    }

    /**
     * Override the output of this utility. Only use this in unit tests!
     * 
     * @param outputOverride Override output
     */
    @Deprecated
    public void overrideCalculationOutput(Double outputOverride) {
        this.override = outputOverride;
    }

    /**
     * Override the output of all instances of this utility. Only use this in unit
     * tests!
     * 
     * @param outputOverride Override output
     */
    @Deprecated

    public static void globallyOverrideCalculationOutput(Double outputOverride) {
        globalOverride = outputOverride;
    }

    /**
     * Reset
     */
    public void reset() {
        this.lastTime = getCurrentTime();
        this.override = null;
    }

}