package io.github.frc5024.lib5k.hardware.generic.sensors;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * For mocking a digital input in tests
 */
public class MockDigitalInput extends DigitalInput {
    
    private boolean on = false;

    /**
     * Create a MockDigitalInput
     * @param channel RIO DIO channel
     */
    public MockDigitalInput(int channel) {
        super(channel);
    }

    /**
     * Set the sensor state
     * @param on On?
     */
    public void set(boolean on) {
        this.on = on;
    }

    @Override
    public boolean get() {
        return this.on;
    }
    
}