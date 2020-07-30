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

import edu.wpi.first.wpilibj.controller.LinearQuadraticRegulator;
import edu.wpi.first.wpilibj.estimator.KalmanFilter;
import edu.wpi.first.wpilibj.system.LinearSystem;
import edu.wpi.first.wpilibj.system.LinearSystemLoop;
import edu.wpi.first.wpilibj.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpiutil.math.Nat;
import edu.wpi.first.wpiutil.math.numbers.N1;
import edu.wpi.first.wpiutil.math.numbers.N2;
import edu.wpi.first.wpiutil.math.VecBuilder;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.control_loops.statespace.util.easylqr.ArmMath;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

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

    // Motion profiler
    private TrapezoidProfile.Constraints motionConstraints;
    private TrapezoidProfile.State lastProfiledReference = new TrapezoidProfile.State();
    private TrapezoidProfile.State goal;

    // plant, observer, and LQR
    private LinearSystem<N2, N1, N1> plant;
    private KalmanFilter<N2, N1, N1> observer;
    private LinearQuadraticRegulator<N2, N1, N1> lqr;
    private LinearSystemLoop<N2, N1, N1> loop;

    // Timekeeping
    private double lastTimeSeconds = 0.0;

    /**
     * Create a SingleJointedArmController
     * 
     * @param motorType                 The type of motor(s) used to drive the arm
     * @param armMassKg                 The mass of the arm in Kg
     * @param armLengthM                The length of the arm in M
     * @param maxVelocityMPS            The maximum velocity of the arm at the max
     *                                  voltage in meters/second
     * @param maxAccelerationMPSSquared The maximum acceleration of the arm at the
     *                                  max voltage in meters/second^2
     * @param positionEpsilonDegrees    Position epsilon in degrees
     */
    public SingleJointedArmController(DCBrushedMotor motorType, double armMassKg, double armLengthM,
            double maxVelocityMPS, double maxAccelerationMPSSquared, double positionEpsilonDegrees) {
        this(motorType, armMassKg, armLengthM, 1.0, maxVelocityMPS, maxAccelerationMPSSquared, positionEpsilonDegrees,
                Units.radiansPerSecondToRotationsPerMinute(Units.degreesToRadians(10.0)));
    }

    /**
     * Create a SingleJointedArmController
     * 
     * @param motorType                 The type of motor(s) used to drive the arm
     * @param armMassKg                 The mass of the arm in Kg
     * @param armLengthM                The length of the arm in M
     * @param gearRatio                 reduction between motors and encoder, as
     *                                  output over input. If the elevator spins
     *                                  slower than the motors, this number should
     *                                  be greater than one. IGNORE THIS IF THE
     *                                  ENCODER IS ATTACHED TO THE MOTOR
     * @param maxVelocityMPS            The maximum velocity of the arm at the max
     *                                  voltage in meters/second
     * @param maxAccelerationMPSSquared The maximum acceleration of the arm at the
     *                                  max voltage in meters/second^2
     * @param positionEpsilonDegrees    Position epsilon in degrees
     * @param velocityEpsilonRPM        Velocity epsilon in RPM
     */
    public SingleJointedArmController(DCBrushedMotor motorType, double armMassKg, double armLengthM, double gearRatio,
            double maxVelocityMPS, double maxAccelerationMPSSquared, double positionEpsilonDegrees,
            double velocityEpsilonRPM) {
        this(motorType, armMassKg, armLengthM, gearRatio, maxVelocityMPS, maxAccelerationMPSSquared,
                Units.radiansToDegrees(0.015), Units.radiansPerSecondToRotationsPerMinute(0.17), 0.01, 0.02,
                positionEpsilonDegrees, velocityEpsilonRPM, 12.0);
    }

    /**
     * Create a SingleJointedArmController
     * 
     * @param motorType                    The type of motor(s) used to drive the
     *                                     arm
     * @param armMassKg                    The mass of the arm in Kg
     * @param armLengthM                   The length of the arm in M
     * @param gearRatio                    reduction between motors and encoder, as
     *                                     output over input. If the elevator spins
     *                                     slower than the motors, this number
     *                                     should be greater than one. IGNORE THIS
     *                                     IF THE ENCODER IS ATTACHED TO THE MOTOR
     * @param maxVelocityMPS               The maximum velocity of the arm at the
     *                                     max voltage in meters/second
     * @param maxAccelerationMPSSquared    The maximum acceleration of the arm at
     *                                     the max voltage in meters/second^2
     * @param modelPositionAccuracyDegrees The position accuracy of the model in
     *                                     degrees
     * @param modelVelocityAccuracyRPM     The velocity accuracy of the model in RPM
     * @param encoderAccuracyDegrees       The position accuracy of the encoder in
     *                                     degrees
     * @param expectedLoopTimeSeconds      The loop period of the caller in seconds.
     *                                     This is generally 0.02 (20ms)
     * @param positionEpsilonDegrees       Position epsilon in degrees
     * @param velocityEpsilonRPM           Velocity epsilon in RPM
     * @param maxOutputVoltage             The maximum output voltage. Should
     *                                     generally be 12.0v
     */
    public SingleJointedArmController(DCBrushedMotor motorType, double armMassKg, double armLengthM, double gearRatio,
            double maxVelocityMPS, double maxAccelerationMPSSquared, double modelPositionAccuracyDegrees,
            double modelVelocityAccuracyRPM, double encoderAccuracyDegrees, double expectedLoopTimeSeconds,
            double positionEpsilonDegrees, double velocityEpsilonRPM, double maxOutputVoltage) {

        // Set up motion profile constraints
        motionConstraints = new TrapezoidProfile.Constraints(maxVelocityMPS, maxAccelerationMPSSquared);

        // Create a plant
        plant = LinearSystemId.createSingleJointedArmSystem(motorType, ArmMath.calculateSJIJ(armLengthM, armMassKg),
                gearRatio);

        // Create an observer
        observer = new KalmanFilter<>(Nat.N2(), Nat.N1(), plant,
                VecBuilder.fill(Units.degreesToRadians(modelPositionAccuracyDegrees),
                        Units.rotationsPerMinuteToRadiansPerSecond(modelVelocityAccuracyRPM)),
                VecBuilder.fill(encoderAccuracyDegrees), expectedLoopTimeSeconds);

        // Design LQR
        double rho = 1.0; // I don't think anyone will ever need to change rho
        lqr = new LinearQuadraticRegulator<>(plant,
                VecBuilder.fill(Units.degreesToRadians(positionEpsilonDegrees),
                        Units.rotationsPerMinuteToRadiansPerSecond(velocityEpsilonRPM)),
                rho, VecBuilder.fill(maxOutputVoltage), expectedLoopTimeSeconds);

        // Build a loop
        loop = new LinearSystemLoop<>(plant, lqr, observer, maxOutputVoltage, expectedLoopTimeSeconds);

    }

    /**
     * Reset the controller to a known state
     * 
     * @param encoderPositionDegrees Encoder position reading in degrees
     * @param encoderVelocityRPM     Encoder velocity reading in RPM
     */
    public void reset(double encoderPositionDegrees, double encoderVelocityRPM) {
        // Reset the loop to a known state
        loop.reset(VecBuilder.fill(Units.degreesToRadians(encoderPositionDegrees),
                Units.radiansPerSecondToRotationsPerMinute(encoderVelocityRPM)));

        // Reset the last reference
        lastProfiledReference = new TrapezoidProfile.State(Units.degreesToRadians(encoderPositionDegrees),
                Units.radiansPerSecondToRotationsPerMinute(encoderVelocityRPM));
    }

    /**
     * Set the desired arm angle
     * 
     * @param degrees Angle in degrees
     */
    public void setDesiredAngle(double degrees) {
        goal = new TrapezoidProfile.State(Units.degreesToRadians(degrees), 0.0);
    }

    public double computeVoltage(double encoderPositionDegrees) {

        // Skip if no goal is set
        if (goal == null) {
            return 0.0;
        }

        // Calculate DT
        double dt;
        if (lastTimeSeconds != 0) {
            double currentTimeSeconds = FPGAClock.getFPGASeconds();
            dt = currentTimeSeconds - lastTimeSeconds;
            lastTimeSeconds = currentTimeSeconds;
        } else {

            // Unlike other controllers, things can go wrong if we don't do this
            dt = 0.02;
        }

        // Convert the position to radians
        double encoderPositionRadians = Units.degreesToRadians(encoderPositionDegrees);

        // Get the next motion profile step
        lastProfiledReference = (new TrapezoidProfile(motionConstraints, goal, lastProfiledReference)).calculate(dt);

        // Set the reference
        loop.setNextR(lastProfiledReference.position, lastProfiledReference.velocity);

        // Correct the Kalman filter's state vector estimate
        loop.correct(VecBuilder.fill(encoderPositionRadians));

        // Update LQR to predict next state
        loop.predict(dt);

        // Return the calculated voltage
        return loop.getU(0);

    }
}