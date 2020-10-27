package io.github.frc5024.lib5k.vision.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import io.github.frc5024.lib5k.utils.RobotMath;

public class AxisAlignedBoundingBoxTest {

    @Test
    public void testAspectRatioCalculation() {

        // Create a box
        AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(new Translation2d(-1, 2), new Translation2d(1, -2));

        // Check aspect ratio
        assertEquals("Aspect ratio", 2. / 4., box.getAspectRatio(), RobotMath.kVerySmallNumber);

        // Check sizing
        assertEquals("Width", 2., box.getWidth(), RobotMath.kVerySmallNumber);
        assertEquals("Height", 4., box.getHeight(), RobotMath.kVerySmallNumber);

    }

    @Test
    public void testPositionCalculation() {

        // Create a box
        AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(new Translation2d(-1, -2), new Translation2d(1, 2));

        // Check Center
        assertEquals("CX", 0, box.getX(), RobotMath.kVerySmallNumber);
        assertEquals("CY", 0, box.getY(), RobotMath.kVerySmallNumber);

        // Check corners
        assertEquals("TLX", -1, box.getTopLeftCorner().getX(), RobotMath.kVerySmallNumber);
        assertEquals("TLY", -2, box.getTopLeftCorner().getY(), RobotMath.kVerySmallNumber);
        assertEquals("BLX", -1, box.getBottomLeftCorner().getX(), RobotMath.kVerySmallNumber);
        assertEquals("BLY", 2, box.getBottomLeftCorner().getY(), RobotMath.kVerySmallNumber);
        assertEquals("TRX", 1, box.getTopRightCorner().getX(), RobotMath.kVerySmallNumber);
        assertEquals("TRY", -2, box.getTopRightCorner().getY(), RobotMath.kVerySmallNumber);
        assertEquals("BRX", 1, box.getBottomRightCorner().getX(), RobotMath.kVerySmallNumber);
        assertEquals("BRY", 2, box.getBottomRightCorner().getY(), RobotMath.kVerySmallNumber);

    }

    @Test
    public void testBoxContains() {

        // Create a box
        AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(new Translation2d(-1, 2), new Translation2d(1, -2));

        // Check aspect ratio
        assertTrue("Contains", box.contains(new Translation2d(0.5, 0.5)));
        assertFalse("Does not contain", box.contains(new Translation2d(1.5, 3.5)));

    }

    @Test
    public void testBoxOverlaps() {

        // Create a box
        AxisAlignedBoundingBox box1 = new AxisAlignedBoundingBox(new Translation2d(-1, 2), new Translation2d(1, -2));
        AxisAlignedBoundingBox box2 = new AxisAlignedBoundingBox(new Translation2d(0, 0), new Translation2d(2, 2));
        AxisAlignedBoundingBox box3 = new AxisAlignedBoundingBox(new Translation2d(5, 5), new Translation2d(6, 6));

        // Check aspect ratio
        assertTrue("Overlaps", box1.overlaps(box2));
        assertFalse("Does not overlap", box1.overlaps(box3));

    }

}