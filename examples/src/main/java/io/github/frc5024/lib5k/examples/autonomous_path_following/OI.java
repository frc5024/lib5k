package io.github.frc5024.lib5k.examples.autonomous_path_following;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class OI {
    
    public static XboxController driverController = new XboxController(0);

    public static double getThrottle() {
        return driverController.getTriggerAxis(Hand.kRight) - driverController.getTriggerAxis(Hand.kLeft);
    }

    public static double getTurn(){
        return driverController.getX(Hand.kLeft);
    }
}