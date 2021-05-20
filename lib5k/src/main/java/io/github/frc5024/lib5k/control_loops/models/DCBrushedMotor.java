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

    private Double Kv;
    private Double freeSpeedRPM;

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
        super(nominalVoltage, stallTorque, stallCurrent, freeCurrent, freeSpeed, 1);

        // Add a few extra fields
        this.Kv = Units.radiansPerSecondToRotationsPerMinute(super.KvRadPerSecPerVolt);
        this.freeSpeedRPM = Units.radiansPerSecondToRotationsPerMinute(super.freeSpeedRadPerSec);

    }

    /**
     * Create a DCBrushedMotor from a DCMotor
     * @param motor DCMotor
     */
    public DCBrushedMotor(DCMotor motor) {
        this(motor.nominalVoltageVolts, motor.stallTorqueNewtonMeters, motor.stallCurrentAmps,
                motor.freeCurrentAmps, motor.freeSpeedRadPerSec);
    }

    /**
     * Get the motor's free spinning speed in RPM
     * 
     * @return Free speed
     */
    public double getFreeSpeedRPM() {
        if (this.freeSpeedRPM == null) {
            this.freeSpeedRPM = Units.radiansPerSecondToRotationsPerMinute(super.freeSpeedRadPerSec);
        }
        return this.freeSpeedRPM.doubleValue();
    }

    /**
     * Get the motor's Kv in RPM
     * 
     * @return Kv
     */
    public double getKv() {
        if (this.Kv == null) {
            this.Kv = Units.radiansPerSecondToRotationsPerMinute(super.KvRadPerSecPerVolt);
        }
        return this.Kv.doubleValue();
    }

    /**
     * Converts this to a DC motor, for use with WPILib functions
     * 
     * @return Cast to DCMotor
     */
    public DCMotor toDCMotor() {
        return (DCMotor) this;
    }

}
