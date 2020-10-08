package io.github.frc5024.lib5k.control_loops.base;

/**
 * The base class for all controllers
 */
public interface Controller {

    /**
     * Calculate the next output
     * 
     * @param measurement System measurement
     * @param reference   System reference
     * @return Output
     */
    public double calculate(double measurement, double reference);

    /**
     * Calculate the next output
     * 
     * @param measurement System measurement
     * @return Output
     */
    public double calculate(double measurement);

    /**
     * Set the reference value. This is where you want the system to be
     * 
     * @param reference Reference value
     */
    public void setReference(double reference);

    /**
     * Check if the system is at its reference
     * 
     * @return Is at reference
     */
    public boolean atReference();

    /**
     * Set the acceptable error for the system. Make this realistic
     * 
     * @param epsilon Acceptable error
     */
    public void setEpsilon(double epsilon);
}