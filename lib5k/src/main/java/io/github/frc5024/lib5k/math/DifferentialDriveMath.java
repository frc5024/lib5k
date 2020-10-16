package io.github.frc5024.lib5k.math;

import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.utils.types.DifferentialVoltages;

/**
 * Utils for working with tank drives
 */
public class DifferentialDriveMath {

    /**
     * Compute tank voltages for constant curvature output
     * 
     * @param throttle Throttle percent
     * @param steering Steering percent
     * @return DifferentialVoltages
     */
    public static DifferentialVoltages computeConstantCurvatureOutputs(double throttle, double steering) {

        // Calculate constant-curvature speeds
        double l = (throttle + Math.abs(throttle) * steering);
        double r = (throttle - Math.abs(throttle) * steering);

        return new DifferentialVoltages(l, r).times(RR_HAL.MAXIMUM_BUS_VOLTAGE);
    }

    /**
     * Compute tank voltages for arcade output
     * 
     * @param throttle Throttle percent
     * @param steering Steering percent
     * @return DifferentialVoltages
     */
    public static DifferentialVoltages computeArcadeOutputs(double throttle, double steering) {

        // Calculate arcade speeds
        double l = throttle + steering;
        double r = throttle - steering;

        return new DifferentialVoltages(l, r).times(RR_HAL.MAXIMUM_BUS_VOLTAGE);
    }

    /**
     * Compute tank voltages for semi-constant curvature output
     * 
     * @param throttle Throttle percent
     * @param steering Steering percent
     * @return DifferentialVoltages
     */
    public static DifferentialVoltages computeSemiConstantCurvatureOutputs(double throttle, double steering) {

        // Get both constant curvature and arcade outputs
        DifferentialVoltages constantCurvatureVoltages = computeConstantCurvatureOutputs(throttle, steering);
        DifferentialVoltages arcadeVoltages = computeArcadeOutputs(throttle, steering);

        // Get the averages
        double l = (constantCurvatureVoltages.getLeftVolts() + arcadeVoltages.getLeftVolts()) / 2;
        double r = (constantCurvatureVoltages.getRightVolts() + arcadeVoltages.getRightVolts()) / 2;

        return new DifferentialVoltages(l, r);
    }

}