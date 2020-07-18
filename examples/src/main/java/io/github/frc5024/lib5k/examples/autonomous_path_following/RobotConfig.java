package io.github.frc5024.lib5k.examples.autonomous_path_following;

import edu.wpi.first.wpilibj.util.Units;
import io.github.frc5024.common_drive.DriveTrainConfig;
import io.github.frc5024.common_drive.controller.PDFGains;
import io.github.frc5024.common_drive.controller.PIFGains;
import io.github.frc5024.common_drive.types.ShifterType;
import io.github.frc5024.lib5k.hardware.ctre.motors.CTREConfig;

public class RobotConfig {

    // Config for drivetrain calculations
    public static DriveTrainConfig DRIVETRAIN_CONFIG = new DriveTrainConfig(){
        {
            // Don't use a gear shifter
            this.shifterType = ShifterType.NO_SHIFTER;

            // Run in a 20ms loop
            this.dt_ms = 20;

            // Set robot physical parameters
            this.robotRadius = Units.inchesToMeters(18.38);
            this.wheelRadius = Units.inchesToMeters(6.0) / 2.0;
            this.robotWidth = Units.inchesToMeters(26.0);

            // Set gearing 
            this.highGearRatio = 8.45 / 1;
            this.lowGearRatio = 8.45 / 1;
            this.defaultHighGear = false;

            // Control gains
            this.turningGains = new PIFGains(0.0088, 0.01);
            this.distanceGains = new PDFGains(0.478, 0.008);

            // Ramp rates
            this.defaultRampSeconds = 0.12;
            this.pathingRampSeconds = 0.20;


        }
    };

    // Drivetrain CAN ids
    public static final int DRIVETRAIN_FRONT_LEFT_ID = 1;
    public static final int DRIVETRAIN_REAR_LEFT_ID = 2;
    public static final int DRIVETRAIN_FRONT_RIGHT_ID = 3;
    public static final int DRIVETRAIN_REAR_RIGHT_ID = 4;

    // Config for drivetrain motors. We can use the default
    public static CTREConfig DRIVETRAIN_MOTOR_CONFIG = new CTREConfig();

    // Drivetrain sensor TPR
    public static final int DRIVETRAIN_ENCODER_TPR = 1400;
}