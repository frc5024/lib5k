package io.github.frc5024.lib5k.hardware.revrobotics.motors;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class RevConfig {

    public MotorType motorType;

    public Boolean restoreFactoryDefaults;

    public boolean isInverted;

    public boolean setCurrentLimit;

    public int freeLimit;

    public int stallLimit;

    public int rpmLimit;

    public boolean enableBrakes;

    /**
     * Creates a default Rev Motor Config
     * 
     * @param motorType the type of motor being used, brushed/brushless
     */
    public RevConfig(MotorType motorType) {
        this(motorType, false, true, false, 30, 0, 20000, false);
    }

    /**
     * Creates a Rev Motor Config with configurable inversion
     * 
     * @param motorType the type of motor being used, brushed/brushless
     * @param isInverted should the motor be inverted
     */
    public RevConfig(MotorType motorType, boolean isInverted){
        this(motorType, isInverted, true, false, 30, 0, 20000, false);
    }


    /**
     * Creates a Rev Motor Config with configurable current limiting settings
     * 
     * @param motorType the type of motor being used, brushed/brushless
     * @param setCurrentLimit should the current limit be configured
     * @param freeLimit what should the free limit of the motor be
     * @param stallLimit what should the stall limit of the motor be
     * @param rpmLimit what should the rpm limit of the motor be
     */
    public RevConfig(MotorType motorType, boolean setCurrentLimit, int freeLimit, int stallLimit, int rpmLimit){
        this(motorType, false, true, setCurrentLimit, freeLimit, stallLimit, rpmLimit, false);
    }


    /**
     * Creates a Configuable Motor Config
     * 
     * @param motorType the type of motor being used, brushed/brushless
     * @param isInverted should the motor be inverted
     * @param restoreFactoryDefaults should the motor be restored to factory defaults
     * @param setCurrentLimit should the current limit be configured
     * @param freeLimit what should the free limit of the motor be
     * @param stallLimit what should the stall limit of the motor be
     * @param rpmLimit what should the rpm limit of the motor be
     * @param enableBrakes should the brakes be enabled
     */
    public RevConfig(MotorType motorType, boolean isInverted, boolean restoreFactoryDefaults, boolean setCurrentLimit,
            int freeLimit, int stallLimit, int rpmLimit, boolean enableBrakes) {

        this.motorType = motorType;

        this.restoreFactoryDefaults = restoreFactoryDefaults;

        this.isInverted = isInverted;

        this.setCurrentLimit = setCurrentLimit;

        this.freeLimit = freeLimit;

        this.stallLimit = stallLimit;

        this.rpmLimit = rpmLimit;

        this.enableBrakes = enableBrakes;

    }

    

}