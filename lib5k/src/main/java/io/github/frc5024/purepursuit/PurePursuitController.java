package io.github.frc5024.purepursuit;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import io.github.frc5024.purepursuit.pathgen.Path;

/**
 * A pure pursuit controller implementation
 */
public class PurePursuitController {

    // Path follower
    private Follower m_follower;

    // Tracker for last robot pose
    private Pose2d m_lastPose = null;

    // Maximum speed
    private double kMaxOutput;

    // Flag for following path in reverse
    private boolean followReverse;

    /**
     * Create a PurePursuit Controller
     * 
     * @param path              Path to follow
     * @param lookaheadDistance How far to look ahead in meters
     * @param lookaheadGain     Gain on lookahead (0.1 is usually a good val)
     * @param drivebaseWidth    Drivebase width in meters
     * @param kMaxOutput        Maximum output percent
     */
    public PurePursuitController(Path path, double lookaheadDistance, double lookaheadGain, double drivebaseWidth,
            double kMaxOutput) {
        this(path, lookaheadDistance, lookaheadGain, drivebaseWidth, kMaxOutput, false);
    }

    /**
     * Create a PurePursuit Controller
     * 
     * @param path              Path to follow
     * @param lookaheadDistance How far to look ahead in meters
     * @param lookaheadGain     Gain on lookahead (0.1 is usually a good val)
     * @param drivebaseWidth    Drivebase width in meters
     * @param kMaxOutput        Maximum output percent
     * @param followReverse     Should the path be followed with the back of the
     *                          chassis?
     */
    public PurePursuitController(Path path, double lookaheadDistance, double lookaheadGain, double drivebaseWidth,
            double kMaxOutput, boolean followReverse) {

        // Configure follower
        m_follower = new Follower(path, lookaheadDistance, lookaheadGain, drivebaseWidth);

        this.kMaxOutput = kMaxOutput;
        this.followReverse = followReverse;
    }

    /**
     * Reset the controller
     */
    public void reset() {
        m_follower.reset();
        m_lastPose = null;
    }

    /**
     * Set the lookahead distance
     * 
     * @param lookaheadDistance How far to look ahead in meters
     */
    public void configLookahead(double lookaheadDistance) {
        m_follower.setLookaheadDistance(lookaheadDistance);
    }

    /**
     * Set the lookahead distance and gain
     * 
     * @param lookaheadDistance How far to look ahead in meters
     * @param lookaheadGain     Gain on lookahead (0.1 is usually a good val)
     */
    public void configLookahead(double lookaheadDistance, double lookaheadGain) {
        m_follower.setLookaheadDistance(lookaheadDistance);
        m_follower.setLookaheadGain(lookaheadGain);
    }

    /**
     * Get the goal, but rotated to be relative to the robot, not the field
     * 
     * @param robotPose Robot's pose
     * @return Rotated goal
     */
    private Translation2d getRotatedGoal(Pose2d robotPose) {

        // Get our goal pose from the follower
        Translation2d goal = m_follower.getNextPoint(robotPose);

        // Determine chassis coords on the field
        Translation2d chassisCoords = robotPose.getTranslation();
        Rotation2d chassisAngle = robotPose.getRotation();

        // Determine goal alpha
        double alpha = 0.0;

        // Flip front and back if following backwards
        if (followReverse) {
            // chassisAngle = chassisAngle.plus(Rotation2d.fromDegrees(180.0));
            alpha = Math.atan2(chassisCoords.getY() - goal.getY(), chassisCoords.getX() - goal.getX())
                    - chassisAngle.getRadians();
        } else {
            alpha = Math.atan2(goal.getY() - chassisCoords.getY(), goal.getX() - chassisCoords.getX())
                    - chassisAngle.getRadians();
        }

        // Determine our velocity from the last pose
        double v;
        if (m_lastPose != null) {
            double dx = robotPose.getTranslation().getX() - m_lastPose.getTranslation().getX();
            double dy = robotPose.getTranslation().getY() - m_lastPose.getTranslation().getY();

            // Calc V
            v = Math.hypot(dx, dy);
        } else {
            v = 0.0;
        }

        // Set the last pose
        m_lastPose = robotPose;

        // Determine lookahead
        double LF = m_follower.getLookaheadGain() * v * m_follower.getLookaheadDistance();

        // Handle DivByZero
        if (LF == 0.0) {
            LF = 0.00000001;
        }

        // Determine delta
        double delta = 0.0;

        if (MathUtils.epsilonEquals(alpha, 0.0, 1e-12)) {
            delta = MathUtils.clamp(Math.hypot(goal.getX() - chassisCoords.getX(), goal.getY() - chassisCoords.getY()),
                    -1, 1);
        } else {
            delta = Math.atan2(2.0 * m_follower.getDrivebaseWidth() * Math.sin(alpha) / LF, 1.0);
        }

        // If going reverse, negate delta
        if (followReverse) {
            delta *= -1;
        }

        return new Translation2d(delta, alpha);
    }

    /**
     * Calculate outputs for a tank drivebase
     * 
     * @param robotPose Robot's pose
     * @return Output velocity percentages
     */
    public DifferentialDriveWheelSpeeds calculateTank(Pose2d robotPose) {

        // Get the rotated goal from the current pose
        Translation2d goal = getRotatedGoal(robotPose);

        // Multiplier for speed. Can be affected by multiple factors to make motion look
        // smoother
        double speedMul = 1.0;

        // If the turn is too big, slow down the chassis to allow for the turn to be
        // taken safely
        if (Math.abs(goal.getY()) > 1.0) {
            speedMul = 0.1;
        }

        // Determine left and right velocity goals
        double left = (goal.getX() * speedMul) + goal.getY();
        double right = (goal.getX() * speedMul) - goal.getY();

        // Find the maximum magnitude between both velocities
        double magnitude = Math.max(Math.abs(left), Math.abs(right));

        // Scale back velocities if the max magnitude is greater than 100%
        if (magnitude > 1.) {
            left /= magnitude;
            right /= magnitude;
        }

        // Limit left and right
        left *= kMaxOutput;
        right *= kMaxOutput;

        return new DifferentialDriveWheelSpeeds(left, right);

    }

    /**
     * Calculate outputs for a holonomic drivebase
     * 
     * @param robotPose Robot's pose
     * @return Vector facing in the desired direction of travel, with magnitude
     *         matching the desired speed
     */
    public Translation2d calculateHolonomic(Pose2d robotPose) {

        // Get the rotated goal from the current pose
        Translation2d goal = m_follower.getNextPoint(robotPose);

        // Clamp the speeds
        double x = MathUtils.clamp(goal.getX() - robotPose.getTranslation().getX(), -kMaxOutput, kMaxOutput);
        double y = MathUtils.clamp(goal.getY() - robotPose.getTranslation().getY(), -kMaxOutput, kMaxOutput);

        // We can just treat the goal as a unit vector pointing at the direction we want
        // to move.
        return new Translation2d(x, y);

    }

}