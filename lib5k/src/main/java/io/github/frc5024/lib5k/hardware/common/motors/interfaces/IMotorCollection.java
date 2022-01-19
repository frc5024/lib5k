package io.github.frc5024.lib5k.hardware.common.motors.interfaces;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;

/**
 * Common interface for any collection of motor controllers
 */
public interface IMotorCollection extends MotorController {

    /**
     * Only set on new data
     * 
     * @param speed Motor speed
     */
    public void setBuffer(double speed);
}