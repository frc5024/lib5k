package frc.common.wrappers;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * A high level wrapper class for an encoder attached to a WPI_TalonSRX.
 */
public class EncoderWrapper {
    WPI_TalonSRX talon;
    
    boolean sensor_phase;
    boolean has_configured = false;
    int ticks_per_rev;
    int offset = 0;
    double circumference;

    /**
     * Mesurement unit type
     */
    enum Units {
        kInches, 
        kFeet, 
        kCentimetres, 
        kMetres
    }

    /**
     * EncoderWrapper Constructor
     * 
     * @param talon A talon object for any talon with an encoder attached
     */
    public EncoderWrapper(WPI_TalonSRX talon) {
        this.talon = talon;  
    }

    /**
     * Configure the talon with our preferred settings
     */
    public void configDefault() {
        talon.configFactoryDefault();
    }
    
    /**
     * Configure the encoder
     * 
     * @param phase Is the encoder backwards?
     * @param wheel_circ Circumference of the output wheel or gear in inches
     * @param ticks_per_rev Number of ticks reported by the encoder per revolution of the wheel
     */
    public void config(boolean phase, double wheel_circ, int ticks_per_rev) {
        talon.setSensorPhase(phase);
        this.circumference = wheel_circ;
        this.ticks_per_rev = ticks_per_rev;
        this.has_configured = true;
    }  
    
    /**
     * Set the encoder sensor phase
     * 
     * @param phase Is the encoder counting down while the wheel is spining forwards?
     */
    public void setPhase(boolean phase) {
        talon.setSensorPhase(phase);
        this.sensor_phase = phase;
    }
    
    /**
     * Read the tick count from the encoder
     * 
     * @return Encoder tick count
     */
    public int getTicks() {
        return (talon.getSelectedSensorPosition() - this.offset);
    }

    /**
     * Read the raw tick count from the encoder (ignores resets)
     * 
     * @return Encoder tick count
     */
    public int getRawTicks() {
        return talon.getSelectedSensorPosition();
    }

    /**
     * Set the encoder offset to the current reading.
     * 
     * (Current position is now 0)
     */
    public void reset() {
        this.offset = getRawTicks();
    }

    /**
     * Get the distance the encoder had travled
     * 
     * @param unit_type Unit of mesurement
     * @return Distance travled in the specified unit of mesurement
     */
    public double getDistance(Units unit_type) {
        if (this.has_configured) {
            /* Talon has been configured properly (hopefully) */
            switch(unit_type) {
                case kInches:
                    return (getTicks() / ticks_per_rev) * circumference;
                
                case kFeet:
                    return ((getTicks() / ticks_per_rev) * circumference) / 12;
                
                case kCentimetres:
                    return ((getTicks() / ticks_per_rev) * circumference) * 2.45;
                
                case kMetres:
                    return (((getTicks() / ticks_per_rev) * circumference) * 2.45) / 100;
                
                default:
                return 0.0;
            }
            
        } else {
            System.out.println("Talon has not been configured, but data was requested! Returning 0.0");
            return 0.0;
        }
    }

}