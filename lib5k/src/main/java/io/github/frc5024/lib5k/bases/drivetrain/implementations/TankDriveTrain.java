package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import io.github.frc5024.common_drive.types.ChassisSide;
import io.github.frc5024.lib5k.bases.drivetrain.AbstractDriveTrain;
import io.github.frc5024.lib5k.bases.drivetrain.Chassis;
import io.github.frc5024.lib5k.control_loops.base.Controller;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.math.DifferentialDriveMath;
import io.github.frc5024.lib5k.math.RotationMath;
import io.github.frc5024.lib5k.utils.RobotMath;
import io.github.frc5024.lib5k.utils.types.DifferentialVoltages;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

public abstract class TankDriveTrain extends AbstractDriveTrain {

    // True if constant curvature is enabled
    private boolean constantCurvatureEnabled = false;

    // Open loop control
    private DifferentialVoltages openLoopGoal = null;

    // Control loops
    private Controller distanceController;
    private Controller rotationController;

    // Timer for actions
    private Timer actionTimer = new Timer();

    // Front side
    private Chassis.Side frontSide = Chassis.Side.kFront;

    // Max speed percent
    private double maxSpeedPercent = 1.0;

    public TankDriveTrain(Controller distanceController, Controller rotationController) {
        this.distanceController = distanceController;
        this.rotationController = rotationController;
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
            logger.log("Switched to rotation control");
            logger.log(String.format("Turning to %s with epsilon of %s", goalHeading.toString(), epsilon.toString()));

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

    protected abstract void handleVoltage(double leftVolts, double rightVolts);

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

    public void setOpenLoop(double leftVolts, double rightVolts) {
        setOpenLoop(new DifferentialVoltages(leftVolts, rightVolts));
    }

    public void setOpenLoop(DifferentialVoltages voltages) {
        openLoopGoal = voltages;
        super.stateMachine.setState(State.kOpenLoopControl);
    }

    public void enableConstantCurvature(boolean enabled) {
        this.constantCurvatureEnabled = enabled;
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void reset() {
        super.reset();

        // Reset options
        constantCurvatureEnabled = false;
        frontSide = Chassis.Side.kFront;
        maxSpeedPercent = 1.0;
    }
}