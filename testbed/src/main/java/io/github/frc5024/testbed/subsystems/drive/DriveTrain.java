package io.github.frc5024.testbed.subsystems.drive;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.implementations.TankDriveTrain;
import io.github.frc5024.lib5k.control_loops.base.Controller;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.testbed.ConfigLoader;

public class DriveTrain extends TankDriveTrain {

    // Motors
    private ExtendedTalonSRX leftFrontMotor;
    private ExtendedTalonSRX leftRearMotor;
    private ExtendedTalonSRX rightFrontMotor;
    private ExtendedTalonSRX rightRearMotor;

    // 

    public DriveTrain() {
        super(ConfigLoader.getConfig().drivetrain.throttleController.build(),
                ConfigLoader.getConfig().drivetrain.steeringController.build());
                
    }

    @Override
    public double getLeftMeters() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getRightMeters() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getWidthMeters() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void handleVoltage(double leftVolts, double rightVolts) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void resetEncoders() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setMotorsInverted(boolean motorsInverted) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setEncodersInverted(boolean encodersInverted) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleGearShift(Gear gear) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void enableBrakes(boolean enabled) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Rotation2d getCurrentHeading() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void runIteration() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRampRate(double rampTimeSeconds) {
        // TODO Auto-generated method stub

    }

}