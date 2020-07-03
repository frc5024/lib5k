package io.github.frc5024.lib5k.hardware.common.drivebase;

/**
 * Interface for a simple differential drivebase
 */
public interface IDifferentialDrivebase {

    /**
     * Get left side distance traveled in meters
     * @return Left distance
     */
    public double getLeftMeters();

    /**
     * Get right side distance traveled in meters
     * @return Right distance
     */
    public double getRightMeters();

    /**
     * Get track width in meters
     * @return Track width
     */
    public double getWidthMeters();

}