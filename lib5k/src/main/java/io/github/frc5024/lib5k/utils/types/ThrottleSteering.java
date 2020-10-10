package io.github.frc5024.lib5k.utils.types;

/**
 * Represents throttle and steering
 */
public class ThrottleSteering {

    // Values
    private double throttle;
    private double steering;

    public ThrottleSteering() {
        this(0, 0);
    }

    /**
     * Create new ThrottleSteering
     * 
     * @param throttle Throttle value
     * @param steering Steering value
     */
    public ThrottleSteering(double throttle, double steering) {
        this.throttle = throttle;
        this.steering = steering;
    }

    /**
     * Get the throttle value
     * 
     * @return Throttle
     */
    public double getThrottle() {
        return throttle;
    }

    /**
     * Get the steering value
     * 
     * @return Steering
     */
    public double getSteering() {
        return steering;
    }

}