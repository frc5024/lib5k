package io.github.frc5024.lib5k.utils;

import io.github.frc5024.lib5k.utils.functional.DualOperation;
import io.github.frc5024.lib5k.utils.functional.TwoTypeDualOperation;

/**
 * Wraps mathematical operations. This is used to get around Multiple
 * Inheritance in classes.
 * 
 * @param <T> Value type
 */
public class MathWrapper<T> {

    // Operations
    private final DualOperation<T> additionOperation;
    private final DualOperation<T> subtractionOperation;
    private final TwoTypeDualOperation<T, Double, T> multiplicationOperation;
    private final TwoTypeDualOperation<T, Double, T> divisionOperation;

    /**
     * Create a MethWrapper
     * 
     * @param additionOperation       Function to add
     * @param subtractionOperation    Function to subtract
     * @param multiplicationOperation Function to multiply
     * @param divisionOperation       Function to divide
     */
    public MathWrapper(DualOperation<T> additionOperation, DualOperation<T> subtractionOperation,
            TwoTypeDualOperation<T, Double, T> multiplicationOperation,
            TwoTypeDualOperation<T, Double, T> divisionOperation) {
        this.additionOperation = additionOperation;
        this.subtractionOperation = subtractionOperation;
        this.multiplicationOperation = multiplicationOperation;
        this.divisionOperation = divisionOperation;
    }

    /**
     * Add value a to value b
     * 
     * @param a Value a
     * @param b Value b
     * @return New value
     */
    public T add(T a, T b) {
        return additionOperation.operate(a, b);
    }

    /**
     * Subtract value b from value a
     * 
     * @param a Value a
     * @param b Value b
     * @return New value
     */
    public T subtract(T a, T b) {
        return subtractionOperation.operate(a, b);
    }

    /**
     * Multiply a value by a scalar
     * 
     * @param value  Value
     * @param scalar Scalar
     * @return New value
     */
    public T multiply(T value, double scalar) {
        return multiplicationOperation.operate(value, scalar);
    }

    /**
     * Divide a value by a scalar
     * 
     * @param value  Value
     * @param scalar Scalar
     * @return New value
     */
    public T divide(T value, double scalar) {
        return divisionOperation.operate(value, scalar);
    }

}