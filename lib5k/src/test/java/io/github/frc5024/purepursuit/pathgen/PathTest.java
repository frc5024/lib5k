package io.github.frc5024.purepursuit.pathgen;


import org.junit.Test;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.math.geometry.Translation2d;

public class PathTest {

    /**
     * Compares two poses
     * 
     * @param a Pose A
     * @param b Pose B
     * @return Are equal?
     */
    private boolean comparePoses(Translation2d a, Translation2d b) {
        return (MathUtils.epsilonEquals(a.getX(), b.getX(), 8e-3))
                && (MathUtils.epsilonEquals(a.getY(), b.getY(), 8e-3));
    }

    @Test
    /**
     * Test that points are correctly spaced during path generation in 1 dimension.
     */
    public void test1DSpacing() {

        // Create a path with spacing of 25cm for 1m displacement
        Path path = new Path(0.25, new Translation2d(0.0, 0.0), new Translation2d(1.0, 0.0));
        Translation2d[] poses = path.getPoses();

        // Ensure the correct number of points are placed
        assert poses.length == 4;

        // Ensure each point is placed in the correct location
        assert poses[0].equals(new Translation2d(0.25, 0.0));
        assert poses[1].equals(new Translation2d(0.50, 0.0));
        assert poses[2].equals(new Translation2d(0.75, 0.0));
        assert poses[3].equals(new Translation2d(1.0, 0.0));

    }

    @Test
    /**
     * Test that points are correctly spaced during path generation in 2 dimensions.
     */
    public void test2DSpacing() {

        // Create a path with spacing of 25cm for [1,1]m displacement
        Path path = new Path(0.25, new Translation2d(0.0, 0.0), new Translation2d(1.0, 1.0));
        Translation2d[] poses = path.getPoses();

        // Ensure the correct number of points are placed
        assert poses.length == 6;

        // Ensure each point is placed in the correct location
        assert comparePoses(poses[0], new Translation2d(0.18, 0.18));
        assert comparePoses(poses[1], new Translation2d(0.35, 0.35));
        assert comparePoses(poses[2], new Translation2d(0.53, 0.53));
        assert comparePoses(poses[3], new Translation2d(0.71, 0.71));
        assert comparePoses(poses[4], new Translation2d(0.88, 0.88));
        assert comparePoses(poses[5], new Translation2d(1.0, 1.0));

    }
}