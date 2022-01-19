package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;

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

    // Front side
    private Chassis.Side frontSide = Chassis.Side.kFront;

    // Max speed percent
    protected double maxSpeedPercent = 1.0;

    // Localization
    private DifferentialDriveOdometry localizer;

    // Tracker for last pose
    private Pose2d lastPose = new Pose2d();

    /**
     * Create a new TankDriveTrain
     */
    public TankDriveTrain() {
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
        lastPose = getPose();
        localizer.update(getCurrentHeading(), getLeftMeters(), getRightMeters());

    }

    @Override
    public void resetPose(Pose2d pose) {
        logger.log(String.format("Resetting robot pose to: %s", pose.toString()));
        localizer.resetPosition(pose, getCurrentHeading());
        // Reset the encoders
        resetEncoders();
        lastPose = pose;
    }

    @Override
    public Translation2d getVelocity() {
        return getPose().getTranslation().minus(lastPose.getTranslation());
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

        // TODO: I dont think this is needed
        // // Get the current pose
        // Pose2d currentPose = getPose();

        // // Build a new pose, with a flipped angle
        // Pose2d newPose = new Pose2d(currentPose.getTranslation(),
        // currentPose.getRotation().minus(Rotation2d.fromDegrees(180)));

        // // Reset the localizer
        // resetPose(newPose);

        // // Reset the encoders
        // resetEncoders();

        // // Flip everything
        // setMotorsInverted(side.equals(Side.kBack));
        // setEncodersInverted(side.equals(Side.kBack));

        // Set the active side tracker
        frontSide = side;

    }

    /**
     * Get which side is the front
     * 
     * @return Front side
     */
    public Chassis.Side getFrontSide() {
        return frontSide;
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
    }

    /**
     * Calculates a corrective factor for throttle values, as per:
     * https://bitbucket.org/kaleb_dodd/simbot2019public/src/abc56f5220b5c94bca216f86e3b6b5757d0ffeff/src/main/java/frc/subsystems/Drive.java#lines-337
     * 
     * @param angularError Error from target heading
     * @return Throttle correction
     */
    public static double calculateThrottleCorrectionFactor(Rotation2d angularError) {

        double distanceFromTargetHeading = Math.abs(angularError.getDegrees());
        if (distanceFromTargetHeading > 90) { // prevents the output from being reversed in the next calculation
            distanceFromTargetHeading = 90;
        }

        return ((-1 * distanceFromTargetHeading) / 90.0) + 1;
    }
}
