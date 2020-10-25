package io.github.frc5024.lib5k.vision.types;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * HyperbolicAxisAlignedBoundingBox is a non-euclidean
 * {@link AxisAlignedBoundingBox} in hyperbolic space
 */
public class HyperbolicAxisAlignedBoundingBox extends AxisAlignedBoundingBox {

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

}