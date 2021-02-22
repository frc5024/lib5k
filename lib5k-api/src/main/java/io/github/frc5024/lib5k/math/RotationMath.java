package io.github.frc5024.lib5k.math;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import io.github.frc5024.lib5k.utils.RobotMath;

/**
 * Utils related to rotational math
 */
public class RotationMath {

    /**
     * Get the error in degrees between two rotations
     * 
     * @param current Current rotation
     * @param goal    Goal rotation
     * @return Distance apart from eachother
     */
    public static double getAngularErrorDegrees(Rotation2d current, Rotation2d goal) {
        return RobotMath.getWrappedError(goal.getDegrees(), current.getDegrees());
    }

}