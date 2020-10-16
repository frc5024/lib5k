package io.github.frc5024.lib5k.examples.autonomous_path_following.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.implementations.TankDriveTrain;
import io.github.frc5024.lib5k.examples.autonomous_path_following.RobotConfig;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;

public class DriveTrain extends TankDriveTrain {
    private static DriveTrain instance = null;

    // Motors
    private ExtendedTalonSRX leftFrontMotor;
    private ExtendedTalonSRX leftRearMotor;
    private ExtendedTalonSRX rightFrontMotor;
    private ExtendedTalonSRX rightRearMotor;

    // Encoders
    private CommonEncoder leftEncoder;
    private CommonEncoder rightEncoder;

    // Gyroscope
    private NavX gyroscope;

    // Motor and encoder inversion multiplier
    private double motorInversionMultiplier = 1.0;
    private double encoderInversionMultiplier = 1.0;

    private DriveTrain() {
        super(RobotConfig.DRIVETRAIN_DISTANCE_CONTROLLER, RobotConfig.DRIVETRAIN_ROTATION_CONTROLLER);

        // Set up motors
        leftFrontMotor = new ExtendedTalonSRX(RobotConfig.DRIVETRAIN_FRONT_LEFT_ID);
        leftRearMotor = leftFrontMotor.makeSlave(RobotConfig.DRIVETRAIN_REAR_LEFT_ID);
        rightFrontMotor = new ExtendedTalonSRX(RobotConfig.DRIVETRAIN_FRONT_RIGHT_ID);
        rightRearMotor = rightFrontMotor.makeSlave(RobotConfig.DRIVETRAIN_REAR_RIGHT_ID);

        // Set inversions on motors
        leftFrontMotor.setInverted(false);
        leftRearMotor.setInverted(false);
        rightFrontMotor.setInverted(true);
        rightRearMotor.setInverted(true);

        // Set the sensor phases
        leftFrontMotor.setSensorPhase(false);
        rightFrontMotor.setSensorPhase(false);

        // Set up encoders
        leftEncoder = leftRearMotor.getCommonEncoder(RobotConfig.DRIVETRAIN_ENCODER_TPR);
        rightEncoder = rightRearMotor.getCommonEncoder(RobotConfig.DRIVETRAIN_ENCODER_TPR);

        // Set up gyroscope
        gyroscope = NavX.getInstance();
    }

    public static DriveTrain getInstance() {
        if (instance == null) {
            instance = new DriveTrain();
        }
        return instance;
    }

    @Override
    public double getLeftMeters() {
        return leftEncoder.getPosition() * (2 * Math.PI * RobotConfig.DRIVETRAIN_WHEEL_RADIUS)
                * encoderInversionMultiplier;
    }

    @Override
    public double getRightMeters() {
        return rightEncoder.getPosition() * (2 * Math.PI * RobotConfig.DRIVETRAIN_WHEEL_RADIUS)
                * encoderInversionMultiplier;
    }

    @Override
    public double getWidthMeters() {
        return RobotConfig.ROBOT_WIDTH;
    }

    @Override
    protected void handleVoltage(double leftVolts, double rightVolts) {
        leftFrontMotor.setVoltage(leftVolts * motorInversionMultiplier);
        rightFrontMotor.setVoltage(rightVolts * motorInversionMultiplier);
    }

    @Override
    protected void resetEncoders() {
    }

    @Override
    protected void setMotorsInverted(boolean motorsInverted) {
        this.motorInversionMultiplier = (motorsInverted) ? -1.0 : 1.0;
    }

    @Override
    protected void setEncodersInverted(boolean encodersInverted) {
        this.encoderInversionMultiplier = (encodersInverted) ? -1.0 : 1.0;
    }

    @Override
    protected void handleGearShift(Gear gear) {
        // Do nothing here since we dont have a gear shifter
    }

    @Override
    protected void enableBrakes(boolean enabled) {
        if (enabled) {
            leftFrontMotor.setNeutralMode(NeutralMode.Brake);
            rightFrontMotor.setNeutralMode(NeutralMode.Brake);
        } else {
            leftFrontMotor.setNeutralMode(NeutralMode.Coast);
            rightFrontMotor.setNeutralMode(NeutralMode.Coast);
        }
    }

    @Override
    protected Rotation2d getCurrentHeading() {
        return gyroscope.getRotation();
    }

    @Override
    protected void runIteration() {
    }

    @Override
    public void setRampRate(double rampTimeSeconds) {
        leftFrontMotor.configOpenloopRamp(rampTimeSeconds);
        rightFrontMotor.configOpenloopRamp(rampTimeSeconds);
    }

}