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
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import edu.wpi.first.wpilibj.system.LinearSystem;
import edu.wpi.first.wpilibj.system.LinearSystemLoop;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpiutil.math.Matrix;
import edu.wpi.first.wpiutil.math.Nat;
import edu.wpi.first.wpiutil.math.VecBuilder;
import edu.wpi.first.wpiutil.math.numbers.N1;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.control_loops.statespace.util.easylqr.FlywheelMath;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

/**
 * This is a wrapper around a state space plant, observer, and LQR. The
 * SimpleFlywheelController class is designed to allow grade 9, and new
 * programmers to make use of state space-based flywheel control without having
 * to worry about the actual math happening BTS.
 * 
 * See for information on the math going on here:
 * https://file.tavsys.net/control/controls-engineering-in-frc.pdf#%5B%7B%22num%22%3A40%2C%22gen%22%3A0%7D%2C%7B%22name%22%3A%22XYZ%22%7D%2C85.04%2C265.56%2Cnull%5D
 */
public class SimpleFlywheelController {

    // Amount of noise to simulate
    private static final Matrix<N1, N1> SIMULATED_NOISE = VecBuilder.fill(0.01);

    // Plant, observer, and LQR
    private LinearSystem<N1, N1, N1> plant;
    private KalmanFilter<N1, N1, N1> observer;
    private LinearQuadraticRegulator<N1, N1, N1> lqr;

    // Simulator
    private FlywheelSim simulator;

    // State space loop
    private LinearSystemLoop<N1, N1, N1> loop;

    // Timekeeping
    private double lastTimeSeconds = 0.0;

    /**
     * Create a SimpleFlywheelController
     * 
     * @param motorType         The type of motor(s) used to drive the flywheel
     * @param launcherMassKg    The mass in kg of all the wheels that come in
     *                          contact with the launched object
     * @param launcherDiameterM The diameter in m of one of the wheels that come in
     *                          contact with the launched object
     * @param maxVelocityRPM    The realistic maximum velocity of the flywheel when
     *                          the maximum voltage is applied. This must NOT be the
     *                          velocity written on the motor datasheet, and should
     *                          be measured via an encoder or tacheometer.
     * @param gearing           reduction between motors and encoder, as output over
     *                          input. If the flywheel spins slower than the motors,
     *                          this number should be greater than one. IGNORE THIS
     *                          IF THE ENCODER IS ATTACHED TO THE MOTOR
     * @param maxVoltageOutput  The maximum voltage to output. This is generally
     *                          12.0
     * @param epsilonRPM        Epsilon in RPM. A good starting point is probably
     *                          80-100
     */
    public SimpleFlywheelController(DCBrushedMotor motorType, double launcherMassKg, double launcherDiameterM,
            double maxVelocityRPM, double gearing, double maxVoltageOutput, double epsilonRPM) {
        this(motorType, launcherMassKg, launcherDiameterM, 0.0, 0.0, maxVelocityRPM, gearing, maxVoltageOutput,
                epsilonRPM);
    }

    /**
     * Create a SimpleFlywheelController
     * 
     * @param motorType         The type of motor(s) used to drive the flywheel
     * @param launcherMassKg    The mass in kg of all the wheels that come in
     *                          contact with the launched object
     * @param launcherDiameterM The diameter in m of one of the wheels that come in
     *                          contact with the launched object
     * @param flywheelMassKg    The mass in kg of the extra weight added to the
     *                          flywheel for momentum (0.0 if none attached)
     * @param flywheelDiameterM The diameter in m of the extra weight added to the
     *                          flywheel for momentum (0.0 if none attached)
     * @param maxVelocityRPM    The realistic maximum velocity of the flywheel when
     *                          the maximum voltage is applied. This must NOT be the
     *                          velocity written on the motor datasheet, and should
     *                          be measured via an encoder or tacheometer.
     * @param gearing           reduction between motors and encoder, as output over
     *                          input. If the flywheel spins slower than the motors,
     *                          this number should be greater than one. IGNORE THIS
     *                          IF THE ENCODER IS ATTACHED TO THE MOTOR
     * @param maxVoltageOutput  The maximum voltage to output. This is generally
     *                          12.0
     * @param epsilonRPM        Epsilon in RPM. A good starting point is probably
     *                          80-100
     */
    public SimpleFlywheelController(DCBrushedMotor motorType, double launcherMassKg, double launcherDiameterM,
            double flywheelMassKg, double flywheelDiameterM, double maxVelocityRPM, double gearing,
            double maxVoltageOutput, double epsilonRPM) {
        this(motorType, launcherMassKg, launcherDiameterM, flywheelMassKg, flywheelDiameterM, maxVelocityRPM, gearing,
                Units.radiansPerSecondToRotationsPerMinute(3.0), Units.radiansPerSecondToRotationsPerMinute(0.01), 0.02,
                maxVoltageOutput, epsilonRPM);
    }

    /**
     * Create a SimpleFlywheelController
     * 
     * @param motorType               The type of motor(s) used to drive the
     *                                flywheel
     * @param launcherMassKg          The mass in kg of all the wheels that come in
     *                                contact with the launched object
     * @param launcherDiameterM       The diameter in m of one of the wheels that
     *                                come in contact with the launched object
     * @param flywheelMassKg          The mass in kg of the extra weight added to
     *                                the flywheel for momentum (0.0 if none
     *                                attached)
     * @param flywheelDiameterM       The diameter in m of the extra weight added to
     *                                the flywheel for momentum (0.0 if none
     *                                attached)
     * @param maxVelocityRPM          The realistic maximum velocity of the flywheel
     *                                when the maximum voltage is applied. This must
     *                                NOT be the velocity written on the motor
     *                                datasheet, and should be measured via an
     *                                encoder or tacheometer.
     * @param gearing                 reduction between motors and encoder, as
     *                                output over input. If the flywheel spins
     *                                slower than the motors, this number should be
     *                                greater than one. IGNORE THIS IF THE ENCODER
     *                                IS ATTACHED TO THE MOTOR
     * @param modelAccuracy           How accurate we think the model is in RPM
     * @param encoderAccuracy         How accurate we think the encoder is in RPM
     * @param expectedLoopTimeSeconds The loop period of the caller in seconds. This
     *                                is generally 0.02 (20ms)
     * @param maxVoltageOutput        The maximum voltage to output. This is
     *                                generally 12.0
     * @param epsilonRPM              Epsilon in RPM. A good starting point is
     *                                probably 80-100
     */
    public SimpleFlywheelController(DCBrushedMotor motorType, double launcherMassKg, double launcherDiameterM,
            double flywheelMassKg, double flywheelDiameterM, double maxVelocityRPM, double gearing,
            double modelAccuracy, double encoderAccuracy, double expectedLoopTimeSeconds, double maxVoltageOutput,
            double epsilonRPM) {

        // Calculate flywheel J
        double J = FlywheelMath.calculateJ(launcherMassKg, launcherDiameterM, flywheelMassKg, flywheelDiameterM);

        // Build a plant
        plant = LinearSystemId.createFlywheelSystem(motorType, J, gearing);

        // Build an observer
        observer = new KalmanFilter<>(Nat.N1(), Nat.N1(), plant,
                VecBuilder.fill(Units.rotationsPerMinuteToRadiansPerSecond(modelAccuracy)),
                VecBuilder.fill(Units.rotationsPerMinuteToRadiansPerSecond(encoderAccuracy)), expectedLoopTimeSeconds);

        // Convert eps to RAD/s
        double epsilonRADS = Units.rotationsPerMinuteToRadiansPerSecond(epsilonRPM);

        // Build LQR
        lqr = new LinearQuadraticRegulator<N1, N1, N1>(plant, VecBuilder.fill(epsilonRADS),
                VecBuilder.fill(maxVoltageOutput), 0.020);

        // Build loop
        loop = new LinearSystemLoop<N1, N1, N1>(plant, lqr, observer, maxVoltageOutput, expectedLoopTimeSeconds);

        // Set up simulation
        this.simulator = new FlywheelSim(plant, (DCMotor) motorType, gearing, SIMULATED_NOISE);
    }

    /**
     * Reset the system
     * 
     * @param currentRPM Current encoder velocity in RPM
     */
    public void reset(double currentRPM) {
        loop.reset(VecBuilder.fill(Units.rotationsPerMinuteToRadiansPerSecond(currentRPM)));
    }

    /**
     * Stop the flywheel
     */
    public void stop() {
        setDesiredVelocity(0.0);
    }

    /**
     * Set the desired velocity
     * 
     * @param rpm Desired RPM
     */
    public void setDesiredVelocity(double rpm) {

        // Convert to RAD/s
        double rads = Units.rotationsPerMinuteToRadiansPerSecond(rpm);

        // Set the next reference
        loop.setNextR(VecBuilder.fill(rads));

    }

    /**
     * Get the simulated system RPM
     * 
     * @return Simulated RPM
     */
    public double getSimulatedRPN() {
        return this.simulator.getAngularVelocityRPM();
    }

    /**
     * Compute the voltage to send to the motor
     * 
     * @param currentRPM Current velocity in RPM
     * @return Output voltage
     */
    public double computeNextVoltage(double currentRPM) {

        // Calculate DT
        double currentTimeSeconds = FPGAClock.getFPGASeconds();
        double dt = currentTimeSeconds - lastTimeSeconds;
        lastTimeSeconds = currentTimeSeconds;

        // Convert current velocity to RAD/s
        double currentRADS = Units.rotationsPerMinuteToRadiansPerSecond(currentRPM);

        // Correct the Kalman filter to estimate a more accurate state based on encoder
        // data
        loop.correct(VecBuilder.fill(currentRADS));

        // Predict the next state using the Kalman filter, and generate a new voltage
        // with LQR
        loop.predict(dt);

        // Return the new voltage
        return loop.getU(0);
    }

}