package io.github.frc5024.asynchal.sensors;

import java.util.function.Consumer;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import io.github.frc5024.asynchal.Pollable;
import io.github.frc5024.asynchal.Poller;

public class AsyncADXRS450_Gyro extends ADXRS450_Gyro implements Pollable {

    /**
     * Default-construct an AsyncADXRS450_Gyro connected to the onboard SPI port
     */
    public AsyncADXRS450_Gyro() {
        super();

        Poller.getInstance().register(this);
    }

    private double lastAngle = 0.0;
    private Consumer<Double> angleCallback = null;
    private Runnable motionCallback = null;
    private double motionThresh = Double.POSITIVE_INFINITY;

    @Override
    public void checkForUpdates() {

        // Read current angle
        double currentAngle = getAngle();

        // Handle angle callback
        if (currentAngle != lastAngle) {
            if (angleCallback != null) {
                angleCallback.accept(currentAngle);
            }
        }

        // Handle motion callback
        if (motionCallback != null) {
            if (!MathUtils.epsilonEquals(currentAngle, lastAngle, motionThresh)) {
                motionCallback.run();
            }
        }

        // Set the last angle
        lastAngle = currentAngle;

    }

    /**
     * Register a callback for changes in gyro angle in degrees.
     * 
     * @param callback Callback function
     */
    public void registerAngleCallback(Consumer<Double> callback) {
        this.angleCallback = callback;
    }

    /**
     * Register a callback for gyro motion. This is useful for detecting if a robot
     * has been hit during aiming.
     * 
     * @param callback        Callback function
     * @param motionThreshold Threshold for motion in degrees
     */
    public void registerMotionCallback(Runnable callback, double motionThreshold) {
        this.motionCallback = callback;
        this.motionThresh = motionThreshold;
    }

    @Override
    public void close() {
        // Remove from poller
        Poller.getInstance().deregister(this);
    }

}