package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import io.github.frc5024.lib5k.bases.drivetrain.AbstractDriveTrain;
import io.github.frc5024.lib5k.math.DifferentialDriveMath;
import io.github.frc5024.lib5k.utils.types.DifferentialVoltages;

public abstract class TankDriveTrain extends AbstractDriveTrain {

    // True if constant curvature is enabled
    private boolean constantCurvatureEnabled = false;

    // Open loop control
    private DifferentialVoltages openLoopGoal;

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