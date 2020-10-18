package io.github.frc5024.testbed;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;

/**
 * OI defines mappings between inputs and joystick packets
 */
public class OI {

    // Instance
    private static OI instance = null;

    // HID devices
    private XboxController driverController;

    private OI() {
        driverController = new XboxController(ConfigLoader.getConfig().driver_controller_hid_slot);
    }

    public static OI getInstance() {
        if (instance == null) {
            instance = new OI();
        }
        return instance;
    }

    /**
     * Get the robot "throttle" input
     * 
     * @return Throttle [-1.0-1.0]
     */
    public double getThrottle() {
        double speed = 0.0;

        // Determine the speed by subtracting the left trigger from the right. This will
        // effectively turn the left into "backwards speed" and the right into "forwards
        // speed"
        speed += driverController.getTriggerAxis(GenericHID.Hand.kRight);
        speed -= driverController.getTriggerAxis(GenericHID.Hand.kLeft);

        return speed;
    }

    /**
     * Get the robot "steering" input
     * 
     * @return Turn [-1.0-1.0]
     */
    public double getSteering() {
        return driverController.getX(GenericHID.Hand.kLeft);
    }

}