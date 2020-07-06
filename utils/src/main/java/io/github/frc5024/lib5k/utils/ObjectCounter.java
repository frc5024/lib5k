package io.github.frc5024.lib5k.utils;

import io.github.frc5024.lib5k.utils.annotations.FieldTested;

/**
 * A helper for counting the number of an object that exists
 */
@FieldTested(year=2020)
public class ObjectCounter {

    private int count;

    public ObjectCounter() {
        this(0);
    }

    public ObjectCounter(int offset) {
        this.count = offset;
    }

    /**
     * Every time this is called, it will return the next int in the counter.
     * 
     * @return Next int
     */
    public int getNewID() {
        return count++;
    }

}