package io.github.frc5024.lib5k.vision.types;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import io.github.frc5024.lib5k.utils.MathWrapper;

/**
 * HyperbolicAxisAlignedBoundingBox is a non-euclidean
 * {@link AxisAlignedBoundingBox} in hyperbolic space
 */
public class HyperbolicAxisAlignedBoundingBox extends AxisAlignedBoundingBox {

    // Collection of mathematical operators
    public static final MathWrapper<HyperbolicAxisAlignedBoundingBox> OPERATORS = new MathWrapper<HyperbolicAxisAlignedBoundingBox>(
            HyperbolicAxisAlignedBoundingBox::add, HyperbolicAxisAlignedBoundingBox::subtract,
            HyperbolicAxisAlignedBoundingBox::multiply, HyperbolicAxisAlignedBoundingBox::divide);

    // Side rotations
    private final Rotation2d leftRot;
    private final Rotation2d rightRot;
    private final Rotation2d topRot;
    private final Rotation2d bottomRot;

    /**
     * Create a HyperbolicAxisAlignedBoundingBox from a horizontal and vertical
     * theta
     * 
     * @param topLeft     Left corner expressed as a percentage of the frame from
     *                    the center (top left is -1,1)
     * @param bottomRight Bottom right corner expressed as a percentage of the frame
     *                    from the center (top left is -1,1)
     * @param hRot        Positive theta between the left and right bounds
     * @param vRot        Positive theta between the top and bottom bounds
     */
    public HyperbolicAxisAlignedBoundingBox(Translation2d topLeft, Translation2d bottomRight, Rotation2d hRot,
            Rotation2d vRot) {
        this(topLeft, bottomRight, Contour.NO_ROTATION, Contour.NO_ROTATION, hRot, vRot);
    }

    /**
     * Create a HyperbolicAxisAlignedBoundingBox from a horizontal and vertical
     * theta
     * 
     * @param topLeft     left corner expressed as a percentage of the frame from
     *                    the center (top left is -1,1)
     * @param bottomRight Bottom right corner expressed as a percentage of the frame
     *                    from the center (top left is -1,1)
     * @param xRot        X angle from camera
     * @param yRot        Y angle from camera
     * @param hRot        Positive theta between the left and right bounds
     * @param vRot        Positive theta between the top and bottom bounds
     */
    public HyperbolicAxisAlignedBoundingBox(Translation2d topLeft, Translation2d bottomRight, Rotation2d xRot,
            Rotation2d yRot, Rotation2d hRot, Rotation2d vRot) {
        this(topLeft, bottomRight, xRot, yRot, hRot.times(-0.5), hRot.times(0.5), vRot.times(0.5), vRot.times(-0.5));
    }

    /**
     * Create a HyperbolicAxisAlignedBoundingBox using rotations for each side
     * 
     * @param topLeft     Top left corner expressed as a percentage of the frame
     *                    from the center (top left is -1,1)
     * @param bottomRight Bottom right corner expressed as a percentage of the frame
     *                    from the center (top left is -1,1)
     * @param xRot        X angle from camera
     * @param yRot        Y angle from camera
     * @param leftRot     Rotation of the left bound from the center point
     * @param rightRot    Rotation of the right bound from the center point
     * @param topRot      Rotation of the top bound from the center point
     * @param bottomRot   Rotation of bottom left bound from the center point
     */
    public HyperbolicAxisAlignedBoundingBox(Translation2d topLeft, Translation2d bottomRight, Rotation2d xRot,
            Rotation2d yRot, Rotation2d leftRot, Rotation2d rightRot, Rotation2d topRot, Rotation2d bottomRot) {
        super(topLeft, bottomRight, xRot, yRot);

        // Set rotations
        this.leftRot = leftRot;
        this.rightRot = rightRot;
        this.topRot = topRot;
        this.bottomRot = bottomRot;
    }

    /**
     * Get the theta between the left and right bounds
     * 
     * @return Theta between the left and right bounds
     */
    public Rotation2d getHorizontalTheta() {
        return leftRot.times(-1).plus(rightRot);
    }

    /**
     * Get the theta between the top and bottom bounds
     * 
     * @return Theta between the top and bottom bounds
     */
    public Rotation2d getVerticalTheta() {
        return topRot.times(-1).plus(bottomRot);
    }

    /**
     * Get if the left and right rotations are of equal proportion
     * 
     * @return Is left and right balanced
     */
    public boolean isHorizontalBalanced() {
        return leftRot.equals(rightRot.times(-1));
    }

    /**
     * Get if the top and bottom rotations are of equal proportion
     * 
     * @return Is top and bottom balanced
     */
    public boolean isVerticalBalanced() {
        return topRot.equals(bottomRot.times(-1));
    }

    /**
     * Get if isHorizontalBalanced and isVerticalBalanced are both true
     * 
     * @return Is fully balanced
     */
    public boolean isBalanced() {
        return isHorizontalBalanced() && isVerticalBalanced();
    }

    /**
     * Get the rotation from the center to the left bound
     * 
     * @return Rotation
     */
    public Rotation2d getLeftBoundRotation() {
        return leftRot;
    }

    /**
     * Get the rotation from the center to the right bound
     * 
     * @return Rotation
     */
    public Rotation2d getRightBoundRotation() {
        return rightRot;
    }

    /**
     * Get the rotation from the center to the top bound
     * 
     * @return Rotation
     */
    public Rotation2d getTopBoundRotation() {
        return topRot;
    }

    /**
     * Get the rotation from the center to the bottom bound
     * 
     * @return Rotation
     */
    public Rotation2d getBottomBoundRotation() {
        return bottomRot;
    }

    /**
     * Adds two HyperbolicAxisAlignedBoundingBoxes in 2d space and returns the sum.
     * This is similar to vector addition.
     *
     * @param a The first HyperbolicAxisAlignedBoundingBox
     * @param b The second HyperbolicAxisAlignedBoundingBox
     * @return The sum of the HyperbolicAxisAlignedBoundingBoxes.
     */
    public static HyperbolicAxisAlignedBoundingBox add(HyperbolicAxisAlignedBoundingBox a,
            HyperbolicAxisAlignedBoundingBox b) {

        // Get the underlying bounding box
        AxisAlignedBoundingBox aabb = AxisAlignedBoundingBox.add(a, b);

        // Get the new rotations
        Rotation2d newTopRot = a.getTopBoundRotation().plus(b.getTopBoundRotation());
        Rotation2d newBottomRot = a.getBottomBoundRotation().plus(b.getBottomBoundRotation());
        Rotation2d newLeftRot = a.getLeftBoundRotation().plus(b.getLeftBoundRotation());
        Rotation2d newRightRot = a.getRightBoundRotation().plus(b.getRightBoundRotation());

        // Build output
        return new HyperbolicAxisAlignedBoundingBox(aabb.getTopLeftCorner(), aabb.getBottomRightCorner(),
                aabb.getXRotation(), aabb.getYRotation(), newLeftRot, newRightRot, newTopRot, newBottomRot);

    }

    /**
     * Subtracts the HyperbolicAxisAlignedBoundingBox b from
     * HyperbolicAxisAlignedBoundingBox a
     *
     * @param a The first HyperbolicAxisAlignedBoundingBox
     * @param b The second HyperbolicAxisAlignedBoundingBox
     * @return The result of (a-b)
     */
    public static HyperbolicAxisAlignedBoundingBox subtract(HyperbolicAxisAlignedBoundingBox a,
            HyperbolicAxisAlignedBoundingBox b) {

        // Get the underlying bounding box
        AxisAlignedBoundingBox aabb = AxisAlignedBoundingBox.subtract(a, b);

        // Get the new rotations
        Rotation2d newTopRot = a.getTopBoundRotation().minus(b.getTopBoundRotation());
        Rotation2d newBottomRot = a.getBottomBoundRotation().minus(b.getBottomBoundRotation());
        Rotation2d newLeftRot = a.getLeftBoundRotation().minus(b.getLeftBoundRotation());
        Rotation2d newRightRot = a.getRightBoundRotation().minus(b.getRightBoundRotation());

        // Build output
        return new HyperbolicAxisAlignedBoundingBox(aabb.getTopLeftCorner(), aabb.getBottomRightCorner(),
                aabb.getXRotation(), aabb.getYRotation(), newLeftRot, newRightRot, newTopRot, newBottomRot);

    }

    /**
     * Multiplies a HyperbolicAxisAlignedBoundingBox by a scalar and returns the new
     * HyperbolicAxisAlignedBoundingBox.
     *
     * @param value  Base HyperbolicAxisAlignedBoundingBox
     * @param scalar The scalar to multiply by.
     * @return The scaled HyperbolicAxisAlignedBoundingBox.
     */
    public static HyperbolicAxisAlignedBoundingBox multiply(HyperbolicAxisAlignedBoundingBox value, double scalar) {

        // Get the underlying bounding box
        AxisAlignedBoundingBox aabb = AxisAlignedBoundingBox.multiply(value, scalar);

        // Get the new rotations
        Rotation2d newTopRot = value.getTopBoundRotation().times(scalar);
        Rotation2d newBottomRot = value.getBottomBoundRotation().times(scalar);
        Rotation2d newLeftRot = value.getLeftBoundRotation().times(scalar);
        Rotation2d newRightRot = value.getRightBoundRotation().times(scalar);

        // Build output
        return new HyperbolicAxisAlignedBoundingBox(aabb.getTopLeftCorner(), aabb.getBottomRightCorner(),
                aabb.getXRotation(), aabb.getYRotation(), newLeftRot, newRightRot, newTopRot, newBottomRot);

    }

    /**
     * Divides the HyperbolicAxisAlignedBoundingBox by a scalar and returns the new
     * HyperbolicAxisAlignedBoundingBox.
     * 
     * @param scalar The scalar to multiply by.
     * @return The new HyperbolicAxisAlignedBoundingBox.
     */
    public static HyperbolicAxisAlignedBoundingBox divide(HyperbolicAxisAlignedBoundingBox value, double scalar) {

        // Get the underlying bounding box
        AxisAlignedBoundingBox aabb = AxisAlignedBoundingBox.divide(value, scalar);

        // Get the new rotations
        Rotation2d newTopRot = Rotation2d.fromDegrees(value.getTopBoundRotation().getDegrees() / scalar);
        Rotation2d newBottomRot = Rotation2d.fromDegrees(value.getBottomBoundRotation().getDegrees() / scalar);
        Rotation2d newLeftRot = Rotation2d.fromDegrees(value.getLeftBoundRotation().getDegrees() / scalar);
        Rotation2d newRightRot = Rotation2d.fromDegrees(value.getRightBoundRotation().getDegrees() / scalar);

        // Build output
        return new HyperbolicAxisAlignedBoundingBox(aabb.getTopLeftCorner(), aabb.getBottomRightCorner(),
                aabb.getXRotation(), aabb.getYRotation(), newLeftRot, newRightRot, newTopRot, newBottomRot);

    }
}