package io.github.frc5024.purepursuit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import io.github.frc5024.purepursuit.pathgen.Path;

public class FollowerTest {

    // Lookahead distance to test against
    double LOOKAHEAD = 0.2;
    double GAIN = 0.1;

    @Test
    /**
     * Tests to make sure the follower can find goals both to the left and right of
     * the chassis
     */
    public void testForwardGoal() {

        // Follower to test against
        Follower follower = new Follower(new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 1.0)), LOOKAHEAD,
                GAIN, Units.inchesToMeters(28.0));

        // Check for appropriate final pose
        assert follower.getFinalPose().equals(new Translation2d(1.0, 1.0));

        // Discard any pose inside the lookahead
        Translation2d goal = follower.getNextPoint(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));
        // while (Math.hypot(goal.getX(), goal.getY()) < LOOKAHEAD) {

        // // Grab a new goal
        // goal = follower.getNextPoint(new Pose2d(0.0, 0.0,
        // Rotation2d.fromDegrees(0.0)));
        // }

        // Check that the found point is forward, and to the right of the "chassis" when
        // sitting at (0,0)
        assert goal.getX() > 0.0;
        assert goal.getY() > 0.0;

        // Go to the right of the path, and test again
        goal = follower.getNextPoint(new Pose2d(1.0, 0.0, Rotation2d.fromDegrees(0.0)));
        assert goal.getX() > 0.0;
        assert goal.getY() > 0.0;
    }

    @Test
    /**
     * Tests to make sure the follower can find goals both to the left and right of
     * the chassis but behind it
     */
    public void testReverseGoal() {

        // Follower to test against
        Follower follower = new Follower(new Path(new Translation2d(0.0, 0.0), new Translation2d(-1.0, 1.0)), LOOKAHEAD,
                GAIN, Units.inchesToMeters(28.0));

        // Check for appropriate final pose
        assert follower.getFinalPose().equals(new Translation2d(-1.0, 1.0));

        // Discard any pose inside the lookahead
        Translation2d goal = follower.getNextPoint(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));
        // while (Math.hypot(goal.getX(), goal.getY()) < LOOKAHEAD) {

        // // Grab a new goal
        // goal = follower.getNextPoint(new Pose2d(0.0, 0.0,
        // Rotation2d.fromDegrees(0.0)));
        // }

        // Check that the found point is forward, and to the right of the "chassis" when
        // sitting at (0,0)
        assert goal.getX() < 0.0;
        assert goal.getY() > 0.0;

        // Go to the right of the path, and test again
        goal = follower.getNextPoint(new Pose2d(1.0, 0.0, Rotation2d.fromDegrees(0.0)));
        assert goal.getX() < 0.0;
        assert goal.getY() > 0.0;
    }

    @Test
    public void testFollowerMaintainsProperSpacing() {

        // Follower to test against
        Follower follower = new Follower(new Path(new Translation2d(0.0, 0.0), new Translation2d(10.0, 10.0)),
                LOOKAHEAD, GAIN, Units.inchesToMeters(28.0));

        // Loop until we start seeing the same point multiple times
        Translation2d lastPose = new Translation2d(-1, -1);
        while (true) {

            // Get the next pose
            Translation2d nextPose = follower.getNextPoint(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

            // Compare to the last
            if (nextPose.equals(lastPose)) {
                lastPose = nextPose;
                break;
            }

            lastPose = nextPose;
        }

        // Check if the pose we got "stuck on" is near the lookahead gain
        assertEquals("Farthest acceptable pose", true, MathUtils
                .epsilonEquals(new Translation2d(0.0, 0.0).getDistance(lastPose), GAIN, Units.inchesToMeters(6.0)));
    }
}