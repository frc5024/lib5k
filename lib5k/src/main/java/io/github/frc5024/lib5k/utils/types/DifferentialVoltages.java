package io.github.frc5024.lib5k.utils.types;

import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;

public class DifferentialVoltages {

    private double left;
    private double right;

    public DifferentialVoltages(double left, double right) {
        this.left = left;
        this.right = right;
    }

    public double getLeftVolts() {
        return left;
    }

    public double getRightVolts() {
        return right;
    }

    public double getLeftBusPercentage() {
        return getLeftVolts() / RR_HAL.getSimSafeVoltage();
    }

    public double getRightBusPercentage() {
        return getRightVolts() / RR_HAL.getSimSafeVoltage();
    }

    public double getLeftPercentage() {
        return getLeftVolts() / RR_HAL.MAXIMUM_BUS_VOLTAGE;
    }

    public double getRightPercentage() {
        return getRightVolts() / RR_HAL.MAXIMUM_BUS_VOLTAGE;
    }

    public DifferentialVoltages times(double volts) {

        // Multiply the voltages
        this.left *= volts;
        this.right *= volts;

        return this;
    }

    public DifferentialVoltages times(DifferentialVoltages other) {

        // Multiply the voltages
        this.left *= other.getLeftVolts();
        this.right *= other.getRightVolts();

        return this;
    }

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

    public static DifferentialVoltages fromThrottleAndSteering(double throttlePercentage, double steeringPercentage) {
        return new DifferentialVoltages(throttlePercentage + steeringPercentage,
                throttlePercentage - steeringPercentage).times(RR_HAL.MAXIMUM_BUS_VOLTAGE);
    }

}