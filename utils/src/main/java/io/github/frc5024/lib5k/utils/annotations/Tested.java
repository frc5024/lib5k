package io.github.frc5024.lib5k.utils.annotations;

import java.lang.annotation.Documented;

/**
 * Indicates that a class or method was successfully used by a team on a robot
 */
@Documented
public @interface Tested {

    /**
     * The team that tested this component
     * 
     * @return Team that tested this
     */
    int team() default 5024;

    /**
     * A note about testing
     * 
     * @return any testing notes
     */
    String note() default "";

}