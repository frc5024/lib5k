package io.github.frc5024.lib5k.hardware.generic.pneumatics;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Solenoid;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;
import io.github.frc5024.lib5k.logging.Loggable;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.telemetry.ComponentTelemetry;

/**
 * Buffer solenoid commands to reduce CAN spam. For some reason, solenoid
 * commands take up a lot of compute
 */
public class LazySolenoid extends Solenoid implements Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Locals */
    private boolean lastState = false;
    private double lastSetAt = 0;

    /* Telemetry */
    private String name;
    private NetworkTable telemetryTable;

    /**
     * Create a LazySolenoid
     * 
     * @param moduleNumber PCM module number (CAN ID)
     * @param channel      PCM channel
     */
    public LazySolenoid(int moduleNumber, int channel) {
        super(moduleNumber, channel);

        // Determine component name
        name = String.format("LazySolenoid (%d:%d)", moduleNumber, channel);

        // Get telemetry table
        telemetryTable = ComponentTelemetry.getInstance().getTableForComponent(name);
    }

    /**
     * Set the value of a solenoid, but reduce CAN spam by only sending new data
     * 
     * @param on Should the solenoid turn on?
     */
    @Override
    public void set(boolean on) {

        // Check if there is a new command
        if (on != lastState) {

            // Set solenoid mode
            super.set(on);

            // Set last state
            lastState = on;

            // Track trigger
            lastSetAt = FPGAClock.getFPGASeconds();
        }
    }

    /**
     * re-send the current state to flush CAN
     */
    public void flush() {
        super.set(lastState);
    }

    /**
     * Gets the timestamp of the last time a CAN packet was written
     * 
     * @return Timestamp of last CAN packet
     */
    public double getLastCANWriteTimestampSeconds() {
        return lastSetAt;
    }

    @Override
    public void logStatus() {

        // Build status string
        String status = String.format("Value: %b", lastState);

        // Log status
        logger.log(status);

    }

    @Override
    public void updateTelemetry() {
        telemetryTable.getEntry("Enabled").setBoolean(lastState);

    }

}