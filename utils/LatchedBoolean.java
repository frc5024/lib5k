package frc.common.utils;

public class LatchedBoolean {
    boolean prev_val = false;

    public boolean feed(boolean input) {
        if (input != prev_val) {
            prev_val = input;
            return true;
        }
        return false;
    }
}