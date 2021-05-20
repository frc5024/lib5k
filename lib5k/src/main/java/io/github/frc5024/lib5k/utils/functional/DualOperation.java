package io.github.frc5024.lib5k.utils.functional;

/**
 * A functional interface for an operation involving two objects of the same
 * type
 * 
 * @param <T> Object type
 */
@FunctionalInterface
public interface DualOperation<T> extends TwoTypeDualOperation<T, T, T> {

}