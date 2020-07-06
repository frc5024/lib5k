package io.github.frc5024.lib5k.hardware.ctre.motors;

/**
 * A config class for Talons
 */
public class CTREConfig {

    // Should the motor be set to Factory Default
    public boolean configFactoryDefault;

    // Should the motor safety be set
    public boolean setSafety;

    // Should the motor be inverted
    public boolean setInverted;

    // Should the brakes be set
    public boolean setBrake;

    // Should the current limit be set
    public boolean setCurrentLimit;

    // Current limits peak amps
    public int peakAmps;

    // Duration in milliseconds of current limit
    public int durationMS;

    // Current limit hold amps
    public int holdAmps;

    // Current limit timeout in milliseconds
    public int timeoutMS;

    // Should enable Current Limiting
    public boolean enableCurrentLimit;

    /**
     * Default Config Constructor
     */
    public CTREConfig() {
        this(true, true, true, false, true, 33, 15, 30, 0, true);    
    }

    /**
     * Config Constructor for changing motor inversion 
     * @param setInverted should the motor be inverted
     */
    public CTREConfig(boolean setInverted){
        this(true, true, setInverted, false, true, 33, 15, 30, 0, true);       
    }

    /**
     * Config Constructor for Configuring Current Limits
     * @param setCurrentLimit Should the current limit be set
     * @param peakAmps Current limits peak amps
     * @param durationMS Duration in milliseconds of current limit
     * @param holdAmps Current limit hold amps
     * @param timeoutMS Current limit timeout in milliseconds
     * @param enableCurrentLimit should the current limit be enabled
     */
    public CTREConfig(boolean setCurrentLimit, int peakAmps, int durationMS, int holdAmps, int timeoutMS, boolean enableCurrentLimit){
        this(true, true, true, false, setCurrentLimit, peakAmps, durationMS, holdAmps, timeoutMS, enableCurrentLimit); 
    }

    /**
     * Creates a custom config
     * @param configFactoryDefault Should the motor be set to Factory Default
     * @param setSafety Should the motor safety be set
     * @param setInverted Should the motor be inverted
     * @param setBrake Should the brakes be set
     * @param setCurrentLimit Should the current limit be set
     * @param peakAmps Current limits peak amps
     * @param durationMS Duration in milliseconds of current limit
     * @param holdAmps Current limit hold amps
     * @param timeoutMS Current limit timeout in milliseconds
     * @param enableCurrentLimit should the current limit be enabled
     */
    public CTREConfig(boolean configFactoryDefault, boolean setSafety, boolean setInverted, boolean setBrake,
            boolean setCurrentLimit, int peakAmps, int durationMS, int holdAmps, int timeoutMS, boolean enableCurrentLimit) {

        this.configFactoryDefault = configFactoryDefault;

        this.setSafety = setSafety;

        this.setInverted = setInverted;

        this.setBrake = setBrake;

        this.setCurrentLimit = setCurrentLimit;

        this.peakAmps = peakAmps;

        this.durationMS = durationMS;

        this.holdAmps = holdAmps;

        this.timeoutMS = timeoutMS;

        this.enableCurrentLimit = enableCurrentLimit;
        
    }

}