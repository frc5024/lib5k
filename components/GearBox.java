package frc.lib5k.components;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * A GearBox is a wrapper for any pair of WPI_TalonSRX motor controllers where the first controller has an encoder attached.
 */
public class GearBox{
    public WPI_TalonSRX front, rear;

    private boolean is_inverse_motion = false;

    /**
     * GearBox Constructor
     * 
     * @param front The front or master talon in the gearbox
     * @param rear The rear or slave talon in the gearbox
     */
    public GearBox(WPI_TalonSRX front, WPI_TalonSRX rear){
        /* Store both Talons */
        this.front = front;
        this.rear = rear;

        /* Configure the Talons */
        this.front.configFactoryDefault();
        this.rear.follow(this.front);
    }

    /**
     * Wrapper method around the WPI_TalonSRX current limiting functionality
     * 
     * @param peakCurrent The current threshold that must be passed before the limiter kicks in
     * @param holdCurrent The current to hold the motors at once the threshold has been passed
     * @param peakDuration The duration of the current limit
     */
    public void limitCurrent(int peakCurrent, int holdCurrent, int peakDuration){
        int timeout = 0;
        this.front.configPeakCurrentLimit(peakCurrent, timeout);
        this.front.configPeakCurrentDuration(peakDuration, timeout);
        this.front.configContinuousCurrentLimit(holdCurrent, timeout);
        front.enableCurrentLimit(true);

        this.rear.configPeakCurrentLimit(peakCurrent, timeout);
        this.rear.configPeakCurrentDuration(peakDuration, timeout);
        this.rear.configContinuousCurrentLimit(holdCurrent, timeout);
        rear.enableCurrentLimit(true);
    }

    /**
     * Wrapper around the encoder for the front or master talon
     * 
     * @return Number of ticks reported by the front or master talon
     */
    public int getTicks() {
        return this.front.getSelectedSensorPosition();
    }
    
    /**
     * Flips the sensor phase of the GearBox's encoder
     * 
     * This is mainly used for inverse motion profiling
     * 
     * @param is_inverted Should the phase be inverted?
     */
    public void setInverseMotion(boolean is_inverted) {
        this.front.setSensorPhase(is_inverted);
    }

    /**
     * Is the gearbox currently in inverse motion mode?
     * 
     * @return Is inverse motion?
     */
    public boolean getInverseMotion() {
        return this.is_inverse_motion;
    }

    /**
     * Get the master controller
     * 
     * @return the front talon
     */
    public WPI_TalonSRX getMaster() {
        return this.front;
    }

    /**
     * Set the GearBox speed
     * 
     * @param speed Percent output
     */
    public void set(double speed) {
        this.front.set(speed);
    }
}