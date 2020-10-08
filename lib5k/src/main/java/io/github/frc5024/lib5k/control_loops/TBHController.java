package io.github.frc5024.lib5k.control_loops;

import io.github.frc5024.lib5k.control_loops.base.Controller;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;
import io.github.frc5024.lib5k.utils.RobotMath;

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
public class TBHController implements Controller {

    // Controller gain
    private double gain;

    // Controller state
    private double tbh;
    private double previousError;
    private double output;

    // Timekeeping
    private double lastTime;
    private final double restTimeSeconds;
    private double lastUnstableTime;

    // Reference
    private double reference;
    private double epsilon;
    private boolean atGoal = false;

    /**
     * Create a TBHController
     * 
     * @param gain Controller gain (should be between 0.0 and 1.0)
     */
    public TBHController(double gain, double restTimeSeconds) {

        // Set up controller
        setGain(gain);
        reset();

        this.restTimeSeconds = restTimeSeconds;

        // Track last time
        lastTime = FPGAClock.getFPGAMilliseconds();
    }

    /**
     * Set the controller gain
     * 
     * @param gain gain (should be between 0.0 and 1.0)
     */
    public void setGain(double gain) {
        this.gain = gain;
    }

    @Override
    public double calculate(double measurement) {

        // Calculate error
        double error = this.reference - measurement;

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

        // Check if we are at the goal
        if (RobotMath.epsilonEquals(error, 0, epsilon)) {
            atGoal = true;
        } else {
            atGoal = false;
            lastUnstableTime = FPGAClock.getFPGASeconds();
        }

        return output;

    }

    @Override
    public double calculate(double measurement, double reference) {
        setReference(reference);
        return calculate(measurement);
    }

    @Override
    public void setReference(double reference) {
        this.reference = reference;

    }

    @Override
    public boolean atReference() {
        return atGoal && (FPGAClock.getFPGASeconds() - lastUnstableTime) > restTimeSeconds;
    }

    @Override
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;

    }

    @Override
    public void reset() {
        tbh = 0.0;
        previousError = 0.0;
        output = 0.0;

        // Track last time
        lastTime = FPGAClock.getFPGAMilliseconds();
    }

}