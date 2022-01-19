package io.github.frc5024.lib5k.vision.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import io.github.frc5024.lib5k.utils.RobotMath;

public class HyperbolicAxisAlignedBoundingBoxTest {

    @Test
    public void testThetaCalculation() {

        // Build a box
        HyperbolicAxisAlignedBoundingBox box = new HyperbolicAxisAlignedBoundingBox(new Translation2d(-1, 2),
                new Translation2d(1, -2), Contour.NO_ROTATION, Contour.NO_ROTATION, Rotation2d.fromDegrees(-10),
                Rotation2d.fromDegrees(15), Rotation2d.fromDegrees(-10), Rotation2d.fromDegrees(15));

        // Check the math
        assertEquals("HRot calculation", 25.0, box.getHorizontalTheta().getDegrees(), RobotMath.kVerySmallNumber);
        assertEquals("VRot calculation", 25.0, box.getVerticalTheta().getDegrees(), RobotMath.kVerySmallNumber);

        // Ensure this is not a balanced box
        assertFalse("Horizontally balanced", box.isHorizontalBalanced());
        assertFalse("Vertically balanced", box.isVerticalBalanced());
        assertFalse("Balanced", box.isBalanced());

    }

    @Test
    public void testBalancedBox() {

        // Build a box
        HyperbolicAxisAlignedBoundingBox box = new HyperbolicAxisAlignedBoundingBox(new Translation2d(-1, 2),
                new Translation2d(1, -2),  Rotation2d.fromDegrees(10), Rotation2d.fromDegrees(20));

        // Check the math
        assertEquals("LRot calculation", -5.0, box.getLeftBoundRotation().getDegrees(), RobotMath.kVerySmallNumber);
        assertEquals("RRot calculation", 5.0, box.getRightBoundRotation().getDegrees(), RobotMath.kVerySmallNumber);
        assertEquals("TRot calculation", 10.0, box.getTopBoundRotation().getDegrees(), RobotMath.kVerySmallNumber);
        assertEquals("BRot calculation", -10.0, box.getBottomBoundRotation().getDegrees(), RobotMath.kVerySmallNumber);

        // Ensure this is a balanced box
        assertTrue("Horizontally balanced", box.isHorizontalBalanced());
        assertTrue("Vertically balanced", box.isVerticalBalanced());
        assertTrue("Balanced", box.isBalanced());

    }

}