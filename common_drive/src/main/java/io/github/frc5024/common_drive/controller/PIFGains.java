package io.github.frc5024.common_drive.controller;

/**
 * Gains for a PIF controller
 */
public class PIFGains {

    public double kP;
    public double kI;
    public double kFF;

    /**
     * Create gains for a PIF controller
     * 
     * @param kP  P gain
     * @param kI  I gain
     */
    public PIFGains(double kP, double kI) {
        this(kP, kI, 0.0);
    }

    /**
     * Create gains for a PIF controller
     * 
     * @param kP  P gain
     * @param kI  I gain
     * @param kFF FeedForward value
     */
    public PIFGains(double kP, double kI, double kFF) {
        this.kP = kP;
        this.kI = kI;
        this.kFF = kFF;
    }
}