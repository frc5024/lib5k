package io.github.frc5024.lib5k.utils;

/**
 * Used for toggle-based buttons
 */
public class Toggle {
    boolean m_currentOutput = false;

    /**
     * Feed the input state to the toggle
     * 
     * @param input Current button input
     * @return Toggle output
     */
    public boolean feed(boolean input) {
        // Toggle the output
        if (input) {
            m_currentOutput = !m_currentOutput;
        }

        // Return the output
        return m_currentOutput;
    }

    /**
     * This is to be used in a situation were you are not looking to toggle the
     * value, just read
     * 
     * @return Current output of the Toggle
     */
    public boolean get() {
        return m_currentOutput;
    }

    /**
     * Reset the current output
     */
    public void reset() {
        m_currentOutput = false;
    }
}