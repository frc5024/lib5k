package io.github.frc5024.lib5k.hardware.generic.sensors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.IBinarySensor;

public class LimitSwitch extends DigitalInput implements IBinarySensor {

    /**
     * Create a digital Limit Switch sensor object
     * 
     * @param channel DigitalIO channel
     */
    public LimitSwitch(int channel) {
        super(channel);

        // Set the Sendable name
        SendableRegistry.setName(this, "LimitSwitch", channel);
    }

    @Override
    public boolean get() {
        return !super.get();
    }

}