package io.github.frc5024.lib5k.hardware.common.sensors.interfaces;

/**
 * The CommonEncoder interface is designed to provide a unified interface
 * between the 3 encoder sources (ctre, wpi, revrobotics). 5024 likes being able
 * to hot-swap components, and the biggest bottleneck is rewriting encoder code
 * because they are not cross-compatible.
 */
public interface CommonEncoder {

    /**
     * Get the ID of the connected sensor.
     *
     * @return The ID of the sensor
     */
    public int getID();

    /**
     * Set the phase of the encoder so that it is set to be in phase with the motor
     * itself. This only works for quadrature encoders and analog sensors.
     * 
     * @param inverted The phase of the sensor
     */
    public void setInverted(boolean inverted);

    /**
     * Get the phase of the sensor. This will just return false if the user tries to
     * get the inversion of the hall effect.
     * 
     * @return The phase of the sensor
     */
    public boolean getInverted();

    /**
     * Get the position of the motor. This returns the native units
     * of rotations.
     *
     * @return Number of rotations of the motor
     *
     */
    public double getPosition();

    /**
     * Get the velocity of the motor. This returns the native units
     * of RPM.
     *
     * @return Number the RPM of the motor
     *
     */
    public double getVelocity();

    

}