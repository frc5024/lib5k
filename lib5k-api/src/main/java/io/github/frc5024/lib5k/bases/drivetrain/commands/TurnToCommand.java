package io.github.frc5024.lib5k.bases.drivetrain.commands;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.frc5024.lib5k.bases.drivetrain.AbstractDriveTrain;
import io.github.frc5024.lib5k.logging.RobotLogger;

/**
 * A command for making drivetrains turn to a specific heading.
 */
public class TurnToCommand extends CommandBase {

    // Logger
    private RobotLogger logger = RobotLogger.getInstance();

    // DriveTrain
    private AbstractDriveTrain driveTrain;

    // Goal heading
    private Rotation2d goalHeading;
    private Rotation2d epsilon;
    private boolean fieldRelative;

    // Max speed
    private double maxSpeedPercent;

    /**
     * Create a new TurnToCommand
     * 
     * @param driveTrain DriveTrain to control
     * @param fieldRelativeAngle      Desired heading (field-relative)
     * @param epsilon    Rotational epsilon
     */
    public TurnToCommand(AbstractDriveTrain driveTrain, Rotation2d fieldRelativeAngle, Rotation2d epsilon) {
        this(driveTrain, fieldRelativeAngle, epsilon, true);
    }

    /**
     * Create a new TurnToCommand
     * 
     * @param driveTrain    DriveTrain to control
     * @param angle         Desired heading
     * @param epsilon       Rotational epsilon
     * @param fieldRelative Is the desired heading field-relative?
     */
    public TurnToCommand(AbstractDriveTrain driveTrain, Rotation2d angle, Rotation2d epsilon, boolean fieldRelative) {
        this(driveTrain, angle, epsilon, 1.0, fieldRelative);
    }

    /**
     * Create a new TurnToCommand
     * 
     * @param driveTrain DriveTrain to control
     * @param fieldRelativeAngle      Desired heading (field-relative)
     * @param epsilon    Rotational epsilon
     * @param speedCap   Maximum speed as a percent
     */
    public TurnToCommand(AbstractDriveTrain driveTrain, Rotation2d fieldRelativeAngle, Rotation2d epsilon,
            double speedCap) {
        this(driveTrain, fieldRelativeAngle, epsilon, speedCap, true);

    }

    /**
     * Create a new TurnToCommand
     * 
     * @param driveTrain    DriveTrain to control
     * @param angle         Desired heading
     * @param epsilon       Rotational epsilon
     * @param speedCap      Maximum speed as a percent
     * @param fieldRelative Is the desired heading field-relative?
     */
    public TurnToCommand(AbstractDriveTrain driveTrain, Rotation2d angle, Rotation2d epsilon, double speedCap,
            boolean fieldRelative) {
        this.driveTrain = driveTrain;
        this.goalHeading = angle;
        this.epsilon = epsilon;
        this.fieldRelative = fieldRelative;
        this.maxSpeedPercent = speedCap;
    }

    @Override
    public void initialize() {
        logger.log("Beginning TurnToCommand");

        // Set the drivetrain speed
        driveTrain.setMaxSpeedPercent(this.maxSpeedPercent);

        // Handle robot-relative vs field-relative angle
        Rotation2d adjustedGoalHeading = null;
        if (fieldRelative) {
            adjustedGoalHeading = this.goalHeading;
        } else {
            // Get the current heading
            Rotation2d currentHeading = driveTrain.getPose().getRotation();
            // Add the angles
            adjustedGoalHeading = currentHeading.plus(this.goalHeading);
        }

        // Set the drivetrain goal
        driveTrain.setGoalHeading(adjustedGoalHeading, this.epsilon);
    }

    @Override
    public void execute() {
    }

    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            logger.log("Path following was interrupted.");
        } else {
            logger.log(String.format("Robot successfully reached goal heading: %s", goalHeading.toString()));
        }

        // Stop the robot
        driveTrain.reset();
    }

    @Override
    public boolean isFinished() {
        return driveTrain.isAtGoal();
    }
}