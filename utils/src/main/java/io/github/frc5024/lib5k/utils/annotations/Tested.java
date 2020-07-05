package io.github.frc5024.lib5k.utils.annotations;

/**
 * Indicates that a class or method was successfully used by a team on a robot
 */
public @interface Tested {

    /**
     * The team that tested this component
     */
    int team() default 5024;

    /**
     * A note about testing
     */
    String note() default "";
    
}