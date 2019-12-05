package frc.lib5k.components;

public class Latch {

    private boolean state = true;

    /**
     * Creates a Latch object.
     *
     * Latch is used as a sticky boolean for the robot.
     *
     */
    public Latch() {

    }

    /**
     * 
     * @param x The state of the Latch
     */
    public void feed(boolean x) {
        if (x == true) state = true;
    }

    public boolean get() {

    }

    /**
     * Resets the state of the Latch
     */
    public void resest() {
        state = false;
    }
}