package io.github.frc5024.lib5k.control_loops.models;

import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.util.Units;

/**
 * DC Brushed motor constants. <br>
 * <br>
 * A Java translation of:
 * https://github.com/calcmogul/frccontrol/blob/master/frccontrol/models.py
 */
public class DCBrushedMotor extends DCMotor {

    public final double Kv;
    public final double freeSpeedRPM;

    /**
     * Holds the constants for a DC brushed motor
     * 
     * @param nominalVoltage voltage at which the motor constants were measured
     * @param stallTorque    current draw when stalled in Newton-meters
     * @param stallCurrent   current draw when stalled in Amps
     * @param freeCurrent    current draw under no load in Amps
     * @param freeSpeed      angular velocity under no load in RPM
     */
    private DCBrushedMotor(double nominalVoltage, double stallTorque, double stallCurrent, double freeCurrent,
            double freeSpeed) {
        super(nominalVoltage, stallCurrent, stallCurrent, freeCurrent, freeSpeed);

        // Add a few extra fields
        this.Kv = Units.radiansPerSecondToRotationsPerMinute(super.m_KvRadPerSecPerVolt);
        this.freeSpeedRPM = Units.radiansPerSecondToRotationsPerMinute(super.m_freeSpeedRadPerSec);

    }
}