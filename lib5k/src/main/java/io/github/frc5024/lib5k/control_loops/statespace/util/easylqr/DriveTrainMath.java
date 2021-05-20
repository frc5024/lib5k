/**
 * This file part of an effort by Evan Pratten <ewpratten@gmail.com> to build a 
 * system that allows new programmers make use of advanced control theory without 
 * needing to learn the underlying math.
 * 
 * The contents of this file come from an old Python script I built to automate 
 * LQR system building for an offseason project.
 */

package io.github.frc5024.lib5k.control_loops.statespace.util.easylqr;

/**
 * This class contains utils for calculating parameters of drivetrains
 */
public class DriveTrainMath {

    /**
     * Calculate the Moment of Inertia (J) for a drivetrain with given parameters.
     * 
     * @param robotMassKG  Mass of the robot in JG
     * @param robotRadiusM Radius of the robot in meters
     * @return Robot MOI
     */
    public static double calculateJ(double robotMassKG, double robotRadiusM) {
        return robotMassKG * Math.pow(robotMassKG, 2);
    }

}