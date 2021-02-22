package io.github.frc5024.common_drive.controller;

@Deprecated(since = "October 2020", forRemoval = true)
public interface BaseController {

    /**
     * Calculate a new controller output
     * 
     * @param error Controller error
     * @param dt    Time since last call
     * @return New output
     */
    public double calculate(double error, double dt);

    /**
     * Reset the controller
     */
    public void reset();
}