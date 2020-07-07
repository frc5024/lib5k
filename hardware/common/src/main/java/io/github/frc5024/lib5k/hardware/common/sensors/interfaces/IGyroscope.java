package io.github.frc5024.lib5k.hardware.common.sensors.interfaces;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * Common interface for all gyroscopes
 */
public interface IGyroscope extends Gyro {

    /**
     * Set if angle readings should be flipped
     * 
     * @param inverted Should flip?
     */
    public void setInverted(boolean inverted);

    /**
     * Get if the readings are inverted
     * 
     * @return Is inverted?
     */
    public boolean getInverted();

    /**
     * Get if the sensor has been calibrated
     * 
     * @return Is calibrated?
     */
    public boolean getCalibrated();

    /**
     * Returns the current heading heading
     *
     * @return the heading in degrees, from 180 to 180
     */
    public double getHeading();

    /**
     * Get the gyro angle, wrapped by 360 degrees
     * 
     * @return Wrapped angle
     */
    public default double getWrappedAngle() {
        return getAngle() % 360;
    }

    /**
     * Get the heading as a Rotation2d object
     * 
     * @return Heading
     */
    public default Rotation2d getRotation() {
        return Rotation2d.fromDegrees(getHeading());
    }
}