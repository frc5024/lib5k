package io.github.frc5024.lib5k.logging;

/**
 * Common interface for components that can be logged
 */
public interface Loggable {
    
    /**
     * Log component status 
     */
    public void logStatus();


    /**
     * Push telemetry data to NetworkTables
     */
    public void updateTelemetry();

}