package io.github.frc5024.common_drive.calculation;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;

/**
 * Helpers for calculating differential drive kinematics
 */
@Deprecated(since = "October 2020", forRemoval = true)
public class DifferentialDriveCalculation {

    /**
     * Normalize a percent output DriveSignal
     * 
     * @param signal Input signal
     * @return Normalized signal
     */
    public static DifferentialDriveWheelSpeeds normalize(DifferentialDriveWheelSpeeds signal) {

        // Find the maximum magnitude between both wheels
        double magnitude = Math.max(Math.abs(signal.leftMetersPerSecond), Math.abs(signal.rightMetersPerSecond));

        // Scale back motors if the max magnitude is greater than the max output (1.0)
        if (magnitude > 1.) {
            signal.leftMetersPerSecond = (signal.leftMetersPerSecond / magnitude);
            signal.rightMetersPerSecond = (signal.rightMetersPerSecond / magnitude);
        }

        return signal;
    }

    /**
     * Calculate a percent motor output from speed and rotation inputs using
     * "semi-constant curvature" calculation
     * 
     * @param speed    Desired speed
     * @param rotation Desired rotation
     * @return Computed DriveSignal
     */
    public static DifferentialDriveWheelSpeeds semiConstCurve(double speed, double rotation) {

        // Stop speed from being NaN
        if (Double.isNaN(speed)) {
            speed = 0.0000001;
        }

        // Stop speed from being NaN
        if (Double.isNaN(rotation)) {
            rotation = 0.0000001;
        }

        // Calculate direct speed conversion (rate-based turning)
        double rate_l = (speed + rotation);
        double rate_r = (speed - rotation);

        // Calculate constant-curvature speeds
        double curv_l = (speed + Math.abs(speed) * rotation);
        double curv_r = (speed - Math.abs(speed) * rotation);

        // Determine average speeds
        double avg_l = (rate_l + curv_l) / 2;
        double avg_r = (rate_r + curv_r) / 2;

        // Clamp motor outputs
        avg_l = MathUtils.clamp(avg_l, -1., 1.);
        avg_r = MathUtils.clamp(avg_r, -1., 1.);

        // Create a DriveSignal from motor speeds
        DifferentialDriveWheelSpeeds signal = new DifferentialDriveWheelSpeeds(avg_l, avg_r);


        // Return the drive signal
        return signal;
    }

    /**
     * Calculate a percent motor output from speed and rotation inputs using "arcade
     * calculation"
     * 
     * @param speed    Desired speed
     * @param rotation Desired rotation
     * @return Computed DriveSignal
     */
    public static DifferentialDriveWheelSpeeds arcade(double speed, double rotation) {

        // Stop speed from being NaN
        if (Double.isNaN(speed)) {
            speed = 0.0000001;
        }

        // Stop speed from being NaN
        if (Double.isNaN(rotation)) {
            rotation = 0.0000001;
        }

        // Calculate direct speed conversion (rate-based turning)
        double l = (speed + rotation);
        double r = (speed - rotation);

        // Clamp motor outputs
        l = MathUtils.clamp(l, -1., 1.);
        r = MathUtils.clamp(r, -1., 1.);

        // Create a DriveSignal from motor speeds
        DifferentialDriveWheelSpeeds signal = new DifferentialDriveWheelSpeeds(l, r);

        // Normalize the signal
        signal = normalize(signal);

        // Return the drive signal
        return signal;
    }

}