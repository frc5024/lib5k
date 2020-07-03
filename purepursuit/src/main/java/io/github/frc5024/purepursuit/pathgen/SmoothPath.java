package io.github.frc5024.purepursuit.pathgen;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.util.Units;
import io.github.frc5024.purepursuit.util.Smoothing;

/**
 * A smoothed path
 */
public class SmoothPath extends Path {

    /**
     * Create a Smooth Path
     * 
     * @param weight    Weight of smoothing
     * @param smoothing How smooth the path is
     * @param tolerance How much the path is allowed to change
     * @param waypoints Path waypoints to follow
     */
    public SmoothPath(double weight, double smoothing, double tolerance, Translation2d waypoints) {
        this(Units.inchesToMeters(6.0), weight, smoothing, tolerance, waypoints);
    }

    /**
     * Create a Smooth Path
     * 
     * @param spacing   Amount of space between "inner" points in meters
     * @param weight    Weight of smoothing
     * @param smoothing How smooth the path is
     * @param tolerance How much the path is allowed to change
     * @param waypoints Path waypoints to follow
     */
    public SmoothPath(double spacing, double weight, double smoothing, double tolerance, Translation2d waypoints) {

        // Generate a linear path
        super(spacing, waypoints);

        // Smooth the generated path
        points = Smoothing.smooth(points, weight, smoothing, tolerance);

    }
}