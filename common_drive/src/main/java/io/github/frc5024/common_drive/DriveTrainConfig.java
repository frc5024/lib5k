package io.github.frc5024.common_drive;

import edu.wpi.first.wpiutil.math.Matrix;
import edu.wpi.first.wpiutil.math.MatrixUtils;
import edu.wpi.first.wpiutil.math.Nat;
import edu.wpi.first.wpiutil.math.numbers.*;
import io.github.frc5024.common_drive.controller.PDFGains;
import io.github.frc5024.common_drive.controller.PIFGains;
import io.github.frc5024.common_drive.types.ShifterType;

/**
 * Configuration data for the drivetrain
 */
public abstract class DriveTrainConfig {

    // Shifting method to use
    public ShifterType shifterType;


    // State-space matrices
    // StateSpaceLoop<N4, N2, N2> drivetrainLoop;

    // Control loop time step
    public long dt_ms;

    // Robot parameters
    public double robotRadius;
    public double wheelRadius;
    // public double v; // Motor velocity constant

    // Gearing
    public double highGearRatio;
    public double lowGearRatio;

    // Moment of inertia and mass
    // public double J;
    // public double mass;

    // Variable for storing weather the robot is in high gear by default
    public boolean defaultHighGear;

    // double downOffset; // TODO: is this needed?

    // Closed loop control
    public PIFGains turningGains;
    public PDFGains distanceGains;

    // Ramp rates
    public double defaultRampSeconds;
    public double pathingRampSeconds;

    /**
     * Converts the robot state to a linear [position, velocity].
     * 
     * @param leftRight Robot state
     * @return Linear distance
     */
    public static Matrix<N2, N1> leftRightToLinear(Matrix<N7, N1> leftRight) {
        // Make an empty matrix
        Matrix<N2, N1> linear = MatrixUtils.zeros(Nat.N2());

        // Fill the matrix with linear state
        linear.set(0, 0, (leftRight.get(0, 0) + leftRight.get(2, 0)) / 2.0);
        linear.set(1, 0, (leftRight.get(1, 0) + leftRight.get(3, 0)) / 2.0);

        return linear;
    }

    /**
     * Converts the robot state to a angular [distance, velocity].
     * 
     * @param leftRight Robot state
     * @return Angular distance
     */
    public Matrix<N2, N1> leftRightToAngular(Matrix<N7, N1> leftRight) {
        // Make an empty matrix
        Matrix<N2, N1> angular = MatrixUtils.zeros(Nat.N2());

        // Fill the matrix with angular state
        angular.set(0, 0, (leftRight.get(2, 0) - leftRight.get(0, 0)) / (this.robotRadius * 2.0));
        angular.set(1, 0, (leftRight.get(3, 0) - leftRight.get(1, 0)) / (this.robotRadius * 2.0));

        return angular;
    }

    /**
     * Converts the linear and angular position, velocity to the top 4 states of the
     * robot state.
     * 
     * @param linear Linear position
     * @param angular Angular position
     * @return Robot states
     */
    public Matrix<N4, N1> angularLinearToLeftRight(Matrix<N2, N1> linear, Matrix<N2, N1> angular) {

        // Create a scaled angle
        Matrix<N2, N1> scaledAngle = angular.times(this.robotRadius);

        // Init an empty state
        Matrix<N4, N1> state = MatrixUtils.zeros(Nat.N4());

        // Fill state
        state.set(0, 0, linear.get(0, 0) - scaledAngle.get(0, 0));
        state.set(1, 0, linear.get(1, 0) - scaledAngle.get(1, 0));
        state.set(2, 0, linear.get(0, 0) + scaledAngle.get(0, 0));
        state.set(3, 0, linear.get(1, 0) + scaledAngle.get(1, 0));

        return state;
    }

}