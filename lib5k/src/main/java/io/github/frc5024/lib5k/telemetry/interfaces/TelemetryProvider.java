package io.github.frc5024.lib5k.telemetry.interfaces;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

/**
 * Interface for classes that provide network telemetry data
 */
public interface TelemetryProvider extends Sendable {

    /**
     * Gets the name of this object.
     *
     * @return Name
     */
    public String getName();

    /**
     * Set if telemetry should be enabled
     * 
     * @param enabled Should enable telemetry?
     */
    public default void setTelemetryEnabled(boolean enabled){
        if(enabled){
            SendableRegistry.add(this, getName());
        } else {
            SendableRegistry.remove(this);
        }
    }

    /**
     * Get if telemetry is currently enabled
     * 
     * @return Is telemetry enabled?
     */
    public default boolean isTelemetryEnabled() {
        return SendableRegistry.contains(this);
    }

    /**
     * Publish this provider
     * 
     * @param table NetworkTable parent
     */
    public default void publish(NetworkTable table) {
        SendableRegistry.publish(this, table);
    }

}