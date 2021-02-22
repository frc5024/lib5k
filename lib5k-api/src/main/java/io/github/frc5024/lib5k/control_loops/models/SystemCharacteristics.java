package io.github.frc5024.lib5k.control_loops.models;

/**
 * The characteristics of a system
 */
public interface SystemCharacteristics {

    /**
     * System motor characteristics
     * 
     * @return Motor characteristics
     */
    public DCBrushedMotor getMotorCharacteristics();

    /**
     * System gear ratio expressed as output over input
     * 
     * @return Gear ratio
     */
    public double getGearRatio();

}