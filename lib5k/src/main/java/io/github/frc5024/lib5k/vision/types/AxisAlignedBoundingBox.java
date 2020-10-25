package io.github.frc5024.lib5k.vision.types;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * A computer-vision axis-aligned bounding box. Relative to a camera
 */
public class AxisAlignedBoundingBox extends Contour {

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
        this.width = (bottomRight.getX() - topLeft.getX());
        this.height = (topLeft.getY() - bottomRight.getY());

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
}