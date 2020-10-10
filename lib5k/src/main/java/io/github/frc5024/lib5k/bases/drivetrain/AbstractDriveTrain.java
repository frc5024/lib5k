package io.github.frc5024.lib5k.bases.drivetrain;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.hardware.common.drivebase.IDifferentialDrivebase;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.interfaces.SafeSystem;
import io.github.frc5024.lib5k.utils.types.DifferentialVoltages;

public abstract class AbstractDriveTrain extends SubsystemBase implements IDifferentialDrivebase, SafeSystem {

    // Logging
    private RobotLogger logger = RobotLogger.getInstance();

    // True if constant curvature is enabled
    private boolean constantCurvatureEnabled = false;

    public AbstractDriveTrain() {

    }

    protected abstract void handleVoltage(double leftVolts, double rightVolts);

    protected abstract void handleGearShift(Gear gear);

    protected abstract Rotation2d getCurrentHeading();

    protected abstract void runIteration();

    public abstract void setRampRate(double rampTimeSeconds);

    public void enableConstantCurvature(boolean enabled) {
        this.constantCurvatureEnabled = enabled;
    }

    public void handleDriverInputs(double throttlePercent, double steeringPercent) {

    }

    public void setOpenLoop(double leftVolts, double rightVolts) {
        setOpenLoop(new DifferentialVoltages(leftVolts, rightVolts));
    }

    public void setOpenLoop(DifferentialVoltages voltages) {

    }

    public void setGoalPose(Pose2d pose) {
        setGoalPose(pose.getTranslation());
    }

    public void setGoalPose(Translation2d pose) {
        
    }
    
    public void setGoalHeading(Rotation2d heading) {

    }
    
    public void setInverted(boolean inverted) {

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

        // Reset options
        constantCurvatureEnabled = false;


    }

}