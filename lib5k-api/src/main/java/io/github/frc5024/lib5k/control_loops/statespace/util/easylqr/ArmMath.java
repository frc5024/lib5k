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
 * This class contains utils for calculating parameters of arms
 */
public class ArmMath {

    /**
     * Calculate the Moment of Inertia (J) for a single jointed arm with given
     * parameters.
     * 
     * @param armLengthM Length of the arm in meters
     * @param armMassKg  Mass of the arm in Kg
     * @return The Moment of Inertia
     */
    public static double calculateSJIJ(double armLengthM, double armMassKg) {

        // Calculate MOI
        return 1.0 / 3.0 * armMassKg * Math.pow(armLengthM, 2);
    }

}