package io.github.frc5024.lib5k.control_loops;

import edu.wpi.first.math.controller.PIDController;
import io.github.frc5024.lib5k.control_loops.base.Controller;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

/**
 * ExtendedPIDController is an extension of WPILib's PIDController that adds a
 * few quality-of-life features
 */
public class ExtendedPIDController extends PIDController implements Controller {
    
    // FeedForward
    private final double feedForward;

    // Settling time
    private final double restTimeSeconds;
    private double lastUnstableTime;

    /**
     * Create an ExtendedPIDController
     * 
     * @param Kp P gain
     * @param Ki I gain
     * @param Kd D gain
     */
    public ExtendedPIDController(double Kp, double Ki, double Kd) {
        this(Kp, Ki, Kd, 0, 0);
    }

    /**
     * Create an ExtendedPIDController
     * 
     * @param Kp P gain
     * @param Ki I gain
     * @param Kd D gain
     * @param FF Feed Forward value
     */
    public ExtendedPIDController(double Kp, double Ki, double Kd, double FF) {
        this(Kp, Ki, Kd, FF, 0);
    }

    /**
     * Create an ExtendedPIDController
     * 
     * @param Kp              P gain
     * @param Ki              I gain
     * @param Kd              D gain
     * @param FF              Feed Forward value
     * @param restTimeSeconds Amount of time the system must be stable for to be
     *                        considered "truly stable"
     */
    public ExtendedPIDController(double Kp, double Ki, double Kd, double FF, double restTimeSeconds) {
        super(Kp, Ki, Kd);
        this.feedForward = FF;
        this.restTimeSeconds = restTimeSeconds;
    }

    @Override
    public double calculate(double measurement) {

        // Calculate the system output
        double output = super.calculate(measurement);

        // Check if the system is unstable
        if (!super.atSetpoint()) {
            lastUnstableTime = FPGAClock.getFPGASeconds();
        }

        // Return the FF value
        return output + feedForward;
    }

    @Override
    public boolean atSetpoint() {
        // We are at the setpoint if we are stable, and have been stable long enough
        return super.atSetpoint() && (FPGAClock.getFPGASeconds() - lastUnstableTime) > restTimeSeconds;
    }

    @Override
    public void setReference(double reference) {
        setSetpoint(reference);

    }

    @Override
    public boolean atReference() {
        return atSetpoint();
    }

    @Override
    public void setEpsilon(double epsilon) {
        setTolerance(epsilon);
    }

}