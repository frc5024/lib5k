package io.github.frc5024.lib5k.hardware.ctre.motors;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import io.github.frc5024.lib5k.hardware.ctre.util.TalonHelper;

/**
 * A Class for making talons
 */
public class CTREMotorFactory{

    private CTREMotorFactory(){}



    public static ExtendedTalonSRX createTalonSRX(int id, TalonConfig config){

        ExtendedTalonSRX talon = new ExtendedTalonSRX(id, config);

        

        if(config.configFactoryDefault){
            talon.configFactoryDefault();
        }

        talon.setSafetyEnabled(config.setSafety);
        
        talon.setInverted(config.setInverted);

        talon.setNeutralMode(config.setBrake ? NeutralMode.Brake : NeutralMode.Coast);


        if(config.setCurrentLimit){
            TalonHelper.configCurrentLimit(talon, config.peakAmps, config.durationMS, config.holdAmps, config.timeoutMS);
            talon.enableCurrentLimit(config.enableCurrentLimit);
        }
        

        talon.stopMotor();

        return talon;
    } 

    public static ExtendedTalonFX createTalonFX(int id, TalonConfig config){

        ExtendedTalonFX talon = new ExtendedTalonFX(id, config);
        
        if(config.configFactoryDefault){
            talon.configFactoryDefault();
        }

        talon.setSafetyEnabled(config.setSafety);
        
        talon.setInverted(config.setInverted);

        talon.setNeutralMode(config.setBrake ? NeutralMode.Brake : NeutralMode.Coast);

        talon.stopMotor();
        
        return talon;

    }


    public static ExtendedVictorSPX createVictorSPX(int id, TalonConfig config){
        ExtendedVictorSPX victor = new ExtendedVictorSPX(id, config);

        if(config.configFactoryDefault){
            victor.configFactoryDefault();
        }

        victor.setSafetyEnabled(config.setSafety);
        
        victor.setInverted(config.setInverted);

        victor.setNeutralMode(config.setBrake ? NeutralMode.Brake : NeutralMode.Coast);


        
        victor.stopMotor();        


        return victor;
    }



}