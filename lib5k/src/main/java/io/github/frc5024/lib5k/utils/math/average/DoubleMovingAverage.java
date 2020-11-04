package io.github.frc5024.lib5k.utils.math.average;

import io.github.frc5024.lib5k.utils.MathWrapper;

/**
 * A MovingAverage for Doubles
 */
public class DoubleMovingAverage extends MovingAverage<Double> {

    /**
     * Create a MovingAverage for Doubles
     * 
     * @param size Sample size
     */
    public DoubleMovingAverage(int size) {
        super(size, new MathWrapper<>((a, b) -> {
            return a + b;
        }, (a, b) -> {
            return a - b;
        }, (a, b) -> {
            return a * b;
        }, (a, b) -> {
            return a / b;
        }));
    }

}