package io.github.frc5024.lib5k.hardware.common.sensors.interfaces;

/**
 * Interface for binary sensors
 */
public interface IBinarySensor {

    /**
     * Get binary sensor reading
     * 
     * @return Is sensor on?
     */
    public boolean get();
}