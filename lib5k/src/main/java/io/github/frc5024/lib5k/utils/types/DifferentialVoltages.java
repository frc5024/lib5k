package io.github.frc5024.lib5k.utils.types;

import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;

/**
 * Reperesents two differential voltages named left, and right. This should be
 * used to express DriveTrain outputs
 */
public class DifferentialVoltages {

    // Voltages
    private double left;
    private double right;

    /**
     * Create new DifferentialVoltages with no voltage
     */
    public DifferentialVoltages() {
        this(0);
    }

    /**
     * Create new DifferentialVoltages
     * 
     * @param volts Voltage
     */
    public DifferentialVoltages(double volts) {
        this(volts, volts);
    }

    /**
     * Create new DifferentialVoltages
     * 
     * @param left  Left volts
     * @param right Right volts
     */
    public DifferentialVoltages(double left, double right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Get the left voltage
     * 
     * @return Left voltage
     */
    public double getLeftVolts() {
        return left;
    }

    /**
     * Get the right voltage
     * 
     * @return Right voltage
     */
    public double getRightVolts() {
        return right;
    }

    /**
     * Get the left voltage as a percentage of the current robot bus voltage
     * 
     * @return Left voltage percent
     */
    public double getLeftBusPercentage() {
        return getLeftVolts() / RR_HAL.getSimSafeVoltage();
    }

    /**
     * Get the right voltage as a percentage of the current robot bus voltage
     * 
     * @return Right voltage percent
     */
    public double getRightBusPercentage() {
        return getRightVolts() / RR_HAL.getSimSafeVoltage();
    }

    /**
     * Get the left voltage as a percentage of the maximum robot bus voltage
     * 
     * @return Left voltage percent
     */
    public double getLeftPercentage() {
        return getLeftVolts() / RR_HAL.MAXIMUM_BUS_VOLTAGE;
    }

    /**
     * Get the right voltage as a percentage of the maximum robot bus voltage
     * 
     * @return Right voltage percent
     */
    public double getRightPercentage() {
        return getRightVolts() / RR_HAL.MAXIMUM_BUS_VOLTAGE;
    }

    /**
     * Multiply this DifferentialVoltages by a number of volts. THIS WILL MODIFY THE
     * OBJECT CONTENTS.
     * 
     * @param volts Volts to multiply by
     * @return this
     */
    public DifferentialVoltages times(double volts) {

        // Multiply the voltages
        this.left *= volts;
        this.right *= volts;

        return this;
    }

    /**
     * Multiply this DifferentialVoltages by another DifferentialVoltages object.
     * THIS WILL MODIFY THE OBJECT CONTENTS.
     * 
     * @param volts DifferentialVoltages
     * @return this
     */
    public DifferentialVoltages times(DifferentialVoltages other) {

        // Multiply the voltages
        this.left *= other.getLeftVolts();
        this.right *= other.getRightVolts();

        return this;
    }

    /**
     * Normalize the voltages below the maximum robot bus voltage. THIS WILL MODIFY
     * THE OBJECT CONTENTS.
     * 
     * @return this
     */
    public DifferentialVoltages normalize() {

        // Find the maximum magnitude between both voltages
        double magnitude = Math.max(Math.abs(getLeftPercentage()), Math.abs(getRightPercentage()));

        // Scale back motors if the max magnitude is greater than the max output (1.0)
        if (magnitude > 1.) {
            this.left = (this.left / magnitude);
            this.right = (this.right / magnitude);
        }

        return this;
    }

    /**
     * Create new DifferentialVoltages from a throttle and steering value
     * 
     * @param throttlePercentage Throttle percent
     * @param steeringPercentage Steering percent
     * @return new DifferentialVoltages
     */
    public static DifferentialVoltages fromThrottleAndSteering(double throttlePercentage, double steeringPercentage) {
        return new DifferentialVoltages(throttlePercentage + steeringPercentage,
                throttlePercentage - steeringPercentage).times(RR_HAL.MAXIMUM_BUS_VOLTAGE);
    }

}