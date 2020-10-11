package io.github.frc5024.lib5k.examples.drivebase_simulation.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import io.github.frc5024.common_drive.DriveTrainBase;
import io.github.frc5024.common_drive.queue.DriveTrainOutput;
import io.github.frc5024.common_drive.queue.DriveTrainSensors;
import io.github.frc5024.common_drive.types.MotorMode;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.examples.drivebase_simulation.RobotConfig;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.EncoderSimulation;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.ISimGyro;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREMotorFactory;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.kauai.gyroscopes.NavX;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;

public class DriveTrain extends DriveTrainBase {
    private static DriveTrain instance;

    // Motors
    private ExtendedTalonSRX frontLeftMotor;
    private ExtendedTalonSRX rearLeftMotor;
    private ExtendedTalonSRX frontRightMotor;
    private ExtendedTalonSRX rearRightMotor;

    // Sensors
    private EncoderSimulation leftEncoder;
    private EncoderSimulation rightEncoder;
    private ISimGyro gyroscope;

    // Simulation
    private NetworkTableInstance ntInst;
    private NetworkTableEntry ntX;
    private NetworkTableEntry ntY;
    private NetworkTableEntry ntTheta;

    private DriveTrain() {
        // Set up DriveTrain with out config
        super(RobotConfig.DRIVETRAIN_CONFIG);

        // Set up motors
        frontLeftMotor = CTREMotorFactory.createTalonSRX(RobotConfig.DRIVETRAIN_FRONT_LEFT_ID,
                RobotConfig.DRIVETRAIN_MOTOR_CONFIG);
        rearLeftMotor = frontLeftMotor.makeSlave(RobotConfig.DRIVETRAIN_REAR_LEFT_ID);
        frontRightMotor = CTREMotorFactory.createTalonSRX(RobotConfig.DRIVETRAIN_FRONT_RIGHT_ID,
                RobotConfig.DRIVETRAIN_MOTOR_CONFIG);
        rearRightMotor = frontRightMotor.makeSlave(RobotConfig.DRIVETRAIN_REAR_RIGHT_ID);

        // Set inversions on motors
        frontLeftMotor.setInverted(false);
        rearLeftMotor.setInverted(false);
        frontRightMotor.setInverted(true);
        rearRightMotor.setInverted(true);

        // Set the sensor phases
        frontLeftMotor.setSensorPhase(false);
        frontRightMotor.setSensorPhase(false);

        // Disable safety
        frontLeftMotor.setSafetyEnabled(false);
        frontRightMotor.setSafetyEnabled(false);

        // Set up encoders
        leftEncoder = (EncoderSimulation) rearLeftMotor.getCommonEncoder(RobotConfig.DRIVETRAIN_ENCODER_TPR);
        rightEncoder = (EncoderSimulation) rearRightMotor.getCommonEncoder(RobotConfig.DRIVETRAIN_ENCODER_TPR);

        // Set up encoder simulation
        leftEncoder.initSimulationDevice(frontLeftMotor, RobotConfig.DRIVETRAIN_CONFIG.lowGearRatio,
                ((DCBrushedMotor) DCBrushedMotor.getCIM(1)).getFreeSpeedRPM(),
                RobotConfig.DRIVETRAIN_CONFIG.defaultRampSeconds);
        rightEncoder.initSimulationDevice(frontRightMotor, RobotConfig.DRIVETRAIN_CONFIG.lowGearRatio,
                ((DCBrushedMotor) DCBrushedMotor.getCIM(1)).getFreeSpeedRPM(),
                RobotConfig.DRIVETRAIN_CONFIG.defaultRampSeconds);

        // Set up gyroscope
        gyroscope = NavX.getInstance();

        // Enable gyro simulation
        gyroscope.initDrivebaseSimulation(this);

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
    public DriveTrainSensors readInputs() {
        DriveTrainSensors output = new DriveTrainSensors();

        // Update encoders
        leftEncoder.update();
        rightEncoder.update();

        // Timestamp
        output.timestamp_ms = FPGAClock.getFPGAMilliseconds();

        // Gyroscope
        output.rotation = gyroscope.getRotation();
        output.angle = gyroscope.getAngle();
        output.angularRate = gyroscope.getRate();

        // Encoders
        output.leftEncoderMetres = getLeftMeters();
        output.rightEncoderMetres = getRightMeters();

        return output;
    }

    @Override
    public void consumeOutputs(DriveTrainOutput output) {

        // Set motor voltages
        frontLeftMotor.setVoltage(output.leftVoltage);
        frontRightMotor.setVoltage(output.rightVoltage);

        // Handle motor settings
        if (output.invertSensorPhase.write) {
            frontLeftMotor.setSensorPhase(output.invertSensorPhase.value);
            frontRightMotor.setSensorPhase(output.invertSensorPhase.value);
            output.invertSensorPhase.consume();
        }
        if (output.motorMode.write) {
            if (output.motorMode.value == MotorMode.kCoast) {
                frontLeftMotor.setNeutralMode(NeutralMode.Coast);
                frontRightMotor.setNeutralMode(NeutralMode.Coast);
            } else {
                frontLeftMotor.setNeutralMode(NeutralMode.Brake);
                frontRightMotor.setNeutralMode(NeutralMode.Brake);
            }
            output.motorMode.consume();
        }
        if (output.motorRamp.write) {
            frontLeftMotor.configOpenloopRamp(output.motorRamp.value);
            frontRightMotor.configOpenloopRamp(output.motorRamp.value);
            output.motorRamp.consume();
        }

    }

    @Override
    public double getRightMeters() {
        return rightEncoder.getPosition() * RobotConfig.DRIVETRAIN_CONFIG.getWheelCircumference();
    }

    @Override
    public double getLeftMeters() {
        return leftEncoder.getPosition() * RobotConfig.DRIVETRAIN_CONFIG.getWheelCircumference();
    }

    @Override
    public void customPeriodic() {

        // Publish pose to NT
        Pose2d pose = getPose();
        ntX.setDouble(pose.getTranslation().getX());
        ntY.setDouble(pose.getTranslation().getY());
        ntTheta.setDouble(pose.getRotation().getDegrees());

    }

}