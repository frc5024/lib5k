package io.github.frc5024.lib5k.vision.types;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * Contour is the base class for any type of computer-vision Contour
 */
public abstract class Contour extends Translation2d {

    // A static for no rotation
    public static final Rotation2d NO_ROTATION = new Rotation2d();

    // This contour's rotations
    private final Rotation2d xRot;
    private final Rotation2d yRot;

    /**
     * Create a Contour at 0,0
     */
    public Contour() {
        this(0, 0);
    }

    /**
     * Create a contour with a position
     * 
     * @param x X offset expressed as a percentage of the frame from the center (top
     *          left is -1,1)
     * @param y Y offset expressed as a percentage of the frame from the center (top
     *          left is -1,1)
     */
    public Contour(double x, double y) {
        this(x, y, NO_ROTATION, NO_ROTATION);
    }

    /**
     * Create a contour with a position and rotation from the camera
     * 
     * @param x    X offset expressed as a percentage of the frame from the center
     *             (top left is -1,1)
     * @param y    Y offset expressed as a percentage of the frame from the center
     *             (top left is -1,1)
     * @param xRot X angle from camera
     * @param yRot Y angle from camera
     */
    public Contour(double x, double y, Rotation2d xRot, Rotation2d yRot) {
        super(x, y);
        this.xRot = xRot;
        this.yRot = yRot;
    }

    /**
     * Get the X angle from the camera
     * 
     * @return X angle
     */
    public Rotation2d getXRotation() {
        return this.xRot;
    }

    /**
     * Get the Y angle from the camera
     * 
     * @return Y angle
     */
    public Rotation2d getYRotation() {
        return this.yRot;
    }

    /**
     * Check if this contour contains a point
     * 
     * @param point Point
     * @return Is the point in this contour?
     */
    public abstract boolean contains(Translation2d point);

    /**
     * Check if this contour overlaps with an AxisAlignedBoundingBox
     * 
     * @param box AxisAlignedBoundingBox
     * @return Does it overlap?
     */
    public abstract boolean overlaps(AxisAlignedBoundingBox box);

}