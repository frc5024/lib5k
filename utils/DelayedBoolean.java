package frc.lib5k.utils;

public class DelayedBoolean {
    boolean m_bool = false;
    int iterations = 0;

    public void setAfter(int n, boolean on) {
        if (iterations >= n) {
            m_bool = on;
            iterations++;
        }

    }

    public void reset() {
        iterations = 0;
    }

    public boolean get() {
        return m_bool;
    }
}