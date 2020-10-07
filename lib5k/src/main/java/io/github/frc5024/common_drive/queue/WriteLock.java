package io.github.frc5024.common_drive.queue;

import java.util.function.Consumer;

/**
 * This class is used to store and handle values that can only be read once
 * 
 * @param <T> Datatype
 */
public class WriteLock<T> {

    public boolean write;
    private T defaultValue;
    public T value;

    /**
     * Create a WriteLock
     */
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
    public void zero(){
        reset();
    }

    /**
     * Set to defaults
     */
    @Deprecated(since = "October 2020", forRemoval = true)
    public void reset() {
        this.write = false;
        this.value = this.defaultValue;
    }

    /**
     * Write a value and enable reading
     * 
     * @param value Value
     */
    @Deprecated(since = "October 2020", forRemoval = true)
    public void write(T value) {
        set(value);
    }

    /**
     * Write a value and enable reading
     * 
     * @param value Value
     */
    public void set(T value) {
        this.write = true;
        this.value = value;
    }

    /**
     * Get the value, and set write to false
     * 
     * @return Value
     */
    @Deprecated(since = "October 2020", forRemoval = true)
    public T consume() {
        this.write = false;
        return this.value;
    }

    /**
     * Safely consume the contained value
     * 
     * @param closure Consuming action
     */
    public void consume(Consumer<T> closure) {
        if (write) {
            closure.accept(this.value);
            this.write = false;
        }
    }
}