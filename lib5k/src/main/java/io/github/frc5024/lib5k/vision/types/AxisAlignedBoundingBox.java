package io.github.frc5024.lib5k.vision.types;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import io.github.frc5024.lib5k.utils.MathWrapper;

/**
 * A computer-vision axis-aligned bounding box. Relative to a camera
 */
public class AxisAlignedBoundingBox extends Contour {

    // Collection of mathematical operators
    public static final MathWrapper<AxisAlignedBoundingBox> OPERATORS = new MathWrapper<AxisAlignedBoundingBox>(
            AxisAlignedBoundingBox::add, AxisAlignedBoundingBox::subtract, AxisAlignedBoundingBox::multiply,
            AxisAlignedBoundingBox::divide);

    // Box corners
    private final Translation2d topLeft;
    private final Translation2d topRight;
    private final Translation2d bottomLeft;
    private final Translation2d bottomRight;

    // Sizing
    private final double width;
    private final double height;

    // Aspect ratio
    private final double aspectRatio;

    /**
     * Create an AxisAlignedBoundingBox from opposing corners
     * 
     * @param topLeft     Top left corner expressed as a percentage of the frame
     *                    from the center (top left is -1,1)
     * @param bottomRight Bottom right corner expressed as a percentage of the frame
     *                    from the center (top left is -1,1)
     */
    public AxisAlignedBoundingBox(Translation2d topLeft, Translation2d bottomRight) {
        this(topLeft, bottomRight, Contour.NO_ROTATION, Contour.NO_ROTATION);
    }

    /**
     * Create an AxisAlignedBoundingBox from opposing corners, with angles relative
     * to a camera
     * 
     * @param topLeft     Top left corner expressed as a percentage of the frame
     *                    from the center (top left is -1,1)
     * @param bottomRight Bottom right corner expressed as a percentage of the frame
     *                    from the center (top left is -1,1)
     * @param xRot        X angle from camera
     * @param yRot        Y angle from camera
     */
    public AxisAlignedBoundingBox(Translation2d topLeft, Translation2d bottomRight, Rotation2d xRot, Rotation2d yRot) {
        super(topLeft.getX() + ((bottomRight.getX() - topLeft.getX()) / 2),
                bottomRight.getY() + ((topLeft.getY() - bottomRight.getY()) / 2), xRot, yRot);

        // Set corners
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.bottomLeft = new Translation2d(topLeft.getX(), bottomRight.getY());
        this.topRight = new Translation2d(bottomRight.getX(), topLeft.getY());

        // Set size
        this.width = Math.abs(bottomRight.getX() - topLeft.getX());
        this.height = Math.abs(topLeft.getY() - bottomRight.getY());

        // Set aspect ratio
        this.aspectRatio = width / height;
    }

    /**
     * Get the aspect ratio of this box as width/height
     * 
     * @return Aspect ratio
     */
    public double getAspectRatio() {
        return this.aspectRatio;
    }

    @Override
    public boolean contains(Translation2d point) {
        return (point.getX() >= getTopLeftCorner().getX()) && (point.getX() <= getBottomRightCorner().getX())
                && (point.getY() >= getBottomRightCorner().getY()) && (point.getX() <= getTopLeftCorner().getY());
    }

    @Override
    public boolean overlaps(AxisAlignedBoundingBox box) {
        return this.contains(box.getTopLeftCorner()) || this.contains(box.getBottomLeftCorner())
                || this.contains(box.getTopRightCorner()) || this.contains(box.getBottomRightCorner());
    }

    /**
     * Get the top left corner of this box expressed as a percentage of the frame
     * from the center (top left is -1,1)
     * 
     * @return top left corner
     */
    public Translation2d getTopLeftCorner() {
        return topLeft;
    }

    /**
     * Get the bottom left corner of this box expressed as a percentage of the frame
     * from the center (top left is -1,1)
     * 
     * @return bottom left corner
     */
    public Translation2d getBottomLeftCorner() {
        return bottomLeft;
    }

    /**
     * Get the top right corner of this box expressed as a percentage of the frame
     * from the center (top left is -1,1)
     * 
     * @return top right corner
     */
    public Translation2d getTopRightCorner() {
        return topRight;
    }

    /**
     * Get the bottom right corner of this box expressed as a percentage of the
     * frame from the center (top left is -1,1)
     * 
     * @return bottom right corner
     */
    public Translation2d getBottomRightCorner() {
        return bottomRight;
    }

    /**
     * Get box width
     * 
     * @return Width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Get box height
     * 
     * @return Height
     */
    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return String.format(
                "AABB{corners: [%s, %s, %s, %s], center: %s, xRot: %s, yRot: %s, width: %.2f, height: %.2f, aspect: %.2f]",
                topLeft, bottomLeft, topRight, bottomRight, super.toString(), getXRotation(), getYRotation(), width,
                height, aspectRatio);
    }

    /**
     * Adds two AxisAlignedBoundingBoxes in 2d space and returns the sum. This is
     * similar to vector addition.
     *
     * @param a The first AxisAlignedBoundingBox
     * @param b The second AxisAlignedBoundingBox
     * @return The sum of the AxisAlignedBoundingBoxes.
     */
    public static AxisAlignedBoundingBox add(AxisAlignedBoundingBox a, AxisAlignedBoundingBox b) {

        // Get new rotations
        Rotation2d newXRot = a.getXRotation().plus(b.getXRotation());
        Rotation2d newYRot = a.getYRotation().plus(b.getYRotation());

        // Get new corners
        Translation2d newTopLeft = a.getTopLeftCorner().plus(b.getTopLeftCorner());
        Translation2d newBottomRight = a.getBottomRightCorner().plus(b.getBottomRightCorner());

        // Build new AABB
        return new AxisAlignedBoundingBox(newTopLeft, newBottomRight, newXRot, newYRot);
    }

    /**
     * Subtracts the AxisAlignedBoundingBox b from AxisAlignedBoundingBox a
     *
     * @param a The first AxisAlignedBoundingBox
     * @param b The second AxisAlignedBoundingBox
     * @return The result of (a-b)
     */
    public static AxisAlignedBoundingBox subtract(AxisAlignedBoundingBox a, AxisAlignedBoundingBox b) {

        // Get new rotations
        Rotation2d newXRot = a.getXRotation().minus(b.getXRotation());
        Rotation2d newYRot = a.getYRotation().minus(b.getYRotation());

        // Get new corners
        Translation2d newTopLeft = a.getTopLeftCorner().minus(b.getTopLeftCorner());
        Translation2d newBottomRight = a.getBottomRightCorner().minus(b.getBottomRightCorner());

        // Build new AABB
        return new AxisAlignedBoundingBox(newTopLeft, newBottomRight, newXRot, newYRot);
    }

    /**
     * Multiplies a AxisAlignedBoundingBox by a scalar and returns the new
     * AxisAlignedBoundingBox.
     *
     * @param value  Base AxisAlignedBoundingBox
     * @param scalar The scalar to multiply by.
     * @return The scaled AxisAlignedBoundingBox.
     */
    public static AxisAlignedBoundingBox multiply(AxisAlignedBoundingBox value, double scalar) {

        // Get new rotations
        Rotation2d newXRot = value.getXRotation().times(scalar);
        Rotation2d newYRot = value.getYRotation().times(scalar);

        // Get new corners
        Translation2d newTopLeft = value.getTopLeftCorner().times(scalar);
        Translation2d newBottomRight = value.getBottomRightCorner().times(scalar);

        // Build new AABB
        return new AxisAlignedBoundingBox(newTopLeft, newBottomRight, newXRot, newYRot);
    }

    /**
     * Divides the AxisAlignedBoundingBox by a scalar and returns the new
     * AxisAlignedBoundingBox.
     * 
     * @param scalar The scalar to multiply by.
     * @return The new AxisAlignedBoundingBox.
     */
    public static AxisAlignedBoundingBox divide(AxisAlignedBoundingBox value, double scalar) {

        // Get new rotations
        Rotation2d newXRot = Rotation2d.fromDegrees(value.getXRotation().getDegrees() / scalar);
        Rotation2d newYRot = Rotation2d.fromDegrees(value.getYRotation().getDegrees() / scalar);

        // Get new corners
        Translation2d newTopLeft = value.getTopLeftCorner().div(scalar);
        Translation2d newBottomRight = value.getBottomRightCorner().div(scalar);

        // Build new AABB
        return new AxisAlignedBoundingBox(newTopLeft, newBottomRight, newXRot, newYRot);
    }

}