package io.github.frc5024.lib5k.utils.functional;

/**
 * A functional interface for an operation involving two objects of different
 * types
 * 
 * @param <A> First type
 * @param <B> Second type
 * @param <R> Return type
 */
@FunctionalInterface
public interface TwoTypeDualOperation<A, B, R> {

    /**
     * Operate on the objects
     * 
     * @param a Object A
     * @param b Object B
     * @return Result
     */
    public R operate(A a, B b);
}