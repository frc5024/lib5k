package io.github.frc5024.common_drive.controller;

/**
 * A PDFController is a simple PD + FeedForward calculation. <br>
 * This is helpful for calculating steering gains
 */
public class PDFController implements BaseController {

    private PDFGains gains;
    private boolean signedFF;

    private double prevError;

    /**
     * Create a PDFController
     * 
     * @param gains    PDF gains
     * @param signedFF Should FF be inverted if the output is negative?
     */
    public PDFController(PDFGains gains, boolean signedFF) {
        this.gains = gains;
        this.signedFF = signedFF;
    }

    /**
     * Calculate the controller output with FeedForward
     * 
     * @param error System error
     * @param dt    Delta time
     * @return Output
     */
    public double calculate(double error, double dt) {
        return calculate(error, dt, true);
    }

    /**
     * Calculate the controller output
     * 
     * @param error    System error
     * @param dt       Delta time
     * @param enableFF Set this to false to bypass the FeedForward calculation
     * @return Output
     */
    public double calculate(double error, double dt, boolean enableFF) {

        // Handle div-by-zero errors
        if (dt == 0.0) {
            dt = 1e-12;
        }

        // Calculate the derivative
        double derivative = (error - this.prevError) / dt;
        this.prevError = error;

        // Calculate output
        double output = ((this.gains.kP * error) + (this.gains.kD * derivative));

        // Calculate FF
        double FF = this.gains.kFF;
        if (this.signedFF && output < 0.0) {
            FF *= -1;
        }
        if (!enableFF) {
            FF = 0.0;
        }

        return output + FF;
    }

    /**
     * Reset the controller
     */
    public void reset() {
        this.prevError = 0.0;
    }
}