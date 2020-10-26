package io.github.frc5024.lib5k.config.types;

/**
 * JSON object for PID gains
 * 
 * <pre>
 * <code>
 * {
 *  "Kp": 0.0,
 *  "Ki": 0.0,
 *  "Kd": 0.0,
 *  "FF": 0.0, // Default: 0.0
 *  "settlingTime": 0.0 // Seconds. Default: 0.0
 * }
 * </code>
 * </pre>
 */
public class JSONPIDGains {

    public double Kp;
    public double Ki;
    public double Kd;
    public double FF = 0.0;
    public double settlingTime = 0.0;

}