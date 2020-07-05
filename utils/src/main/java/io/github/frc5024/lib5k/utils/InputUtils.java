package io.github.frc5024.lib5k.utils;

import io.github.frc5024.lib5k.utils.annotations.FieldTested;

/**
 * Utils for working with drive inputs
 */
@FieldTested(year=2020)
public class InputUtils {

    /**
     * Scale type / mode
     */
    public enum ScalingMode {
        LINEAR, SQUARED, CUBIC;
    }

    /**
     * Scale a value
     * 
     * @param val  Value
     * @param mode Type of scale
     * @return Scaled value
     */
    public static double scale(double val, ScalingMode mode) {

        // Handle each mode
        switch (mode) {
        case LINEAR:
            return val;

        case SQUARED:
            return Math.copySign(val * val, val);

        case CUBIC:
            return val * val * val;

        default:
            return val;

        }
    }
}