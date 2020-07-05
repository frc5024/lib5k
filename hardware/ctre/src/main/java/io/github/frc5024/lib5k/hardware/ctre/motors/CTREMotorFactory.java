package io.github.frc5024.lib5k.hardware.ctre.motors;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import io.github.frc5024.lib5k.hardware.ctre.util.TalonHelper;

/**
 * A Class for making talons
 */
public class CTREMotorFactory {

    private CTREMotorFactory() {
    }

    /**
     * Creates a Talon SRX 
     * 
     * @param id the id of the motor
     * @param config the talon config
     * 
     * @return a configured talon
     */
    public static ExtendedTalonSRX createTalonSRX(int id, CTREConfig config) {

        // Creates a TalonSRX
        ExtendedTalonSRX talon = new ExtendedTalonSRX(id, config);

        // Configures the motor
        if (config.configFactoryDefault) {
            talon.configFactoryDefault();
        }

        talon.setSafetyEnabled(config.setSafety);

        talon.setInverted(config.setInverted);

        talon.setNeutralMode(config.setBrake ? NeutralMode.Brake : NeutralMode.Coast);

        if (config.setCurrentLimit) {
            TalonHelper.configCurrentLimit(talon, config.peakAmps, config.durationMS, config.holdAmps,
                    config.timeoutMS);
            talon.enableCurrentLimit(config.enableCurrentLimit);
        }

        // Stops the motor
        talon.stopMotor();

        return talon;
    }


    /**
     * Creates a TalonFX
     * 
     * @param id the id of the motor
     * @param config the talon config
     * 
     * @return a configured TalonFX
     */
    public static ExtendedTalonFX createTalonFX(int id, CTREConfig config) {

        ExtendedTalonFX talon = new ExtendedTalonFX(id, config);

        if (config.configFactoryDefault) {
            talon.configFactoryDefault();
        }

        talon.setSafetyEnabled(config.setSafety);

        talon.setInverted(config.setInverted);

        talon.setNeutralMode(config.setBrake ? NeutralMode.Brake : NeutralMode.Coast);

        talon.stopMotor();

        return talon;

    }

    /**
     * Creates a VictorSPX
     * 
     * @param id the id of the motor
     * @param config the config for the motor
     * 
     * @return a configured victorSPX
     */
    public static ExtendedVictorSPX createVictorSPX(int id, CTREConfig config) {
        ExtendedVictorSPX victor = new ExtendedVictorSPX(id, config);

        if (config.configFactoryDefault) {
            victor.configFactoryDefault();
        }

        victor.setSafetyEnabled(config.setSafety);

        victor.setInverted(config.setInverted);

        victor.setNeutralMode(config.setBrake ? NeutralMode.Brake : NeutralMode.Coast);

        victor.stopMotor();

        return victor;
    }

}