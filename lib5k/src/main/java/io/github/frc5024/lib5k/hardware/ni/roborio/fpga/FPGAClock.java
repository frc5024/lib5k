package io.github.frc5024.lib5k.hardware.ni.roborio.fpga;

import edu.wpi.first.wpilibj.Timer;

/**
 * Tools for interacting with the FPGA's high-precision clock
 */
public class FPGAClock {

    private static boolean overrideClock;
    private static double overrideTime;

    /**
     * Get the number of seconds since the robot timer started
     * 
     * @return Seconds since timer start
     */
    public static double getFPGASeconds() {

        // Handle override
        if (overrideClock) {
            return overrideTime;
        }

        // Normal operation
        return Timer.getFPGATimestamp();
    }

    /**
     * Get the number of milliseconds since the robot timer started
     * 
     * @return Milliseconds since timer start
     */
    public static double getFPGAMilliseconds() {
        return getFPGASeconds() * 1000.0;
    }

    /**
     * A utility for anything that needs to toggle every N milliseconds (for
     * example, a blinking light)
     * 
     * @param period Number of milliseconds to wait before toggling the output
     * @return Output
     */
    public static boolean getMillisecondCycle(double period) {
        return ((getFPGAMilliseconds() % (period * 2)) - period) >= 0;
    }

    /**
     * THIS IS NOT RECOMMENDED UNLESS RUNNING A UNIT TEST. Set a manual override on
     * the system clock
     * 
     * @param enabled     Enable
     * @param timeSeconds Time to start at
     */
    public static void enableSystemClockOverride(boolean enabled, double timeSeconds) {
        overrideClock = enabled;
        overrideTime = timeSeconds;
    }

    /**
     * HIS IS NOT RECOMMENDED UNLESS RUNNING A UNIT TEST. Increment the override
     * time
     * 
     * @param stepSeconds Time to increment by
     */
    public static void incrementSystemClockOverride(double stepSeconds) {
        overrideTime += stepSeconds;
    }
}