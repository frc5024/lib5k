package io.github.frc5024.lib5k.control_loops.base;

/**
 * Base class for all non-statespace controllers.
 */
@Deprecated(since = "October 2020", forRemoval = true)
public abstract class ControllerBase {

    // Controller epsilon
    private double epsilon = 0.0;

    // Controller goal
    private double goal;
    private double measurement;

    /**
     * Update the system measurement, and get new output
     * 
     * @param measurement Current sensor measurement
     * @return Calculated output
     */
    public double update(double measurement) {
        return update(measurement, getGoal());
    }

    /**
     * Update the system measurement and goal, and get new output
     * 
     * @param measurement Current sensor measurement
     * @param goal        System goal
     * @return Calculated output
     */
    public double update(double measurement, double goal) {
        // Update internal trackers
        setGoal(goal);
        this.measurement = measurement;

        // Call calculator
        return calculate(measurement - goal);
    }

    /**
     * Internal controller calculation
     * 
     * @param error Error in system
     * @return Output
     */
    protected abstract double calculate(double error);

    /**
     * Check if the system is at it's goal
     * 
     * @return Is at goal?
     */
    public abstract boolean isAtGoal();

    /**
     * Set acceptable error around goal
     * 
     * @param eps Acceptable error
     */
    public void setEpsilon(double eps) {
        this.epsilon = eps;
    }

    /**
     * Get acceptable error around goal
     * 
     * @return Acceptable error
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * Set the system goal
     * 
     * @param goal Goal
     */
    public void setGoal(double goal) {
        this.goal = goal;
    }

    /**
     * Get the system goal
     * 
     * @return Goal
     */
    public double getGoal() {
        return goal;
    }

    /**
     * Get the last known measurement
     * 
     * @return Last measurement
     */
    public double getLastMeasurement() {
        return measurement;
    }

    /**
     * Reset the controller
     */
    public void reset() {
        measurement = 0.0;
        resetInternals();
    }
    
    /**
     * Override this for controller-specific resets
     */
    protected abstract void resetInternals();

}