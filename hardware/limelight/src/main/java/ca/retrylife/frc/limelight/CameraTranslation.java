package ca.retrylife.frc.limelight;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.geometry.Twist2d;

/**
 * Camera translation from vision target
 */
public class CameraTranslation {
    private double x, y, z, pitch, yaw, roll;

    /**
     * Create a CameraTranslation
     * 
     * @param x     X component
     * @param y     Y component
     * @param z     Z component
     * @param pitch Pitch
     * @param yaw   Yaw
     * @param roll  Roll
     */
    public CameraTranslation(double x, double y, double z, double pitch, double yaw, double roll) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    /**
     * Get the X component
     * 
     * @return X
     */
    public double getX() {
        return this.x;
    }

    /**
     * Get the Y component
     * 
     * @return Y
     */
    public double getY() {
        return this.y;
    }

    /**
     * Get the Z component
     * 
     * @return Z
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Get the Pitch
     * 
     * @return Pitch
     */
    public double getPitch() {
        return this.pitch;
    }

    /**
     * Get the Yaw
     * 
     * @return Yaw
     */
    public double getYaw() {
        return this.yaw;
    }

    /**
     * Get the Roll
     * 
     * @return Roll
     */
    public double getRoll() {
        return this.roll;
    }

    /**
     * Get the camera-to-target translation
     * 
     * @return Translation
     */
    public Translation2d getTranslation() {
        return new Translation2d(getX(), getY());
    }

    /**
     * Get the camera-to-target Rotation
     * 
     * @return Rotation
     */
    public Rotation2d getRotation() {
        return Rotation2d.fromDegrees(getYaw());
    }

    /**
     * Get the difference between the camera and target
     * 
     * @return Difference
     */
    public Twist2d getTwist() {
        return new Twist2d(getX(), getY(), getYaw());
    }

}