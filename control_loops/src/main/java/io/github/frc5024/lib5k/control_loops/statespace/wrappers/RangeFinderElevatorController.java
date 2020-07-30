package io.github.frc5024.lib5k.control_loops.statespace.wrappers;

import edu.wpi.first.wpilibj.util.Units;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;

/**
 * This is a wrapper around a state space plant, observer, motion profiling, and
 * LQR. The EncoderElevatorController class is designed to allow grade 9, and
 * new programmers to make use of state space-based elevator control without
 * having to worry about the actual math happening BTS.
 * 
 * This controller is specially for controlling elevators that use a
 * rangefinder. Use of a rangefinder is recommended by many teams.
 * 
 * See for information on the math going on here:
 * https://file.tavsys.net/control/controls-engineering-in-frc.pdf#%5B%7B%22num%22%3A39%2C%22gen%22%3A0%7D%2C%7B%22name%22%3A%22XYZ%22%7D%2C85.04%2C391.42%2Cnull%5D
 */
public class RangeFinderElevatorController extends EncoderElevatorController {

    /**
     * Create a RangeFinderElevatorController
     * 
     * @param motorType                         The type of motor(s) used to drive
     *                                          the flywheel
     * @param carriageMassKg                    The mass of the carriage in Kg
     * @param carriageMaxVelocityMPS            The maximum upward velocity of the
     *                                          carriage at max voltage
     * @param carriageMaxAccelerationMPSSquared The maximum upward acceleration of
     *                                          the carriage at max voltage as m/s^2
     * @param positionEpsilonM                  Position epsilon in meters
     * @param velocityEpsilonMPS                Velocity epsilon in meters/second
     */
    public RangeFinderElevatorController(DCBrushedMotor motorType, double carriageMassKg, double carriageMaxVelocityMPS,
            double carriageMaxAccelerationMPSSquared, double positionEpsilonM, double velocityEpsilonMPS) {
        this(motorType, carriageMassKg, carriageMaxVelocityMPS, carriageMaxAccelerationMPSSquared,
                Units.inchesToMeters(2), Units.inchesToMeters(40), 0.01, 0.02, positionEpsilonM, velocityEpsilonMPS,
                12.0);
    }

    /**
     * Create a RangeFinderElevatorController
     * 
     * @param motorType                         The type of motor(s) used to drive
     *                                          the flywheel
     * @param carriageMassKg                    The mass of the carriage in Kg
     * @param carriageMaxVelocityMPS            The maximum upward velocity of the
     *                                          carriage at max voltage
     * @param carriageMaxAccelerationMPSSquared The maximum upward acceleration of
     *                                          the carriage at max voltage as m/s^2
     * @param modelPositionAccuracyM            How accurate we think the model's
     *                                          position control is in meters
     * @param modalVelocityAccuracyMPS          How accurate we think the model's
     *                                          velocity control is in meters/second
     * @param rangefinderAccuracy               The accuracy of the rangefinder in
     *                                          meters
     * @param expectedLoopTimeSeconds           The loop period of the caller in
     *                                          seconds. This is generally 0.02
     *                                          (20ms)
     * @param positionEpsilonM                  Position epsilon in meters
     * @param velocityEpsilonMPS                Velocity epsilon in meters/second
     * @param maxVoltageOutput                  The maximum voltage to output. This
     *                                          is generally 12.0
     */
    public RangeFinderElevatorController(DCBrushedMotor motorType, double carriageMassKg, double carriageMaxVelocityMPS,
            double carriageMaxAccelerationMPSSquared, double modelPositionAccuracyM, double modalVelocityAccuracyMPS,
            double rangefinderAccuracy, double expectedLoopTimeSeconds, double positionEpsilonM,
            double velocityEpsilonMPS, double maxVoltageOutput) {
        super(motorType, carriageMassKg, 0.0, 0.0, carriageMaxVelocityMPS, carriageMaxAccelerationMPSSquared,
                modelPositionAccuracyM, modalVelocityAccuracyMPS, rangefinderAccuracy, expectedLoopTimeSeconds,
                positionEpsilonM, velocityEpsilonMPS, maxVoltageOutput);

    }

    /**
     * Calculate the controller output
     * 
     * @param encoderDistanceM Distance reading from rangefinder
     * @return Voltage output
     */
    @Override
    public double computeVoltage(double rangefinderDistance) {
        // For now, this just renames the variable, and adds a new JavaDoc
        return super.computeVoltage(rangefinderDistance);
    }
}