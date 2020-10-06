package io.github.frc5024.lib5k.hardware.generic.sensors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.IBinarySensor;

/**
 * Hall effect sensor
 */
public class HallEffect extends DigitalInput implements IBinarySensor {

    /**
     * Create a digital Hall Effect sensor object
     * 
     * @param channel DigitalIO channel
     */
    public HallEffect(int channel) {
        super(channel);

        // Set the Sendable name
        SendableRegistry.setName(this, "HallEffect", channel);
    }

    @Override
    public boolean get() {
        return !super.get(); // Negate reading
    }

}