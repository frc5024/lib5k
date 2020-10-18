package io.github.frc5024.lib5k.utils.json.types;

import io.github.frc5024.lib5k.control_loops.ExtendedPIDController;

/**
 * A JSON representation of the parameters needed to build a PID controller
 */
public class PIDValues {
    public double Kp;
    public double Ki;
    public double Kd;
    public double FF;
    public double restTime;

    /**
     * Build the controller
     * 
     * @return ExtendedPIDController
     */
    public ExtendedPIDController build() {
        return new ExtendedPIDController(Kp, Ki, Kd, FF, restTime);
    }
}