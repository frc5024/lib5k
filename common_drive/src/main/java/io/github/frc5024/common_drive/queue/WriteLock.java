package io.github.frc5024.common_drive.queue;

/**
 * A dataclass for storing a value, and if it should be written out or not
 */
public class WriteLock<T> {

    public boolean write;
    private T defaultValue;
    public T value;

    public WriteLock() {
        this(null);
    }

    /**
     * Create a WriteLock with a default value
     * 
     * @param defaultValue Default value
     */
    public WriteLock(T defaultValue) {
        this.write = false;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    /**
     * Set to defaults
     */
    public void zero() {
        this.write = false;
        this.value = this.defaultValue;
    }

    /**
     * Write a value and enable reading
     * 
     * @param value Value
     */
    public void write(T value) {
        this.write = true;
        this.value = value;
    }

    /**
     * Get the value, and set write to false
     * 
     * @return Value
     */
    public T consume() {
        this.write = false;
        return this.value;
    }
}