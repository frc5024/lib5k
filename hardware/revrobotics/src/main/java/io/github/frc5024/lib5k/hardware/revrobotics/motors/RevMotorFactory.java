package io.github.frc5024.lib5k.hardware.revrobotics.motors;

import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;


/**
 * A class for making rev Motors
 */
public class RevMotorFactory{


    private RevMotorFactory(){}


    /**
     * Creates a new Spark Max
     * 
     * @param id the Spark Max's motor id
     * @param MotorType the type of motor the Spark Max is
     * 
     * @return a configured Spark Max motor
     */
    public static ExtendedSparkMax createSparkMax(int id, MotorType MotorType){
        return createSparkMax(id, new RevMotorConfig(MotorType));
    }


    /**
     * Creates a new Spark Max
     * 
     * @param id the motor id
     * @param config the motor config
     * 
     * @return a configured Spark Max motor
     */
    public static ExtendedSparkMax createSparkMax(int id, RevMotorConfig config){

        // Makes a spark Motor
        ExtendedSparkMax sparkMax = new ExtendedSparkMax(id, config);

        // Motor Configuration
        if(config.restoreFactoryDefaults){
            sparkMax.restoreFactoryDefaults();  
        }
        

        sparkMax.setInverted(config.isInverted);
        

        if(config.setCurrentLimit){
            sparkMax.setSmartCurrentLimit(config.freeLimit, config.stallLimit, config.rpmLimit);
        }
        
        sparkMax.setIdleMode(config.enableBrakes ? IdleMode.kBrake : IdleMode.kCoast);


        sparkMax.clearFaults();

        // Stops motor
        sparkMax.stopMotor();


        return sparkMax;
        
    }


}