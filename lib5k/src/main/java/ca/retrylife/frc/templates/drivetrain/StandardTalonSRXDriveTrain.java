package ca.retrylife.frc.templates.drivetrain;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import io.github.frc5024.common_drive.DriveTrainBase;
import io.github.frc5024.common_drive.DriveTrainConfig;
import io.github.frc5024.common_drive.controller.PDFGains;
import io.github.frc5024.common_drive.controller.PIFGains;
import io.github.frc5024.common_drive.queue.DriveTrainOutput;
import io.github.frc5024.common_drive.queue.DriveTrainSensors;
import io.github.frc5024.common_drive.types.MotorMode;
import io.github.frc5024.common_drive.types.ShifterType;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.IGyroscope;
import io.github.frc5024.lib5k.hardware.ctre.motors.ExtendedTalonSRX;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.FPGAClock;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

/**
 * A pre-made template class for the standard TalonSRX-based drivetrain used on
 * every RaiderRobotics robot
 */
public abstract class StandardTalonSRXDriveTrain extends DriveTrainBase {

    // Logger
    private RobotLogger logger = RobotLogger.getInstance();

    // Speed controllers
    private ExtendedTalonSRX leftSideSpeedControllers;
    private ExtendedTalonSRX rightSideSpeedControllers;

    // Sensors
    private CommonEncoder leftSideEncoder;
    private CommonEncoder rightSideEncoder;
    private IGyroscope gyroscope;

    // Wheel circumference
    private double wheelCircumference;

    /**
     * Create a StandardTalonSRXDriveTrain
     * 
     * @param leftMasterController    The left side motor controller that has an
     *                                encoder attached
     * @param extraLeftSideMotors     All other left side motor controllers
     * @param leftSideInverted        Is the left side inverted?
     * @param leftSensorInverted      Is the left encoder inverted?
     * @param rightMasterController   The right side motor controller that has an
     *                                encoder attached
     * @param extraRightSideMotors    All other right side motor controllers
     * @param rightSideInverted       Is the right side inverted?
     * @param rightSensorInverted     Is the right encoder inverted?
     * @param encoderTicksPerRotation Number of encoder ticks measured per single
     *                                rotation of the wheels
     * @param gyroscope               The robot gyroscope
     * @param gyroInverted            If the gyro's left and right are inverted.
     *                                Right should be positive
     * @param robotRadiusMeters       The distance from the centre of the robot to
     *                                the corner of its bumpers in meters
     * @param robotWidthMeters        The robot's width in meters
     * @param wheelRadiusMeters       The radius of the largest wheel in meters
     * @param gearRatio               The gear ratio between the motors and the
     *                                wheels. This is usually 8.45/1
     * @param rotationControlGains    PID+F gains for robot rotation
     * @param speedControlGains       PID+F gains for robot speed
     */
    public StandardTalonSRXDriveTrain(ExtendedTalonSRX leftMasterController, ExtendedTalonSRX[] extraLeftSideMotors,
            boolean leftSideInverted, boolean leftSensorInverted, ExtendedTalonSRX rightMasterController,
            ExtendedTalonSRX[] extraRightSideMotors, boolean rightSideInverted, boolean rightSensorInverted,
            int encoderTicksPerRotation, IGyroscope gyroscope, boolean gyroInverted, double robotRadiusMeters,
            double robotWidthMeters, double wheelRadiusMeters, double gearRatio, PIFGains rotationControlGains,
            PDFGains speedControlGains) {
        this(leftMasterController, extraLeftSideMotors, leftSideInverted, leftSensorInverted, rightMasterController,
                extraRightSideMotors, rightSideInverted, rightSensorInverted, encoderTicksPerRotation, gyroscope,
                gyroInverted, robotRadiusMeters, robotWidthMeters, wheelRadiusMeters, gearRatio, rotationControlGains,
                speedControlGains, 0.12, 0.18);
    }

    /**
     * Create a StandardTalonSRXDriveTrain
     * 
     * @param leftMasterController    The left side motor controller that has an
     *                                encoder attached
     * @param extraLeftSideMotors     All other left side motor controllers
     * @param leftSideInverted        Is the left side inverted?
     * @param leftSensorInverted      Is the left encoder inverted?
     * @param rightMasterController   The right side motor controller that has an
     *                                encoder attached
     * @param extraRightSideMotors    All other right side motor controllers
     * @param rightSideInverted       Is the right side inverted?
     * @param rightSensorInverted     Is the right encoder inverted?
     * @param encoderTicksPerRotation Number of encoder ticks measured per single
     *                                rotation of the wheels
     * @param gyroscope               The robot gyroscope
     * @param gyroInverted            If the gyro's left and right are inverted.
     *                                Right should be positive
     * @param robotRadiusMeters       The distance from the centre of the robot to
     *                                the corner of its bumpers in meters
     * @param robotWidthMeters        The robot's width in meters
     * @param wheelRadiusMeters       The radius of the largest wheel in meters
     * @param gearRatio               The gear ratio between the motors and the
     *                                wheels. This is usually 8.45/1
     * @param rotationControlGains    PID+F gains for robot rotation
     * @param speedControlGains       PID+F gains for robot speed
     * @param defaultRampRateSeconds  The number of seconds it should take the robot
     *                                to reach max speed (raising this will reduce a
     *                                tippy robot)
     * @param pathingRampRateSeconds  The number of seconds it should take the robot
     *                                to reach max speed during autonomous. This is
     *                                generally set higher than the default to keep
     *                                things smooth, but changing this will require
     *                                retuning PID gains
     */
    public StandardTalonSRXDriveTrain(ExtendedTalonSRX leftMasterController, ExtendedTalonSRX[] extraLeftSideMotors,
            boolean leftSideInverted, boolean leftSensorInverted, ExtendedTalonSRX rightMasterController,
            ExtendedTalonSRX[] extraRightSideMotors, boolean rightSideInverted, boolean rightSensorInverted,
            int encoderTicksPerRotation, IGyroscope gyroscope, boolean gyroInverted, double robotRadiusMeters,
            double robotWidthMeters, double wheelRadiusMeters, double gearRatio, PIFGains rotationControlGains,
            PDFGains speedControlGains, double defaultRampRateSeconds, double pathingRampRateSeconds) {

        // Create a config on the fly
        super(new DriveTrainConfig() {
            {
                // Shifting method to use
                this.shifterType = ShifterType.NO_SHIFTER;

                // Robot parameters
                this.robotRadius = robotRadiusMeters;
                this.wheelRadius = wheelRadiusMeters;
                this.robotWidth = robotWidthMeters;

                // Gearing
                this.highGearRatio = gearRatio;
                this.lowGearRatio = gearRatio;
                this.defaultHighGear = true;

                // Closed loop control
                this.turningGains = rotationControlGains;
                this.distanceGains = speedControlGains;

                // Ramp rates
                this.defaultRampSeconds = defaultRampRateSeconds;
                this.pathingRampSeconds = pathingRampRateSeconds;
            }
        });

        // Set up motor controllers
        logger.log("Collecting motor controllers into groups", Level.kDebug);
        this.leftSideSpeedControllers = leftMasterController;
        this.rightSideSpeedControllers = rightMasterController;

        // Set up master/slave relationships
        logger.log("Binding slave controllers to their masters", Level.kDebug);
        for (ExtendedTalonSRX extraMotor : extraRightSideMotors) {
            extraMotor.follow(leftMasterController);
        }
        for (ExtendedTalonSRX extraMotor : extraLeftSideMotors) {
            extraMotor.follow(rightMasterController);
        }

        // Set controller inversions
        logger.log("Configuring motor inversions", Level.kDebug);
        leftMasterController.setInverted(leftSideInverted);
        rightMasterController.setInverted(rightSideInverted);

        // Set up encoders
        logger.log("Getting handles on encoders", Level.kDebug);
        this.leftSideEncoder = leftMasterController.getCommonEncoder(encoderTicksPerRotation);
        this.rightSideEncoder = rightMasterController.getCommonEncoder(encoderTicksPerRotation);

        // Set sensor phases
        logger.log("Configuring encoders", Level.kDebug);
        this.leftSideEncoder.setPhaseInverted(leftSensorInverted);
        this.rightSideEncoder.setPhaseInverted(rightSensorInverted);

        // Set up gyroscope
        logger.log("Setting up gyroscope", Level.kDebug);
        this.gyroscope = gyroscope;
        this.gyroscope.setInverted(gyroInverted);

        // Set locals
        this.wheelCircumference = wheelRadiusMeters * 2 * Math.PI;

    }

    @Override
    public DriveTrainSensors readInputs() {
        DriveTrainSensors output = new DriveTrainSensors();

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
        this.leftSideSpeedControllers.setVoltage(output.leftVoltage);
        this.rightSideSpeedControllers.setVoltage(output.rightVoltage);

        // Set sensor phases
        output.invertSensorPhase.consume((newSensorPhase) -> {
            this.leftSideEncoder.setPhaseInverted(newSensorPhase);
            this.rightSideEncoder.setPhaseInverted(newSensorPhase);
        });

        // Set motor modes
        output.motorMode.consume((newMode) -> {
            this.leftSideSpeedControllers
                    .setNeutralMode((newMode == MotorMode.kCoast) ? NeutralMode.Coast : NeutralMode.Brake);
            this.rightSideSpeedControllers
                    .setNeutralMode((newMode == MotorMode.kCoast) ? NeutralMode.Coast : NeutralMode.Brake);
        });

        // Set ramp rates
        output.motorRamp.consume((newRampRate) -> {
            this.leftSideSpeedControllers.configOpenloopRamp(newRampRate);
            this.rightSideSpeedControllers.configOpenloopRamp(newRampRate);
        });
    }

    @Override
    public double getLeftMeters() {
        return leftSideEncoder.getPosition() * this.wheelCircumference;
    }

    @Override
    public double getRightMeters() {
        return rightSideEncoder.getPosition() * this.wheelCircumference;
    }

    /**
     * Get the robot's current rotation
     * 
     * @return Current rotation
     */
    public Rotation2d getRotation() {
        return getPose().getRotation();
    }

}