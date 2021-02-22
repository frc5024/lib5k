package io.github.frc5024.common_drive;

import io.github.frc5024.common_drive.types.ShifterType;
import io.github.frc5024.lib5k.control_loops.ExtendedPIDController;

/**
 * Configuration data for the drivetrain
 */
@Deprecated(since = "October 2020", forRemoval = true)
public abstract class DriveTrainConfig {

    // Shifting method to use
    public ShifterType shifterType;

    // State-space matrices
    // StateSpaceLoop<N4, N2, N2> drivetrainLoop;

    // Robot parameters
    public double robotRadius;
    public double wheelRadius;
    public double robotWidth;

    // Gearing
    public double highGearRatio;
    public double lowGearRatio;

    // Variable for storing weather the robot is in high gear by default
    public boolean defaultHighGear;

    // Closed loop control
    public ExtendedPIDController turningController;
    public ExtendedPIDController distanceController;

    // Ramp rates
    public double defaultRampSeconds;
    public double pathingRampSeconds;

    /**
     * Get the wheel circumference
     * 
     * @return Circumference
     */
    public double getWheelCircumference() {
        return 2 * Math.PI * wheelRadius;
    }

}