package io.github.frc5024.lib5k.hardware.generic.sensors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.util.sendable.SendableRegistry;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.IBinarySensor;

public class LineBreak extends DigitalInput implements IBinarySensor {

    private Solenoid m_powerSource;

    /**
     * Create a Line Break sensor object for a sensor that is powered via a
     * Pneumatic Control Module
     * 
     * @param dioChannel DigitalIO signal channel
     * @param pcmType      PCM CAN device ID
     * @param pcmChannel PCM device channel
     */
    public LineBreak(int dioChannel, PneumaticsModuleType pcmType, int pcmChannel) {
        this(dioChannel, new Solenoid(pcmType, pcmChannel));

    }

    /**
     * Create a Line Break sensor object for a sensor that is powered via a
     * Pneumatic Control Module
     * 
     * @param dioChannel     DigitalIO signal channel
     * @param pcmPowerSource Solenoid power output
     */
    public LineBreak(int dioChannel, Solenoid pcmPowerSource) {
        super(dioChannel);

        // enable the power source solenoid
        m_powerSource = pcmPowerSource;
        m_powerSource.set(true);
    }

    /**
     * Create a Line Break sensor object for a sensor that is powered via an
     * external source
     * 
     * @param channel DigitalIO channel
     */
    public LineBreak(int channel) {
        super(channel);

        // Set the Sendable name
        SendableRegistry.setName(this, "LineBreak", channel);
    }

    /**
     * Flush the CAN bus and send a new packet to keep external power enabled
     */
    public void flush() {
        m_powerSource.set(true);
    }

    @Override
    public void close() {
        super.close();
        if (m_powerSource != null) {
            m_powerSource.close();
        }
    }

}