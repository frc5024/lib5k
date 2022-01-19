package io.github.frc5024.lib5k.hardware.generic.motors;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;

/**
 * PCM-Powered Linear actuator
 */
public class LinearActuator implements Sendable, AutoCloseable {

    private Solenoid m_trigger;

    public enum ActuatorState {
        kDEPLOYED, // Actuator deployed
        kINACTIVE, // Actuator un-powered
    }

    /**
     * Create a Linear Actuator that is powered via a Pneumatic Control Module
     * 
     * @param pcmType      PCM CAN device ID
     * @param pcmChannel PCM device channel
     */
    public LinearActuator(PneumaticsModuleType pcmType, int pcmChannel) {

        // Configure the trigger
        m_trigger = new Solenoid(pcmType, pcmChannel);

        // Replace solenoid sendable with actuator
        // SendableRegistry.remove(m_trigger);
        SendableRegistry.add(this, "LinearActuator", pcmChannel);

    }

    /**
     * Set the actuator state
     * 
     * @param state State
     */
    public void set(ActuatorState state) {
        // PCM is not designed to do this
        // clearAllFaults();

        // Send data
        switch (state) {
            case kDEPLOYED:
                m_trigger.set(true);
                break;
            default:
                m_trigger.set(false);
        }
    }

    /**
     * Get if the actuator is deployed
     * 
     * @return Is deployed?
     */
    public boolean isDeployed() {
        return m_trigger.get();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Solenoid");
        builder.setActuator(true);
        builder.setSafeState(() -> set(ActuatorState.kINACTIVE));
        builder.addBooleanProperty("Value", this::isDeployed, (t) -> {
            set((t) ? ActuatorState.kDEPLOYED : ActuatorState.kINACTIVE);
        });

    }

    /**
     * Clear all PCM faults for the attached PCM
     */
    @Deprecated(since= "2022", forRemoval = true)
    public void clearAllFaults() {
        //m_trigger.clearAllPCMStickyFaults();
    }

    @Override
    public void close() {
        m_trigger.close();
    }
}