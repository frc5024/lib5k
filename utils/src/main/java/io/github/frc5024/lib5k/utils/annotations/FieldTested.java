package io.github.frc5024.lib5k.utils.annotations;

import java.lang.annotation.Documented;

/**
 * Indicates that a class or method was successfully used by a team on an FRC
 * field (on or off season)
 */
@Documented
public @interface FieldTested {

    /**
     * The team that tested this component
     * 
     * @return Team that tested this
     */
    int team() default 5024;

    /**
     * The year this component was tested
     * 
     * @return Year this was tested
     */
    int year();
}