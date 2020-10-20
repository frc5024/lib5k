package io.github.frc5024.lib5k.examples.statespace_flywheel;

import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;

public class RobotConfig {

    public static class FlywheelConfig {
        
        // The type of motor used
        public static final DCBrushedMotor MOTOR_TYPE = (DCBrushedMotor) DCBrushedMotor.getNEO(1);

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

        // The realistic maximum velocity of the flywheel (this should be measured with an encoder)
        public static final double REALISTIC_MAX_VELOCITY_RPM = 4450.0;

        // The velocity epsilon of the system.
        public static final double VELOCITY_EPSILON_RPM = 80.0;
    }
    
}