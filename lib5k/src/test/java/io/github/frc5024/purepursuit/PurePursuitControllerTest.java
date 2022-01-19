package io.github.frc5024.purepursuit;

import org.junit.Test;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.util.Units;
import io.github.frc5024.purepursuit.pathgen.Path;

public class PurePursuitControllerTest {

    @Test
    /**
     * Test a forward goal with tank drive
     */
    public void testForwardTank() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 0.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0);

        // Get the next goal
        DifferentialDriveWheelSpeeds goal = controller.calculateTank(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure both tracks are moving forward
        assert goal.leftMetersPerSecond > 0.0;
        assert goal.rightMetersPerSecond > 0.0;

        // Ensure both powers are equal
        assert MathUtils.epsilonEquals(goal.leftMetersPerSecond , goal.rightMetersPerSecond, 1e-12);

    }

    @Test
    /**
     * Test a goal directly to the side with tank drive
     */
    public void testSidewaysTank() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(0.0, 1.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0);

        // Get the next goal
        DifferentialDriveWheelSpeeds goal = controller.calculateTank(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure tracks are moving in the correct directions
        assert goal.leftMetersPerSecond > 0.0;
        assert goal.rightMetersPerSecond < 0.0;

    }

    @Test
    /**
     * Test a goal both to the front and side, with a small angle with tank drive
     */
    public void testSmallAngularTank() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 0.5)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0);

        // Get the next goal
        DifferentialDriveWheelSpeeds goal = controller.calculateTank(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure tracks are moving forward
        assert goal.leftMetersPerSecond > 0.0;
        assert goal.rightMetersPerSecond > 0.0;

        // Ensure left track is pushing more power than right
        assert goal.leftMetersPerSecond > goal.rightMetersPerSecond;

        // Ensure both tracks are outputting unrestricted power
        assert goal.leftMetersPerSecond > 0.1;
        assert goal.rightMetersPerSecond > 0.1;

    }

    @Test
    /**
     * Test a goal both to the front and side, with a large angle with tank drive
     */
    public void testLargeAngularTank() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(0.5, 1.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0);

        // Get the next goal
        DifferentialDriveWheelSpeeds goal = controller.calculateTank(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure tracks are counter-rotating
        assert goal.leftMetersPerSecond > 0.0;
        assert goal.rightMetersPerSecond < 0.0;

        // Ensure left track is unrestricted, and right is restricted
        assert Math.abs(goal.rightMetersPerSecond) < Math.abs(goal.leftMetersPerSecond);

    }

    @Test
    /**
     * Test a reverse goal with tank drive
     */
    public void testReverseTank() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(-1.0, 0.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0, true);

        // Get the next goal
        DifferentialDriveWheelSpeeds goal = controller.calculateTank(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure both tracks are moving backward
        assert goal.leftMetersPerSecond < 0.0;
        assert goal.rightMetersPerSecond < 0.0;

        // Ensure both powers are equal
        assert MathUtils.epsilonEquals(goal.leftMetersPerSecond , goal.rightMetersPerSecond, 1e-12);
    }

    @Test
    /**
     * Test a goal both to the front and side, with a small angle with tank drive
     */
    public void testReverseSmallAngularTank() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(-1.0, -0.5)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0, true);

        // Get the next goal
        DifferentialDriveWheelSpeeds goal = controller.calculateTank(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure tracks are moving backward
        assert goal.leftMetersPerSecond < 0.0;
        assert goal.rightMetersPerSecond < 0.0;

        // Ensure right track is pushing more power than left
        assert Math.abs(goal.leftMetersPerSecond) < Math.abs(goal.rightMetersPerSecond);

        // Ensure both tracks are outputting unrestricted power
        assert Math.abs(goal.leftMetersPerSecond) > 0.1;
        assert Math.abs(goal.rightMetersPerSecond) > 0.1;

    }

    @Test
    /**
     * Test a goal both to the front and side, with a large angle with tank drive
     */
    public void testReverseLargeAngularTank() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(-0.5, -1.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0, true);

        // Get the next goal
        DifferentialDriveWheelSpeeds goal = controller.calculateTank(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure tracks are counter-rotating
        assert goal.leftMetersPerSecond > 0.0;
        assert goal.rightMetersPerSecond < 0.0;

        // Ensure right track is unrestricted, and left is restricted
        assert Math.abs(goal.rightMetersPerSecond) > Math.abs(goal.leftMetersPerSecond);

    }

    @Test
    /**
     * Test a path at a slow speed with tank drive
     */
    public void testSlowTank() {
        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 0.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 0.5);

        // Get the next goal
        DifferentialDriveWheelSpeeds goal = controller.calculateTank(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure both tracks are moving forward
        assert goal.leftMetersPerSecond > 0.0;
        assert goal.rightMetersPerSecond > 0.0;

        // Ensure neither power is above 0.5
        assert goal.leftMetersPerSecond <= 0.5;
        assert goal.rightMetersPerSecond <= 0.5;

        // Ensure both powers are equal
        assert goal.leftMetersPerSecond == goal.rightMetersPerSecond;

    }

    @Test
    /**
     * Test a forward goal with holonomic drive
     */
    public void testForwardHolo() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 0.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0);

        // Get the next goal
        Translation2d goal = controller.calculateHolonomic(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure chassis is moving forwards
        assert goal.getX() > 0.0;

        // Ensure chassis is not moving sideways
        assert goal.getY() == 0.0;

    }

    @Test
    /**
     * Test an angular goal with holonomic drive
     */
    public void testAngularHolo() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 1.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0);

        // Get the next goal
        Translation2d goal = controller.calculateHolonomic(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure chassis is moving forwards and right
        assert goal.getX() > 0.0;
        assert goal.getY() > 0.0;

        // Ensure chassis is moving equally in both directions
        assert MathUtils.epsilonEquals(goal.getX(), goal.getY(), 1e-12);

    }

    @Test
    /**
     * Test a goal directly to the side with holonomic drive
     */
    public void testSidewaysHolo() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(0.0, 1.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0);

        // Get the next goal
        Translation2d goal = controller.calculateHolonomic(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure chassis is not moving forwards
        assert MathUtils.epsilonEquals(goal.getX(), 0.0, 1e-12);

        // Ensure chassis is moving to the right
        assert goal.getY() > 0.0;
    }

    @Test
    /**
     * Test a path at a slow speed with holonomic drive
     */
    public void testSlowHolo() {

        // Create a controller
        PurePursuitController controller = new PurePursuitController(
                new Path(new Translation2d(0.0, 0.0), new Translation2d(1.0, 0.0)), 0.2, 0.1,
                Units.inchesToMeters(28.0), 1.0);

        // Get the next goal
        Translation2d goal = controller.calculateHolonomic(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));

        // Ensure chassis is moving forwards
        assert goal.getX() > 0.0;

        // Ensure chassis velocity is not above 0.5
        assert goal.getX() <= 0.5;

        // Ensure chassis is not moving sideways
        assert goal.getY() == 0.0;

    }
}