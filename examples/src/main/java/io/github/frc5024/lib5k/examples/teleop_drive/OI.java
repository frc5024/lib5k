package io.github.frc5024.lib5k.examples.teleop_drive;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * This file contains mappings to controller inputs
 */
public class OI {
    
    public static XboxController driverController = new XboxController(0);

    /**
     * Calculate the robot's throttle
     * @return Throttle
     */
    public static double getThrottle() {
        return driverController.getTriggerAxis(Hand.kRight) - driverController.getTriggerAxis(Hand.kLeft);
    }

    /**
     * Calculate the robot's steering
     * @return Steering
     */
    public static double getTurn(){
        return driverController.getX(Hand.kLeft);
    }
}