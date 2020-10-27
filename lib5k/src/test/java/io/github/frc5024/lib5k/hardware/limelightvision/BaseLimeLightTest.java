package io.github.frc5024.lib5k.hardware.limelightvision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import io.github.frc5024.lib5k.utils.RobotMath;
import io.github.frc5024.lib5k.vision.types.HyperbolicAxisAlignedBoundingBox;

public class BaseLimeLightTest {

    @Test
    public void testTargetConstruction() {

        // NT instance
        NetworkTable table = NetworkTableInstance.getDefault().getTable("unittestlimelight");

        // Limelight
        BaseLimeLight limeLight = new BaseLimeLight(table, "limelight.local", 27, 20.5) {
        };

        // Ensure the limelight has no target
        assertFalse("Can see target", limeLight.hasTarget());

        // Show a target
        table.getEntry("tv").setNumber(1);
        table.getEntry("tx").setNumber(13.5);
        table.getEntry("ty").setNumber(0.0);
        table.getEntry("thor").setNumber(160);
        table.getEntry("tvert").setNumber(80);

        // Ensure the limelight has a target
        assertTrue("Can see target", limeLight.hasTarget());

        // Get the target
        HyperbolicAxisAlignedBoundingBox target = limeLight.getTargetBoundsOrNull();

        // Ensure target exists
        assertNotNull(target);

        System.out.println(target);

        // Check target position
        assertEquals("CX", 0.5, target.getX(), RobotMath.kVerySmallNumber);
        assertEquals("CY", 0.0, target.getY(), RobotMath.kVerySmallNumber);

        assertEquals("TLX", 0.0, target.getTopLeftCorner().getX(), RobotMath.kVerySmallNumber);
        assertEquals("TLY", 0.25, target.getTopLeftCorner().getY(), RobotMath.kVerySmallNumber);
        assertEquals("BRX", 1.0, target.getBottomRightCorner().getX(), RobotMath.kVerySmallNumber);
        assertEquals("BRY", -0.25, target.getBottomRightCorner().getY(), RobotMath.kVerySmallNumber);

    }

}