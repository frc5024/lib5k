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
 * This class contains utils for calculating parameters of flywheels
 */
public class FlywheelMath {

    /**
     * Calculate the Moment of Inertia (J) for a flywheel with given parameters.
     * 
     * @param launcherMassKg    The mass in kg of all the wheels that come in
     *                          contact with the launched object
     * @param launcherDiameterM The diameter in m of one of the wheels that come in
     *                          contact with the launched object
     * @param flywheelMassKg    The mass in kg of the extra weight added to the
     *                          flywheel for momentum (0.0 if none attached)
     * @param flywheelDiameterM The diameter in m of the extra weight added to the
     *                          flywheel for momentum (0.0 if none attached)
     * @return The Moment of Inertia
     */
    public static double calculateJ(double launcherMassKg, double launcherDiameterM, double flywheelMassKg,
            double flywheelDiameterM) {

        // Calculate the launcher's MOI
        double launcherMOI = 0.5 * launcherMassKg * Math.pow(launcherDiameterM / 2, 2);

        // Calculate the ratio between the mass and the wheel
        double launcherToWheelRatio = (flywheelDiameterM / launcherDiameterM) + 1;

        // Calculate the flywheel's MOI
        double flywheelMOI = 0.5 * flywheelMassKg * Math.pow(flywheelDiameterM / 2, 2) * launcherToWheelRatio
                * launcherToWheelRatio;

        // Return the sum of MOIs
        return launcherMOI + flywheelMOI;
    }

}