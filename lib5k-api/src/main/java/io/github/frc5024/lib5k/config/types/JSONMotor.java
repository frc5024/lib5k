package io.github.frc5024.lib5k.config.types;

/**
 * JSON object for a motor
 * 
 * <pre>
 * <code>
 * {
 *  "id": 0,
 *  "inverted": false // Default: false
 * }
 * </code>
 * </pre>
 */
public class JSONMotor {
    public int id;
    public boolean inverted = false;
}