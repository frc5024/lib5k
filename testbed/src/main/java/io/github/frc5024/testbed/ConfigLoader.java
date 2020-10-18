package io.github.frc5024.testbed;

import java.io.FileNotFoundException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import edu.wpi.first.wpilibj.DriverStation;
import io.github.frc5024.lib5k.utils.json.JSONLoader;
import io.github.frc5024.lib5k.utils.json.types.MotorValues;
import io.github.frc5024.lib5k.utils.json.types.PIDValues;

public class ConfigLoader {

    public class Config {

        // HID devices
        public int driver_controller_hid_slot;

        // Drivetrain
        public DriveTrainConfig drivetrain;

    }

    public class DriveTrainConfig {

        // Controllers
        public PIDValues throttleController;
        public PIDValues steeringController;

        // Motors
        public MotorValues frontLeftMotor;
        public MotorValues rearLeftMotor;
        public MotorValues frontRightMotor;
        public MotorValues rearRightMotor;

    }

    // Config instance
    private static Config configInstance = null;

    /**
     * Get the robot's configuration data
     * 
     * @return Robot config
     */
    public static Config getConfig() {
        if (configInstance == null) {
            try {
                configInstance = JSONLoader.loadJsonObjectFromDeployDirectory("robotconfig.json", Config.class);
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                e.printStackTrace();
                DriverStation.reportError(
                        "CRITICAL ERROR: Failed to load core robot configuration data from deploy/robotconfig.json",
                        e.getStackTrace());
                System.exit(1);
            }
        }
        return configInstance;
    }

}