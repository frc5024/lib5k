package io.github.frc5024.lib5k.examples.drivebase_simulation.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import io.github.frc5024.common_drive.gearing.Gear;
import io.github.frc5024.lib5k.bases.drivetrain.implementations.DualPIDTankDriveTrain;
import io.github.frc5024.lib5k.examples.drivebase_simulation.RobotConfig;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;

public class DriveTrain extends DualPIDTankDriveTrain {
    private static DriveTrain instance = null;

    // Motors
    private ExtendedTalonSRX leftFrontMotor;
    private ExtendedTalonSRX leftRearMotor;
    private ExtendedTalonSRX rightFrontMotor;
    private ExtendedTalonSRX rightRearMotor;

    // Encoders
    private EncoderSimulation leftEncoder;
    private EncoderSimulation rightEncoder;

    // Gyroscope
    private NavX gyroscope;

    // Motor and encoder inversion multiplier
    private double motorInversionMultiplier = 1.0;
    private double encoderInversionMultiplier = 1.0;

    // Simulation
    private NetworkTableInstance ntInst;
    private NetworkTableEntry ntX;
    private NetworkTableEntry ntY;
    private NetworkTableEntry ntTheta;

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
        leftEncoder = (EncoderSimulation) leftRearMotor.getCommonEncoder(RobotConfig.DRIVETRAIN_ENCODER_TPR);
        rightEncoder = (EncoderSimulation) rightRearMotor.getCommonEncoder(RobotConfig.DRIVETRAIN_ENCODER_TPR);

        // Set up motor simulation
        if (RobotBase.isSimulation()) {
            leftEncoder.initSimulationDevice(leftFrontMotor, RobotConfig.DRIVETRAIN_GEAR_RATIO,
                    RobotConfig.DRIVETRAIN_GEARBOX_MOTOR_TYPE.getFreeSpeedRPM(),
                    RobotConfig.MAXIMUM_TIME_TO_ACCELERATE_SECONDS);
            rightEncoder.initSimulationDevice(rightFrontMotor, RobotConfig.DRIVETRAIN_GEAR_RATIO,
                    RobotConfig.DRIVETRAIN_GEARBOX_MOTOR_TYPE.getFreeSpeedRPM(),
                    RobotConfig.MAXIMUM_TIME_TO_ACCELERATE_SECONDS);
        }

        // Set up gyroscope
        gyroscope = NavX.getInstance();

        // Set up gyro simulation
        if (RobotBase.isSimulation()) {
            gyroscope.initDrivebaseSimulation(this);
        }

        // Set up networktables
        ntInst = NetworkTableInstance.getDefault();
        ntX = ntInst.getEntry("drivetrain_x");
        ntY = ntInst.getEntry("drivetrain_y");
        ntTheta = ntInst.getEntry("drivetrain_theta");
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
        // Publish pose to NT
        Pose2d pose = getPose();
        ntX.setDouble(pose.getTranslation().getX());
        ntY.setDouble(pose.getTranslation().getY());
        ntTheta.setDouble(pose.getRotation().getDegrees());
    }

    @Override
    public void setRampRate(double rampTimeSeconds) {
        leftFrontMotor.configOpenloopRamp(rampTimeSeconds);
        rightFrontMotor.configOpenloopRamp(rampTimeSeconds);
    }

}