/**
 * This file part of an effort by Evan Pratten <ewpratten@gmail.com> to build a 
 * system that allows new programmers make use of advanced control theory without 
 * needing to learn the underlying math.
 * 
 * The class(es) below rely on some work that was done by 
 * Matt Morley <matthew.morley.ca@gmail.com> and Tyler Veness <calcmogul@gmail.com> 
 * upstream at WPILib to allow teams to write statespace code in Java via JNI.
 * 
 * The underlying code is built on top of an advanced robotics software 
 * library from MIT called DRAKE (https://drake.mit.edu)
 * 
 * If you are interested in learning how this code works, I recommend you take a 
 * look at Tyler's paper titled: "Controls Engineering in the FIRST Robotics Competition".
 * The paper is available at: https://file.tavsys.net/control/controls-engineering-in-frc.pdf
 */

package io.github.frc5024.lib5k.control_loops.statespace.wrappers;

import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;

/**
 * This is a wrapper around a state space plant, observer, motion profiling, and
 * LQR. The SingleJointedArmController class is designed to allow grade 9, and
 * new programmers to make use of state space-based SJI control without having
 * to worry about the actual math happening BTS.
 * 
 * See for information on the math going on here:
 * https://file.tavsys.net/control/controls-engineering-in-frc.pdf#%5B%7B%22num%22%3A41%2C%22gen%22%3A0%7D%2C%7B%22name%22%3A%22XYZ%22%7D%2C85.04%2C470.74%2Cnull%5D
 */
public class SingleJointedArmController {

    public SingleJointedArmController(DCBrushedMotor motorType, double armMassKg, double armLengthM, double gearRatio,
            double maxVelocityMPS, double maxAccelerationMPSSquared, double modelPositionAccuracyDegrees,
            double modelVelocityAccuracyDegreesPerSecond, double encoderAccuracyDegrees, double expectedLoopTimeSeconds,
            double positionEpsilonDegrees, double velocityEpsilonDegreesPerSecond, double maxOutputVoltage) {
        

    }
}