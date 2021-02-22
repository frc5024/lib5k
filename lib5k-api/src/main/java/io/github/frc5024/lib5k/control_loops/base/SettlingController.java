package io.github.frc5024.lib5k.control_loops.base;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.Timer;

/**
 * An extension of the {@link ControllerBase} that will wait a set amount of
 * time before finishing.
 */
@Deprecated(since = "October 2020", forRemoval = true)
@SuppressWarnings({"deprecation", "removal"})
public abstract class SettlingController extends ControllerBase {

    // Rest time
    private double restMS = 0.0;
    private Timer timer;

    /**
     * Create a Settling Controller
     */
    public SettlingController() {

        // Set up timer
        timer = new Timer();
        timer.reset();
    }

    @Override
    public double update(double measurement, double goal) {
        // Get internal calculation
        double calculation = super.update(measurement, goal);

        // Handle timer
        if (rawIsAtGoal()) {
            timer.start();
        } else {
            timer.stop();
            timer.reset();
        }

        return calculation;
    }

    /**
     * Set the amount of time the system must be stable for in order for "is at
     * goal" to be true
     * 
     * @param ms Amount of time to wait for system to settle
     */
    public void setMinRestTime(double ms) {
        this.restMS = ms;
    }

    /**
     * Get the amount of time the system must be stable for in order for "is at
     * goal" to be true
     * 
     * @return Amount of time to wait for system to settle
     */
    public double getRestMS() {
        return restMS;
    }

    /**
     * Check if the system is at it's goal without waiting for the timer
     * 
     * @return Raw getter for "is at goal"
     */
    public boolean rawIsAtGoal() {
        return MathUtils.epsilonEquals(getGoal(), getLastMeasurement(), getEpsilon());
    }

    @Override
    public boolean isAtGoal() {
        // Check if the timer has been reached
        return timer.hasElapsed(this.restMS / 1000);
    }

    @Override
    public void reset() {
        super.reset();

        // Add a timer reset
        timer.stop();
        timer.reset();
    }

}