package frc.common.control;

import frc.common.utils.Line;

/**
 * Input deadband with linear scaling
 * 
 * WARNING: This has not been tested.
 */
public class LinearDeadband {
    Line upper;
    Line lower;

    double deadband;

    /**
     * Construct a LinearDeadband with two lines
     * 
     * @param deadband Deadband zone size
     */
    public LinearDeadband(double deadband) {
        this.upper = new Line(deadband, 1.0, 0.0, 1.0);
        this.lower = new Line(1.0, -deadband, -1.0, 0.0);
        this.deadband = deadband;
    }

    public double feed(double input) {
        if (Math.abs(input) <= this.deadband) {
            return 0.0;
        }

        // Return the Y value of the respective line for the intput.
        // There is probably a better way to do this, make a pull request
        // if you have a better solution.
        return (input > 0.0) ? this.upper.getY(input) : this.lower.getY(input);
    }
}