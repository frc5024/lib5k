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

import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.controller.LinearQuadraticRegulator;
import edu.wpi.first.wpilibj.estimator.KalmanFilter;
import edu.wpi.first.wpilibj.system.LinearSystem;
import edu.wpi.first.wpilibj.system.LinearSystemLoop;
import edu.wpi.first.wpilibj.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpiutil.math.Nat;
import edu.wpi.first.wpiutil.math.VecBuilder;
import edu.wpi.first.wpiutil.math.numbers.N1;
import edu.wpi.first.wpiutil.math.numbers.N2;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

/**
 * This is a wrapper around a state space plant, observer, motion profiling, and
 * LQR. The EncoderElevatorController class is designed to allow grade 9, and
 * new programmers to make use of state space-based elevator control without
 * having to worry about the actual math happening BTS.
 * 
 * This controller is specially for controlling elevators that use an encoder.
 * 
 * See for information on the math going on here:
 * https://file.tavsys.net/control/controls-engineering-in-frc.pdf#%5B%7B%22num%22%3A39%2C%22gen%22%3A0%7D%2C%7B%22name%22%3A%22XYZ%22%7D%2C85.04%2C391.42%2Cnull%5D
 */
public class EncoderElevatorController {

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
     * Create an EncoderElevatorController
     * 
     * @param motorType                         The type of motor(s) used to drive
     *                                          the flywheel
     * @param carriageMassKg                    The mass of the carriage in Kg
     * @param drumRadiusM                       The radius of the elevator drum in M
     * @param gearRatio                         reduction between motors and
     *                                          encoder, as output over input. If
     *                                          the elevator spins slower than the
     *                                          motors, this number should be
     *                                          greater than one. IGNORE THIS IF THE
     *                                          ENCODER IS ATTACHED TO THE MOTOR
     * @param carriageMaxVelocityMPS            The maximum upward velocity of the
     *                                          carriage at max voltage
     * @param carriageMaxAccelerationMPSSquared The maximum upward acceleration of
     *                                          the carriage at max voltage
     * @param positionEpsilonM                  Position epsilon in meters
     */
    public EncoderElevatorController(DCBrushedMotor motorType, double carriageMassKg, double drumRadiusM,
            double gearRatio, double carriageMaxVelocityMPS, double carriageMaxAccelerationMPSSquared,
            double positionEpsilonM) {
        this(motorType, carriageMassKg, drumRadiusM, gearRatio, carriageMaxVelocityMPS,
                carriageMaxAccelerationMPSSquared, Units.inchesToMeters(2), Units.inchesToMeters(40), 0.001, 0.02,
                positionEpsilonM, Units.inchesToMeters(10.0), 12.0);
    }

    /**
     * Create an EncoderElevatorController
     * 
     * @param motorType                         The type of motor(s) used to drive
     *                                          the flywheel
     * @param carriageMassKg                    The mass of the carriage in Kg
     * @param drumRadiusM                       The radius of the elevator drum in M
     * @param gearRatio                         reduction between motors and
     *                                          encoder, as output over input. If
     *                                          the elevator spins slower than the
     *                                          motors, this number should be
     *                                          greater than one. IGNORE THIS IF THE
     *                                          ENCODER IS ATTACHED TO THE MOTOR
     * @param carriageMaxVelocityMPS            The maximum upward velocity of the
     *                                          carriage at max voltage
     * @param carriageMaxAccelerationMPSSquared The maximum upward acceleration of the
     *                                          carriage at max voltage
     * @param positionEpsilonM                  Position epsilon in meters
     * @param velocityEpsilonMPS                Velocity epsilon in meters/second
     */
    public EncoderElevatorController(DCBrushedMotor motorType, double carriageMassKg, double drumRadiusM,
            double gearRatio, double carriageMaxVelocityMPS, double carriageMaxAccelerationMPSSquared,
            double positionEpsilonM, double velocityEpsilonMPS) {
        this(motorType, carriageMassKg, drumRadiusM, gearRatio, carriageMaxVelocityMPS,
                carriageMaxAccelerationMPSSquared, Units.inchesToMeters(2), Units.inchesToMeters(40), 0.001, 0.02,
                positionEpsilonM, velocityEpsilonMPS, 12.0);
    }

    /**
     * Create an EncoderElevatorController
     * 
     * @param motorType                         The type of motor(s) used to drive
     *                                          the elevator
     * @param carriageMassKg                    The mass of the carriage in Kg
     * @param drumRadiusM                       The radius of the elevator drum in M
     * @param gearRatio                         reduction between motors and
     *                                          encoder, as output over input. If
     *                                          the elevator spins slower than the
     *                                          motors, this number should be
     *                                          greater than one. IGNORE THIS IF THE
     *                                          ENCODER IS ATTACHED TO THE MOTOR
     * @param carriageMaxVelocityMPS            The maximum upward velocity of the
     *                                          carriage at max voltage
     * @param carriageMaxAccelerationMPSSquared The maximum upward acceleration of
     *                                          the carriage at max voltage as m/s^2
     * @param modelPositionAccuracyM            How accurate we think the model's
     *                                          position control is in meters
     * @param modalVelocityAccuracyMPS          How accurate we think the model's
     *                                          velocity control is in meters/second
     * @param encoderAccuracy                   How accurate the encoder is in
     *                                          meters
     * @param expectedLoopTimeSeconds           The loop period of the caller in
     *                                          seconds. This is generally 0.02
     *                                          (20ms)
     * @param positionEpsilonM                  Position epsilon in meters
     * @param velocityEpsilonMPS                Velocity epsilon in meters/second
     * @param maxVoltageOutput                  The maximum voltage to output. This
     *                                          is generally 12.0
     */
    public EncoderElevatorController(DCBrushedMotor motorType, double carriageMassKg, double drumRadiusM,
            double gearRatio, double carriageMaxVelocityMPS, double carriageMaxAccelerationMPSSquared,
            double modelPositionAccuracyM, double modalVelocityAccuracyMPS, double encoderAccuracy,
            double expectedLoopTimeSeconds, double positionEpsilonM, double velocityEpsilonMPS,
            double maxVoltageOutput) {

        // Set up motion profile constraints
        motionConstraints = new TrapezoidProfile.Constraints(carriageMaxVelocityMPS, carriageMaxAccelerationMPSSquared);

        // Create a plant
        plant = LinearSystemId.createElevatorSystem(motorType, carriageMassKg, drumRadiusM, gearRatio);

        // Create an observer
        observer = new KalmanFilter<>(Nat.N2(), Nat.N1(), plant,
                VecBuilder.fill(modelPositionAccuracyM, modalVelocityAccuracyMPS), VecBuilder.fill(encoderAccuracy),
                expectedLoopTimeSeconds);

        // Create LQR
        lqr = new LinearQuadraticRegulator<N2, N1, N1>(plant, VecBuilder.fill(positionEpsilonM, velocityEpsilonMPS),
                VecBuilder.fill(maxVoltageOutput), expectedLoopTimeSeconds);

        // Create a loop
        loop = new LinearSystemLoop<>(plant, lqr, observer, maxVoltageOutput, expectedLoopTimeSeconds);

    }

    /**
     * Reset the controller
     * 
     * @param encoderDistanceM Current encoder distance reading
     */
    public void reset(double encoderDistanceM) {

        // Reset statespace loop
        loop.reset(VecBuilder.fill(encoderDistanceM, 0.0));

        // Reset the last profile state
        lastProfiledReference = new TrapezoidProfile.State(encoderDistanceM, 0.0);

    }

    /**
     * Set the elevator's desired height
     * 
     * @param meters Desired height
     */
    public void setDesiredHeight(double meters) {

        // Set a position goal with no velocity
        goal = new TrapezoidProfile.State(meters, 0.0);
    }

    /**
     * Calculate the controller output
     * 
     * @param encoderDistanceM Distance reading from encoder
     * @return Voltage output
     */
    public double computeVoltage(double encoderDistanceM) {

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

        // Get the next motion profile step
        lastProfiledReference = (new TrapezoidProfile(motionConstraints, goal, lastProfiledReference)).calculate(dt);

        // Set the reference
        loop.setNextR(lastProfiledReference.position, lastProfiledReference.velocity);

        // Correct the Kalman filter's state vector estimate
        loop.correct(VecBuilder.fill(encoderDistanceM));

        // Update LQR to predict next state
        loop.predict(dt);

        // Return the calculated voltage
        return loop.getU(0);
    }

}