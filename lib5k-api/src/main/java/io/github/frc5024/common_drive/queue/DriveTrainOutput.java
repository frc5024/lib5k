package io.github.frc5024.common_drive.queue;

import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.common_drive.types.MotorMode;

/**
 * A data class containing all drivetrain outputs
 */
@Deprecated(since = "October 2020", forRemoval = true)
public class DriveTrainOutput {

    // Timestamp
    public double timestamp_ms;

    // Output voltages
    public double leftVoltage;
    public double rightVoltage;

    // Configuration data
    public WriteLock<Boolean> invertSensorPhase = new WriteLock<>(false);
    public WriteLock<MotorMode> motorMode = new WriteLock<>(MotorMode.kDefault);
    public WriteLock<Gear> gear = new WriteLock<>(Gear.HIGH);
    public WriteLock<Double> motorRamp = new WriteLock<>(0.0);

    /**
     * Reset this object's contents
     */
    public void zero() {
        this.timestamp_ms = 0.0;
        this.leftVoltage = 0.0;
        this.rightVoltage = 0.0;
        this.invertSensorPhase.zero();
        this.motorMode.zero();
        this.gear.zero();
        this.motorRamp.zero();
    }

}