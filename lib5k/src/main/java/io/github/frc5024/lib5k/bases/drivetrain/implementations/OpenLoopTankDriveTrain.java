package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.utils.types.DifferentialVoltages;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * OpenLoopTankDriveTrain is a TankDriveTrain implementation that can only be
 * controlled manually. No autonomous control is available
 */
public abstract class OpenLoopTankDriveTrain extends TankDriveTrain {

    /**
     * Create a OpenLoopTankDriveTrain
     */
    public OpenLoopTankDriveTrain() {
        super();
    }

    @Override
    protected void handleAutonomousRotation(StateMetadata<State> meta, Rotation2d goalHeading, Rotation2d epsilon) {
        logger.log("OpenLoopTankDriveTrain does not support autonomous movement", Level.kWarning);
        setOpenLoop(new DifferentialVoltages());
    }

    @Override
    protected void handleDrivingToPose(StateMetadata<State> meta, Translation2d goalPose, Translation2d epsilon) {
        logger.log("OpenLoopTankDriveTrain does not support autonomous movement", Level.kWarning);
        setOpenLoop(new DifferentialVoltages());
    }

    @Override
    public void setGoalHeading(Rotation2d heading, Rotation2d epsilon) {
        throw new IllegalStateException("OpenLoopTankDriveTrain does not support autonomous movement");
    }

    @Override
    public void setGoalPose(Pose2d pose, Translation2d epsilon) {
        throw new IllegalStateException("OpenLoopTankDriveTrain does not support autonomous movement");
    }

    @Override
    public void setGoalPose(Translation2d pose, Translation2d epsilon) {
        throw new IllegalStateException("OpenLoopTankDriveTrain does not support autonomous movement");
    }

}