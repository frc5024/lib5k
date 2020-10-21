package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;

import io.github.frc5024.lib5k.bases.drivetrain.AbstractDriveTrain;
import io.github.frc5024.lib5k.bases.drivetrain.Chassis;
import io.github.frc5024.lib5k.bases.drivetrain.Chassis.Side;
import io.github.frc5024.lib5k.control_loops.base.Controller;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.math.DifferentialDriveMath;
import io.github.frc5024.lib5k.math.RotationMath;
import io.github.frc5024.lib5k.utils.RobotMath;
import io.github.frc5024.lib5k.utils.types.DifferentialVoltages;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * TankDriveTrain is an implementation of AbstractDriveTrain for tank-drive
 * robots.
 */
public abstract class TankDriveTrain extends AbstractDriveTrain {

    // True if constant curvature is enabled
    private boolean constantCurvatureEnabled = false;

    // Open loop control
    private DifferentialVoltages openLoopGoal = new DifferentialVoltages();

    // Control loops
    private Controller distanceController;
    private Controller rotationController;

    // Timer for actions
    private Timer actionTimer = new Timer();

    // Front side
    private Chassis.Side frontSide = Chassis.Side.kFront;

    // Max speed percent
    private double maxSpeedPercent = 1.0;

    // Localization
    private DifferentialDriveOdometry localizer;

    /**
     * Create a new TankDriveTrain that can only be controlled in open-loop mode
     */
    public TankDriveTrain() {
        this(null, null);
    }

    /**
     * Create a new TankDriveTrain
     * 
     * @param distanceController Controller for distance control
     * @param rotationController Controller for rotation control
     */
    public TankDriveTrain(Controller distanceController, Controller rotationController) {

        // Set controllers
        this.distanceController = distanceController;
        this.rotationController = rotationController;

        // Set up the localizer
        localizer = new DifferentialDriveOdometry(new Rotation2d());
    }

    @Override
    protected void handleOpenLoopControl(StateMetadata<State> meta) {
        if (meta.isFirstRun()) {
            logger.log("Switched to Open-Loop control");
        }

        // As long as the goal is non-null, write out to the motors
        if (openLoopGoal != null) {
            handleVoltage(openLoopGoal.getLeftVolts() * maxSpeedPercent,
                    openLoopGoal.getRightVolts() * maxSpeedPercent);
        } else {
            logger.log("An Open-Loop goal of NULL was passed to TankDriveTrain. Failed to write to motors!",
                    Level.kWarning);
        }
    }

    @Override
    protected void handleAutonomousRotation(StateMetadata<State> meta, Rotation2d goalHeading, Rotation2d epsilon) {

        if (meta.isFirstRun()) {

            // Handle this being an open-loop-only drivetrain
            if (rotationController == null || distanceController == null) {
                logger.log(
                        "This drivetrain does not support autonomous control. Please pass controllers into its constructor",
                        Level.kWarning);
                // Switch to open loop control
                setOpenLoop(new DifferentialVoltages());
                return;
            }

            logger.log("Switched to rotation control");
            logger.log(String.format("Turning to %s with epsilon of %s", goalHeading.toString(), epsilon.toString()));

            // Reset the controller
            rotationController.reset();

            // Configure the controller
            rotationController.setEpsilon(epsilon.getDegrees());
            rotationController.setReference(0);

            // Reset and start the action timer
            actionTimer.reset();
            actionTimer.start();
        }

        // Get the current heading
        Rotation2d currentHeading = getPose().getRotation();

        // Determine the error from the goal
        double error = RotationMath.getAngularErrorDegrees(currentHeading, goalHeading);

        // Calculate the needed output
        double output = rotationController.calculate(error) * RR_HAL.MAXIMUM_BUS_VOLTAGE * maxSpeedPercent;

        // Write voltage outputs
        handleVoltage(output, -output);

        // Handle reaching the goal heading
        if (rotationController.atReference()) {

            // Reset the controller
            rotationController.reset();

            // Stop the motors
            handleVoltage(0, 0);
            actionTimer.stop();

            // Switch to open loop control
            setOpenLoop(new DifferentialVoltages());

            // Log success
            logger.log(String.format("Reached heading goal after %.2f seconds", actionTimer.get()));
        }

    }

    @Override
    protected void handleDrivingToPose(StateMetadata<State> meta, Translation2d goalPose, Translation2d epsilon) {

        if (meta.isFirstRun()) {

            // Handle this being an open-loop-only drivetrain
            if (rotationController == null || distanceController == null) {
                logger.log(
                        "This drivetrain does not support autonomous control. Please pass controllers into its constructor",
                        Level.kWarning);
                // Switch to open loop control
                setOpenLoop(new DifferentialVoltages());
                return;
            }

            logger.log("Switched to pose control");
            logger.log(String.format("Driving to pose: %s", goalPose));

            // Reset the controllers
            rotationController.reset();
            distanceController.reset();

            // Configure the controllers
            rotationController.setEpsilon(1.0);
            rotationController.setReference(0.0);
            distanceController.setEpsilon((epsilon.getX() + epsilon.getY()) / 2);
            distanceController.setReference(0.0);

            // Reset and start the action timer
            actionTimer.reset();
            actionTimer.start();
        }

        // Get the robot's current pose
        Pose2d currentPose = getPose();

        // Calculate positional error
        Translation2d error = goalPose.minus(currentPose.getTranslation());

        // Get the hypot to determine scalar distance
        double distanceError = currentPose.getTranslation().getDistance(goalPose) * -1;
        // Math.sqrt(Math.pow((goalPose.getX() - currentPose.getTranslation().getX()), 2)
        //         + Math.pow((goalPose.getY() - currentPose.getTranslation().getY()), 2));

        // Calculate clockwise-positive rotational error
        Rotation2d angularError = Rotation2d.fromDegrees(Math.toDegrees(Math.atan2(error.getY(), error.getX())) * -1);

        // Calculate speed multiplier based on distance from target.
        // This lets the robot curve towards the target, instead of snapping to it.
        // This is a trick I learned from a programmer at 1114. It provides really
        // smooth outputs
        // https://bitbucket.org/kaleb_dodd/simbot2019public/src/abc56f5220b5c94bca216f86e3b6b5757d0ffeff/src/main/java/frc/subsystems/Drive.java#lines-337
        double speedMul = ((-1 * (Math.min(Math.abs(angularError.getDegrees()), 90.0)) / 90.0) + 1);

        // Calculate needed throttle
        double throttleOutput = distanceController.calculate(distanceError);

        // Restrict throttle output
        throttleOutput *= speedMul;

        // Calculate rotation PIF
        double turnOutput = rotationController.calculate(angularError.getDegrees());

        // Calculate motor outputs
        DifferentialVoltages voltages = DifferentialVoltages.fromThrottleAndSteering(throttleOutput, turnOutput);

        // logger.log(voltages.toString());
        logger.log(new DifferentialVoltages(throttleOutput, turnOutput).toString());

        // Write output frame
        handleVoltage(voltages.getLeftVolts(), voltages.getRightVolts());

        // If the robot is at its goal, we are done
        if (RobotMath.epsilonEquals(currentPose.getTranslation(), goalPose, epsilon)) {
            // Reset the controllers
            rotationController.reset();
            distanceController.reset();

            // Stop the motors
            handleVoltage(0, 0);
            actionTimer.stop();

            // Switch to open loop control
            setOpenLoop(new DifferentialVoltages());

            // Log success
            logger.log(String.format("Reached pose goal after %.2f seconds", actionTimer.get()));
        }

    }

    /**
     * TO BE OVERRIDDEN BY THE USER. Send voltage to the motors
     * 
     * @param leftVolts  Left side voltage
     * @param rightVolts Right side voltage
     */
    protected abstract void handleVoltage(double leftVolts, double rightVolts);

    /**
     * TO BE OVERRIDDEN BY THE USER. Reset the encoders to read 0 rotations each
     */
    protected abstract void resetEncoders();

    /**
     * TO BE OVERRIDDEN BY THE USER. Set if the motor outputs should be inverted
     * 
     * @param motorsInverted Should invert motor outputs
     */
    protected abstract void setMotorsInverted(boolean motorsInverted);

    /**
     * TO BE OVERRIDDEN BY THE USER. Set if the encoder inputs should be inverted
     * 
     * @param encodersInverted Should invert encoder inputs
     */
    protected abstract void setEncodersInverted(boolean encodersInverted);

    @Override
    public void periodic() {
        // Run super code
        super.periodic();

        // Update localization
        localizer.update(getCurrentHeading(), getLeftMeters(), getRightMeters());
    }

    @Override
    public void resetPose(Pose2d pose) {
        logger.log(String.format("Resetting robot pose to: %s", pose.toString()));
        localizer.resetPosition(pose, getCurrentHeading());
    }

    @Override
    public void setFrontSide(Side side) {
        // Handle impossible sides
        if (side.equals(Side.kLeft) || side.equals(Side.kRight)) {
            throw new RuntimeException("setFrontSide cannot be kLeft or kRight for a tank-drive system");
        }

        // If this is the same as the current side, do nothing
        if (side.equals(frontSide)) {
            return;
        }

        logger.log(String.format("Setting front side to: %s", side.toString()));

        // Get the current pose
        Pose2d currentPose = getPose();

        // Build a new pose, with a flipped angle
        Pose2d newPose = new Pose2d(currentPose.getTranslation(),
                currentPose.getRotation().minus(Rotation2d.fromDegrees(180)));

        // Reset the localizer
        resetPose(newPose);

        // Reset the encoders
        resetEncoders();

        // Flip everything
        setMotorsInverted(side.equals(Side.kBack));
        setEncodersInverted(side.equals(Side.kBack));

        // Set the active side tracker
        frontSide = side;

    }

    /**
     * Handle inputs from a human operator
     * 
     * @param throttlePercent Throttle percentage
     * @param steeringPercent Steering percentage
     */
    public void handleDriverInputs(double throttlePercent, double steeringPercent) {
        // Handle drive mode
        if (constantCurvatureEnabled) {
            setOpenLoop(DifferentialDriveMath.computeConstantCurvatureOutputs(throttlePercent, steeringPercent)
                    .normalize());
        } else {
            setOpenLoop(DifferentialDriveMath.computeSemiConstantCurvatureOutputs(throttlePercent, steeringPercent)
                    .normalize());
        }
    }

    /**
     * Set an open-loop output
     * 
     * @param leftVolts  Left side voltage
     * @param rightVolts Right side voltage
     */
    public void setOpenLoop(double leftVolts, double rightVolts) {
        setOpenLoop(new DifferentialVoltages(leftVolts, rightVolts));
    }

    /**
     * Set an open-loop output
     * 
     * @param voltages Voltages
     */
    public void setOpenLoop(DifferentialVoltages voltages) {
        openLoopGoal = voltages;
        super.stateMachine.setState(State.kOpenLoopControl);
    }

    /**
     * Set if constant-curvature control should be used
     * 
     * @param enabled Should use constant-curvature control?
     */
    public void enableConstantCurvature(boolean enabled) {
        this.constantCurvatureEnabled = enabled;
    }

    @Override
    public void setMaxSpeedPercent(double maxSpeedPercent) {
        this.maxSpeedPercent = maxSpeedPercent;
    }

    @Override
    public Pose2d getPose() {
        return localizer.getPoseMeters();
    }

    @Override
    public void stop() {
        super.stop();

        setOpenLoop(new DifferentialVoltages());
    }

    @Override
    public void reset() {
        super.reset();

        // Reset options
        constantCurvatureEnabled = false;
        frontSide = Chassis.Side.kFront;
        maxSpeedPercent = 1.0;

        // Reset controllers
        if (rotationController != null) {
            rotationController.reset();
        }
        if (distanceController != null) {
            distanceController.reset();
        }
    }
}