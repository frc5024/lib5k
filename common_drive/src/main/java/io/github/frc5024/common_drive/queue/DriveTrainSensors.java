package io.github.frc5024.common_drive.queue;

import edu.wpi.first.wpilibj.geometry.Rotation2d;

/**
 * A data class for sharing sensor readings from the client to code to the
 * drivetrain base
 */
public class DriveTrainSensors {

    // Timestamp
    public double timestamp_ms;

    // Gyroscope data
    public Rotation2d rotation = new Rotation2d();
    public double angle;
    public double angularRate;

    // Encoder data
    public double leftEncoderMetres;
    public double rightEncoderMetres;

    /**
     * Create a copy of this object
     * 
     * @return Copy
     */
    public DriveTrainSensors copy() {
        // Build an output
        DriveTrainSensors out = new DriveTrainSensors();

        // copy
        out.timestamp_ms = timestamp_ms;
        out.rotation = rotation;
        out.angle = angle;
        out.angularRate = angularRate;
        out.leftEncoderMetres = leftEncoderMetres;
        out.rightEncoderMetres = rightEncoderMetres;

        return out;
    }

}