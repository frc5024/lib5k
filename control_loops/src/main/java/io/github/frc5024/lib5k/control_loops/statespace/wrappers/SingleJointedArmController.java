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

    /**
     * Create a SingleJointedArmController
     * 
     * @param motorType                             The type of motor(s) used to
     *                                              drive the arm
     * @param armMassKg                             The mass of the arm in Kg
     * @param armLengthM                            The length of the arm in M
     * @param gearRatio                             reduction between motors and
     *                                              encoder, as output over input.
     *                                              If the elevator spins slower
     *                                              than the motors, this number
     *                                              should be greater than one.
     *                                              IGNORE THIS IF THE ENCODER IS
     *                                              ATTACHED TO THE MOTOR
     * @param maxVelocityMPS                        The maximum velocity of the arm
     *                                              at the max voltage in
     *                                              meters/second
     * @param maxAccelerationMPSSquared             The maximum acceleration of the
     *                                              arm at the max voltage in
     *                                              meters/second^2
     * @param modelPositionAccuracyDegrees          The position accuracy of the
     *                                              model in degrees
     * @param modelVelocityAccuracyDegreesPerSecond The velocity accuracy of the
     *                                              model in degrees/second
     * @param encoderAccuracyDegrees                The position accuracy of the
     *                                              encoder in degrees
     * @param expectedLoopTimeSeconds               The loop period of the caller in
     *                                              seconds. This is generally 0.02
     *                                              (20ms)
     * @param positionEpsilonDegrees                Position epsilon in degrees
     * @param velocityEpsilonDegreesPerSecond       Velocity epsilon in
     *                                              degrees/second
     * @param maxOutputVoltage                      The maximum output voltage.
     *                                              Should generally be 12.0v
     */
    public SingleJointedArmController(DCBrushedMotor motorType, double armMassKg, double armLengthM, double gearRatio,
            double maxVelocityMPS, double maxAccelerationMPSSquared, double modelPositionAccuracyDegrees,
            double modelVelocityAccuracyDegreesPerSecond, double encoderAccuracyDegrees, double expectedLoopTimeSeconds,
            double positionEpsilonDegrees, double velocityEpsilonDegreesPerSecond, double maxOutputVoltage) {

    }
}