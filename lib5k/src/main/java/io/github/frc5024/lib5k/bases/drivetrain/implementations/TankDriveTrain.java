package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import io.github.frc5024.lib5k.bases.drivetrain.AbstractDriveTrain;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.math.DifferentialDriveMath;
import io.github.frc5024.lib5k.utils.types.DifferentialVoltages;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

public abstract class TankDriveTrain extends AbstractDriveTrain {

    // True if constant curvature is enabled
    private boolean constantCurvatureEnabled = false;

    // Open loop control
    private DifferentialVoltages openLoopGoal = null;

    public TankDriveTrain() {
        
    }

    @Override
    protected void handleOpenLoopControl(StateMetadata<State> meta) {
        if (meta.isFirstRun()) {
            logger.log("Switched to Open-Loop control");
        }

        // As long as the goal is non-null, write out to the motors
        if (openLoopGoal != null) {
            handleVoltage(openLoopGoal.getLeftVolts(), openLoopGoal.getRightVolts());
        } else {
            logger.log("An Open-Loop goal of NULL was passed to TankDriveTrain. Failed to write to motors!",
                    Level.kWarning);
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
    }
}