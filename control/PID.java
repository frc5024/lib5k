package frc.common.control;

import java.util.logging.Logger;

/**
 * Our custom PID controller implementation.
 * 
 * Based off of faceincake's PID code from 2018 and 2019
 */
public class PID {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private double kP, kI, kD;
    private double integral = 0.0;
    private double previous_error = 0.0;
    private double setpoint;

    /**
     * PID Constructor
     * 
     * @param kp P gain
     * @param ki I gain
     * @param kd D gain
     */
    public PID(double kp, double ki, double kd) {
        this.kP = kp;
        this.kI = ki;
        this.kD = kd;

        logger.config("PID gains have been set to: "+kp+", "+ki+", "+kd);

        this.setpoint = 0.0;
    }

    /**
     * Change the target value for the PID controller to reach.
     * 
     * This could be an angle for turning, or an encoder value 
     * for moving an elevator. 
     * 
     * @param setpoint The optimal result (ex. the angle to turn to)
     */
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
        logger.config("PID setpoint has been set to: "+setpoint);
    }

    /**
     * Change the P, I and D gains on the fly.
     * 
     * This is for applications where shifting gains are required. 
     * If you do not need to change the gains more then once, don't use this.
     * 
     * @param kp P gain
     * @param ki I gain
     * @param kd D gain
     */
    public void setGains(double kp, double ki, double kd) {
        this.kP = kp;
        this.kI = ki;
        this.kD = kd;

        logger.config("PID gains have been reset to: "+kp+", "+ki+", "+kd);
    }

    /**
     * Feed the controller with a sensor reading
     * 
     * @param sensor_reading The current reading of the input sensor (ex. gyro angle)
     * 
     * @return The output of the controller. This generally should be fed directly into a motor
     */
    public double feed(double sensor_reading) {
        /* Do the math */
        double error = this.setpoint - sensor_reading; // Error = Target - Actual
        this.integral += (error * 0.02);
        double derivative = (error - this.previous_error) / 0.02;

        this.previous_error = error;

        return (this.kP * error) + (this.kI * this.integral) + (this.kD * derivative);
    }
}