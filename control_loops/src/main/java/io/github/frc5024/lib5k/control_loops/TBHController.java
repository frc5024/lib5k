package io.github.frc5024.lib5k.control_loops;

import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

/**
 * A Take-Back-Half controller designed for controlling flywheels.
 * 
 * The controller increases speed until the measurement goes over the target,
 * then it "takes back half", and continues this process until the velocity is
 * stable.
 * 
 * The gain determines how quickly the speed is changed.
 * 
 * This is based off of this paper:
 * https://www.chiefdelphi.com/t/paper-take-back-half-shooter-wheel-speed-control/121640
 */
public class TBHController {

    // Controller gain
    private double gain;

    // Controller state
    private double tbh;
    private double previousError;
    private double output;

    // Timekeeping
    private double lastTime;

    /**
     * Create a TBHController
     * 
     * @param gain Controller gain (should be between 0.0 and 1.0)
     */
    public TBHController(double gain) {

        // Set up controller
        setGain(gain);
        reset();

        // Track last time
        lastTime = FPGAClock.getFPGAMilliseconds();
    }

    /**
     * Set the controller gain
     * 
     * @param gainController gain (should be between 0.0 and 1.0)
     */
    public void setGain(double gain) {
        this.gain = gain;
    }

    /**
     * Reset the controller
     */
    public void reset() {
        tbh = 0.0;
        previousError = 0.0;
        output = 0.0;

        // Track last time
        lastTime = FPGAClock.getFPGAMilliseconds();
    }

    /**
     * Calculate the controller output
     * 
     * @param error Error in system (goal - current)
     * @return Output
     */
    public double calculate(double error) {

        // Calculate dt
        double time = FPGAClock.getFPGAMilliseconds();
        double dt = time - lastTime;
        lastTime = time;

        // Calculate base output
        output += gain * error * dt;

        // Calculate TBH
        if ((error < 0) != (previousError < 0)) {
            output += tbh;
            output *= 0.5;

            tbh = output;
            previousError = error;
        }

        return output;
    }

}