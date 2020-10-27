package io.github.frc5024.lib5k.config.types;

/**
 * JSON object for an encoder
 * 
 * <pre>
 * <code>
 * {
 *  "cpr": 0,
 *  "inverted": false // Default: false
 * }
 * </code>
 * </pre>
 */
public class JSONEncoder {
    public int cpr;
    public boolean inverted = false;
}