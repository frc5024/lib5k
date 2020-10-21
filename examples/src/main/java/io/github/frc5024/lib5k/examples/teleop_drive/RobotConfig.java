package io.github.frc5024.lib5k.examples.teleop_drive;

import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.util.Units;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;

public class RobotConfig {

    // Drivetrain CAN ids
    public static final int DRIVETRAIN_FRONT_LEFT_ID = 1;
    public static final int DRIVETRAIN_REAR_LEFT_ID = 2;
    public static final int DRIVETRAIN_FRONT_RIGHT_ID = 3;
    public static final int DRIVETRAIN_REAR_RIGHT_ID = 4;

    // Drivetrain sensor TPR
    public static final int DRIVETRAIN_ENCODER_TPR = 1400;

    // Robot sizing
    public static final double ROBOT_WIDTH = Units.inchesToMeters(26.0);
    public static final double DRIVETRAIN_WHEEL_RADIUS = Units.inchesToMeters(6.0) / 2.0;

    // Motor inversions
    public static final boolean FRONT_LEFT_MOTOR_INVERTED = false;
    public static final boolean REAR_LEFT_MOTOR_INVERTED = false;
    public static final boolean FRONT_RIGHT_MOTOR_INVERTED = true;
    public static final boolean REAR_RIGHT_MOTOR_INVERTED = true;

    // Sensor inversions
    public static final boolean LEFT_ENCODER_INVERTED = false;
    public static final boolean RIGHT_ENCODER_INVERTED = false;

    // Drivetrain motor type
    public static final DCBrushedMotor DRIVETRAIN_GEARBOX_MOTOR_TYPE = new DCBrushedMotor(DCMotor.getCIM(2));

    // Some characteristics about the drivetrain
    public static final double MAXIMUM_TIME_TO_ACCELERATE_SECONDS = 0.12;
    public static final double DRIVETRAIN_GEAR_RATIO = 8.45 / 1;
}