package io.github.frc5024.lib5k.csvlogging;

import java.util.Collection;
import java.util.HashMap;

import io.github.frc5024.lib5k.logging.RobotLogger;

public class LoggingObject {

    // Internal map of data
    private HashMap<String, Object> internalMap = new HashMap<>();

    // Object name
    private String name;

    public LoggingObject(String name, String... keys) {
        this.name = name;

        // Fill the map with nothing
        for (String key : keys) {
            internalMap.put(key, "");
        }
    }

    /**
     * Set a key to a value if the key exists
     * 
     * @param key   Key name
     * @param value Value
     */
    public void setValue(String key, Object value) {
        if (internalMap.containsKey(key)) {
            internalMap.put(key, value);
        } else {
            RobotLogger.getInstance().log("Key %s does not exist in LoggingObject %s", key, name);
        }
    }

    /**
     * Set a key to a value if the key exists
     * 
     * @param key   Key name
     * @param value Value
     */
    public void setValue(String key, boolean value) {
        setValue(key, (Boolean) value);
    }

    /**
     * Set a key to a value if the key exists
     * 
     * @param key   Key name
     * @param value Value
     */
    public void setValue(String key, int value) {
        setValue(key, (Integer) value);
    }

    /**
     * Set a key to a value if the key exists
     * 
     * @param key   Key name
     * @param value Value
     */
    public void setValue(String key, double value) {
        setValue(key, (Double) value);
    }

    /**
     * Set a key to a value if the key exists
     * 
     * @param key   Key name
     * @param value Value
     */
    public void setValue(String key, float value) {
        setValue(key, (Float) value);
    }

    /**
     * Get this object's CSV header
     * 
     * @return Header
     */
    protected String getHeader() {

        // Build header
        StringBuilder sb = new StringBuilder();

        for (String key : internalMap.keySet()) {
            sb.append(name);
            sb.append(":");
            sb.append(key);
            sb.append(",");
        }

        return sb.toString();
    }

    /**
     * Get all values
     * 
     * @return Values
     */
    protected Collection<Object> getRow() {
        return internalMap.values();
    }
}