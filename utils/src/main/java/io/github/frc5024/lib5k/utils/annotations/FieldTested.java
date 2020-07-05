package io.github.frc5024.lib5k.utils.annotations;

/**
 * Indicates that a class or method was successfully used by a team on an FRC field (on or off season)
 */
public @interface FieldTested {

    /**
     * The team that tested this component
     */
    int team() default 5024;

    /**
     * The year this component was tested
     */
    int year();
}