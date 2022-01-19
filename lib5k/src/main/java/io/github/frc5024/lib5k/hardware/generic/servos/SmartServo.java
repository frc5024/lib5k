package io.github.frc5024.lib5k.hardware.generic.servos;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.Servo;

// TODO: Implement real servo code
/**
 * A beta wrapper around the Rev Robotics "smart servo".
 * 
 * This currently can only act as a single speed motor, as it was programmed
 * between matches at an event for what we needed at the time.
 */
public class SmartServo extends Servo {

    /* Simulation */
    private SimDevice m_device;
    private SimDouble m_angle;
    private SimDouble m_output;

    /**
     * Create a SmartServo
     * 
     * @param channel PWM channel
     */
    public SmartServo(int channel) {
        super(channel);

        // Set up simulation
        m_device = SimDevice.create("SmartServo");
        if (m_device != null) {
            m_angle = m_device.createDouble("Degrees", true, 0.0);
            m_output = m_device.createDouble("Output", true, 0.0);
        }
    }

    @Override
    public void set(double value) {

        // Set simulation val
        if (m_device != null) {
            value = MathUtils.clamp(value, 0.0, 1.0);
            m_output.set(value);
            m_angle.set(360 * value);
        }

        // Set hardware output
        super.set(value);
    }

    @Override
    public void setAngle(double degrees) {

        // Set simulation val
        if (m_device != null) {
            degrees = MathUtils.clamp(degrees, 0.0, 360.0);
            m_output.set(degrees / 180.0);
            m_angle.set(degrees);
        }

        // Set hardware output
        super.setAngle(degrees);
    }

    /**
     * Stop the servo
     */
    public void stop() {
            set(getPosition());
    }

    /**
     * Make the servo spin forever
     */
    public void rip() {
        set(1.0);
    }

}