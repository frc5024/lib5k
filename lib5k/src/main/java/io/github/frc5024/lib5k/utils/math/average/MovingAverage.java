package io.github.frc5024.lib5k.utils.math.average;

import java.util.LinkedList;

import javax.annotation.CheckForNull;

import io.github.frc5024.lib5k.utils.MathWrapper;

/**
 * A tool for calculating the average of multiple objects
 * 
 * @param <T> Object type
 */
public class MovingAverage<T> {

    // Operations
    private final MathWrapper<T> operationWrapper;

    // List of values
    private LinkedList<T> values;

    // Index in list
    private final int size;
    private int idx = 0;

    /**
     * Create a MovingAverage
     * 
     * @param size             Number of objects to compare
     * @param operationWrapper Collection of mathematical operators to use in the
     *                         calculation
     */
    public MovingAverage(int size, MathWrapper<T> operationWrapper) {
        this.operationWrapper = operationWrapper;

        // Build list
        this.values = new LinkedList<>();
        this.size = size;

        // Fill list
        for (int i = 0; i < this.values.size(); i++) {
            this.values.add(null);
        }
    }

    /**
     * Add a new object to the average, replacing the oldest object if there is no
     * more room
     * 
     * @param value New value
     */
    public void add(T value) {

        if (size == 0) {
            return;
        }

        // Set the value at the index
        this.values.set(idx, value);

        // Move the index forward
        idx++;
        idx %= this.size;

    }

    /**
     * Get how many objects are currently in the buffer
     * 
     * @return Number of objects being averaged
     */
    public int getUsage() {
        int used = 0;
        for (T t : values) {
            if (t != null) {
                used++;
            }
        }
        return used;
    }

    /**
     * Get the average object of the buffer
     * 
     * @return Average object
     */
    public @CheckForNull T getAverage() {

        if (getUsage() == 0 || size == 0) {
            return null;
        }

        // Accumulate T
        T lastValue = values.get(0);

        for (T value : values) {
            if (value != null && value != lastValue) {
                lastValue = operationWrapper.add(value, lastValue);
            }
        }

        // Return avg
        return operationWrapper.divide(lastValue, getUsage());

    }

}