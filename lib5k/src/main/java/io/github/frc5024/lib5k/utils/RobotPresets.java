package io.github.frc5024.lib5k.utils;

import edu.wpi.first.wpilibj.system.plant.DCMotor;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;

/**
 * Robot presets are known measurement taken from various robots. These should
 * mostly be used for unit tests, and sanity checks.
 */
public class RobotPresets {

    /**
     * Preset constants for 5024's MiniBot
     */
    public class MiniBot {

    }

    /**
     * Preset constants for 5024's Darth Raider
     */
    public static class DarthRaider {

        /**
         * Characteristics of Darth Raider's flywheel
         */
        public static class FlywheelPreset {
            // The type of motor used
            public static final DCBrushedMotor MOTOR_TYPE = new DCBrushedMotor(DCMotor.getNEO(1));

            // The mass of the actual wheels on the shooter
            public static final double LAUNCHER_MASS_KG = 0.144583;

            // The diameter of the wheels on the shooter
            public static final double LAUNCHER_DIAMETER = 0.123825;

            // The mass of the added weight
            public static final double FLYWHEEL_MASS_KG = 0.1995806;

            // The diameter of the added weight
            public static final double FLYWHEEL_DIAMETER = 0.0508;

            // The gear ratio between the MOTOR and ENCODER
            public static final double SENSOR_GEAR_RATIO = 1.0;

            // The realistic maximum velocity of the flywheel
            public static final double REALISTIC_MAX_VELOCITY_RPM = 4450.0;

            // The velocity epsilon of the system.
            public static final double VELOCITY_EPSILON_RPM = 80.0;
        }

    }

}