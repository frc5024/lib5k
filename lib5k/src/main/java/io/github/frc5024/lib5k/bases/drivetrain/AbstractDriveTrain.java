package io.github.frc5024.lib5k.bases.drivetrain;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.commands.PathFollowerCommand;
import io.github.frc5024.lib5k.bases.drivetrain.commands.TurnToCommand;
import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.FRCFieldConstants;
import io.github.frc5024.lib5k.utils.interfaces.SafeSystem;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;
import io.github.frc5024.purepursuit.pathgen.Path;

public abstract class AbstractDriveTrain extends SubsystemBase
        implements IDifferentialDrivebase, SafeSystem, AutoCloseable {

    // Logging
    protected RobotLogger logger = RobotLogger.getInstance();

    // State Machine
    protected enum State {
        kOpenLoopControl, // Being controlled manually
        kAutonomousRotation, // Rotating to a new heading
        kDrivingToPose, // Driving to a new pose
    }

    protected StateMachine<State> stateMachine;

    // Goals
    private Rotation2d goalHeading = null;
    private Rotation2d goalHeadingEpsilon = null;
    private Translation2d goalPose = null;
    private Translation2d goalPoseEpsilon = null;
    private boolean waitingForNextLoop = false;

    // Simulation
    private Field2d simField = new Field2d();

    /**
     * Create an AbstractDriveTrain
     */
    public AbstractDriveTrain() {

        // Set up state machine
        stateMachine = new StateMachine<>("AbstractDriveTrain");
        stateMachine.setDefaultState(State.kOpenLoopControl, this::handleOpenLoopControl);
        stateMachine.addState(State.kAutonomousRotation, (meta) -> {
            handleAutonomousRotation(meta, goalHeading, goalHeadingEpsilon);
        });
        stateMachine.addState(State.kDrivingToPose, (meta) -> {
            handleDrivingToPose(meta, goalPose, goalPoseEpsilon);
        });

    }

    /**
     * TO BE OVERRIDDEN BY THE USER. Handler for gear shifting
     * 
     * @param gear Gear to shift to
     */
    protected abstract void handleGearShift(Gear gear);

    /**
     * TO BE OVERRIDDEN BY THE USER. Handler for enabling the brakes.
     * 
     * @param enabled Should brakes be enabled?
     */
    protected abstract void enableBrakes(boolean enabled);

    /**
     * TO BE OVERRIDDEN BY THE USER. Getter for the robot's current heading.
     * Clockwise-positive
     * 
     * @return Robot's heading
     */
    protected abstract Rotation2d getCurrentHeading();

    /**
     * TO BE OVERRIDDEN BY THE USER. Any extra code that needs to be run every loop
     * should go here.
     */
    protected abstract void runIteration();

    /**
     * TO BE OVERRIDDEN BY THE USER. Handler for ramp rate setting
     * 
     * @param rampTimeSeconds Ramp rate time in seconds
     */
    public abstract void setRampRate(double rampTimeSeconds);

    /**
     * Set which side of the chassis is the "front". Used in autonomous path
     * following
     * 
     * @param side Which side is the new front
     */
    public abstract void setFrontSide(Chassis.Side side);

    /**
     * Get the robot's current pose
     * 
     * @return Current pose
     */
    public abstract Pose2d getPose();

    /**
     * Set a new pose for the robot
     * 
     * @param pose New pose
     */
    public abstract void resetPose(Pose2d pose);

    /**
     * Get the robot's velocity in meters per period
     * 
     * @return Velocity
     */
    public abstract Translation2d getVelocity();

    /**
     * Get the robot's speed in meters per period
     * 
     * @return Robot speed
     */
    public double getSpeed() {
        Translation2d velocity = getVelocity();
        return Math.hypot(velocity.getX(), velocity.getY());
    }

    /**
     * State handler for open-loop control
     * 
     * @param meta State metadata
     */
    protected abstract void handleOpenLoopControl(StateMetadata<State> meta);

    /**
     * Set the maximum speed percent
     * 
     * @param maxSpeedPercent Maximum speed percent
     */
    public abstract void setMaxSpeedPercent(double maxSpeedPercent);

    /**
     * State handler for autonomous rotation control
     * 
     * @param meta        State metadata
     * @param goalHeading Goal heading
     * @param epsilon     Rotation epsilon
     */
    protected abstract void handleAutonomousRotation(StateMetadata<State> meta, Rotation2d goalHeading,
            Rotation2d epsilon);

    /**
     * State handler for driving to a new pose autonomously
     * 
     * @param meta     State metadata
     * @param goalPose Goal pose
     * @param epsilon  Epsilon around the goal pose in meters
     */
    protected abstract void handleDrivingToPose(StateMetadata<State> meta, Translation2d goalPose,
            Translation2d epsilon);

    /**
     * Set a goal pose for the robot to be at, and immediately start driving to it.
     * 
     * @param pose    Field-relative pose
     * @param epsilon Pose epsilon in meters
     */
    public void setGoalPose(Pose2d pose, Translation2d epsilon) {
        setGoalPose(pose.getTranslation(), epsilon);
    }

    /**
     * Set a goal pose for the robot to be at, and immediately start driving to it.
     * 
     * @param pose    Field-relative pose
     * @param epsilon Pose epsilon in meters
     */
    public void setGoalPose(Translation2d pose, Translation2d epsilon) {
        waitingForNextLoop = true;
        this.goalPose = pose;
        this.goalPoseEpsilon = new Translation2d(Math.abs(epsilon.getX()), Math.abs(epsilon.getY()));
        this.stateMachine.setState(State.kDrivingToPose);
    }

    /**
     * Set a field-relative heading for the robot to face, and immediately start
     * facing it.
     * 
     * @param heading Field-relative heading goal
     * @param epsilon Heading epsilon
     */
    public void setGoalHeading(Rotation2d heading, Rotation2d epsilon) {
        waitingForNextLoop = true;
        this.goalHeading = heading;
        this.goalHeadingEpsilon = new Rotation2d(Math.abs(epsilon.getRadians()));
        this.stateMachine.setState(State.kAutonomousRotation);
    }

    @Override
    public void periodic() {
        // Run user periodic code
        runIteration();

        // Run state machine
        stateMachine.update();
        waitingForNextLoop = false;

        // Save the field location
        simField.setRobotPose(getPose().plus(FRCFieldConstants.LIB5K_TO_WPILIB_COORDINATE_TRANSFORM));
    }

    /**
     * Create and configure a command that will follow a path using this drivetrain.
     * This returns a builder-style object, where you can chain extra methods to
     * configure the command
     * 
     * @param path      Path to follow
     * @param epsRadius Radius in meters around the final pose for trigger
     *                  isFinished()
     * @return Path following command
     */
    public PathFollowerCommand createPathingCommand(Path path, double epsRadius) {
        return new PathFollowerCommand(this, path, epsRadius);
    }

    /**
     * Create a new TurnToCommand for this drivetrain.
     * 
     * @param heading         Desired heading
     * @param epsilon         Rotational epsilon
     * @param maxSpeedPercent Maximum speed as a percent
     * @param fieldRelative   Is the desired heading field-relative?
     * @return Generated TurnToCommand object
     */
    public TurnToCommand createTurnCommand(Rotation2d heading, Rotation2d epsilon, double maxSpeedPercent,
            boolean fieldRelative) {
        return new TurnToCommand(this, heading, epsilon, maxSpeedPercent, fieldRelative);
    }

    /**
     * Get the internal state of the DriveTrain
     * 
     * @return Current internal state
     */
    public State getCurrentState() {
        return stateMachine.getCurrentState();
    }

    /**
     * Check if the drivetrain is currently at its goal
     * 
     * @return Is currently at goal
     */
    public boolean isAtGoal() {
        return !waitingForNextLoop && getCurrentState().equals(State.kOpenLoopControl);
    }

    @Override
    public void stop() {
        logger.log("Stopping");

    }

    @Override
    public void reset() {

        // Stop before resetting
        stop();

        logger.log("Resetting");

    }

    @Override
    public void close() throws Exception {

    }

}
