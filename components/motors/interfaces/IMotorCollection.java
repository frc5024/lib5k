package frc.lib5k.components.motors.interfaces;

import edu.wpi.first.wpilibj.SpeedController;

public interface IMotorCollection extends SpeedController {

    /**
     * Only set on new data
     * 
     * @param speed Motor speed
     */
    public void setBuffer(double speed);
}