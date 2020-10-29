package io.github.frc5024.lib5k.utils.math.average;

import java.util.LinkedList;

import javax.annotation.CheckForNull;

import io.github.frc5024.lib5k.utils.MathWrapper;

public class MovingAverage<T> {

    // Operations
    private final MathWrapper<T> operationWrapper;

    // List of values
    private LinkedList<T> values;

    // Index in list
    private final int size;
    private int idx = 0;

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

    public int getUsage() {
        int used = 0;
        for (T t : values) {
            if (t != null) {
                used++;
            }
        }
        return used;
    }

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