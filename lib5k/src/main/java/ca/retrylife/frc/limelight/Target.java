package ca.retrylife.frc.limelight;

import edu.wpi.first.wpilibj.geometry.Rotation2d;

/**
 * Limelight vision target
 */
public class Target {
    private double tx, ty, ta, ts, tshort, tlong, thor, tvert;

    /**
     * Create a LimelightTarget
     * 
     * @param tx     Horizontal Offset From Crosshair To Target
     * @param ty     Vertical Offset From Crosshair To Target
     * @param ta     Target area (0% of image to 100% of image)
     * @param ts     Target skew (-90 degrees to 0 degrees)
     * @param tshort Sidelength of shortest side of the fitted bounding box (pixels)
     * @param tlong  Sidelength of longest side of the fitted bounding box (pixels)
     * @param thor   Horizontal sidelength of the rough bounding box (0 - 320
     *               pixels)
     * @param tvert  Vertical sidelength of the rough bounding box (0 - 320 pixels)
     */
    public Target(double tx, double ty, double ta, double ts, double tshort, double tlong, double thor, double tvert) {
        this.tx = tx;
        this.ty = ty;
        this.ta = ta;
        this.ts = ts;
        this.tshort = tshort;
        this.tlong = tlong;
        this.thor = thor;
        this.tvert = tvert;
    }

    /**
     * Get the target X angle in degrees from the crosshair
     * 
     * @return X angle
     */
    public double getX() {
        return this.tx;
    }

    /**
     * Get the target X Rotation from the crosshair
     * 
     * @return X Rotation
     */
    public Rotation2d getXRotation() {
        return Rotation2d.fromDegrees(getX());
    }

    /**
     * Get the target Y angle in degrees from the crosshair
     * 
     * @return Y angle
     */
    public double getY() {
        return this.ty;
    }

    /**
     * Get the target Y Rotation from the crosshair
     * 
     * @return Y Rotation
     */
    public Rotation2d getYRotation() {
        return Rotation2d.fromDegrees(getY());
    }

    /**
     * Get the target area (0% of image to 100% of image)
     * 
     * @return Target area
     */
    public double getArea() {
        return this.ta;
    }

    /**
     * Get the target skew (-90 degrees to 0 degrees)
     * 
     * @return Target skew
     */
    public double getSkew() {
        return this.ts;
    }

    /**
     * Get the sidelength of shortest side of the fitted bounding box (pixels)
     * 
     * @return Shortest sidelength
     */
    public double getShortest() {
        return this.tshort;
    }

    /**
     * Get the sidelength of longest side of the fitted bounding box (pixels)
     * 
     * @return Longest sidelength
     */
    public double getLongest() {
        return this.tlong;
    }

    /**
     * Get the horizontal sidelength of the rough bounding box (0 - 320 pixels)
     * 
     * @return Horizontal sidelength
     */
    public double getHorizontal() {
        return this.thor;
    }

    /**
     * Get the vertical sidelength of the rough bounding box (0 - 320 pixels)
     * 
     * @return Vertical sidelength
     */
    public double getVertical() {
        return this.tvert;
    }
}