package io.github.frc5024.asynchal.sensors;

import edu.wpi.first.wpilibj.DigitalInput;
import io.github.frc5024.asynchal.Pollable;
import io.github.frc5024.asynchal.Poller;

/**
 * An asynchronous wrapper for {@link DigitalInput}
 */
public class AsyncDigitalInput extends DigitalInput implements Pollable {

    /* Sensor state tracking */
    private boolean lastState = false;
    private Runnable triggerCallback = null;
    private Runnable releaseCallback = null;

    /**
     * Create an instance of a Digital Input class. Creates a digital input given a
     * channel with asynchronous capabilities.
     *
     * @param channel the DIO channel for the digital input 0-9 are on-board, 10-25
     *                are on the MXP
     */
    public AsyncDigitalInput(int channel) {
        super(channel);

        // Register with the poller
        Poller.getInstance().register(this);
    }

    @Override
    public void checkForUpdates() {

        // Read the sensor state
        boolean currentState = get();

        // Compare states
        if (currentState != lastState) {
            if (currentState) {
                if (triggerCallback != null) {
                    triggerCallback.run();
                }
            } else {
                if (releaseCallback != null) {
                    releaseCallback.run();
                }
            }
        }

        // Set the last state
        lastState = currentState;

    }

    /**
     * Register a callback function to be run when the input is pulled high
     * 
     * @param callback Callback function
     */
    public void registerTriggerCallback(Runnable callback) {
        this.triggerCallback = callback;
    }

    /**
     * Register a callback function to be run when the input is pulled low
     * 
     * @param callback Callback function
     */
    public void registerReleaseCallback(Runnable callback) {
        this.releaseCallback = callback;
    }

    @Override
    public void close() {
        super.close();

        // Remove from poller
        Poller.getInstance().deregister(this);
    }

}