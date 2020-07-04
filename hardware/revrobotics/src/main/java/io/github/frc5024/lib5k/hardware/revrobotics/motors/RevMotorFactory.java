package io.github.frc5024.lib5k.hardware.revrobotics.motors;

import com.revrobotics.CANSparkMax.IdleMode;

public class RevMotorFactory{


    private RevMotorFactory(){}


    public static ExtendedSparkMax createSparkMax(int id, RevMotorConfig config){

        ExtendedSparkMax sparkMax = new ExtendedSparkMax(id, config);


        if(config.restoreFactoryDefaults){
            sparkMax.restoreFactoryDefaults();  
        }
        

        sparkMax.setInverted(config.isInverted);
        

        if(config.setCurrentLimit){
            sparkMax.setSmartCurrentLimit(config.freeLimit, config.stallLimit, config.rpmLimit);
        }
        
        sparkMax.setIdleMode(config.enableBrakes ? IdleMode.kBrake : IdleMode.kCoast);


        sparkMax.clearFaults();
        sparkMax.stopMotor();


        return sparkMax;
        
    }










}