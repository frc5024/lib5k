package io.github.frc5024.common_drive.controller;

/**
 * Gains for a PDF controller
 */
@Deprecated(since = "October 2020", forRemoval = true)
public class PDFGains {

    public double kP;
    public double kD;
    public double kFF;

    /**
     * Create gains for a PDF controller
     * 
     * @param kP P gain
     * @param kD D gain
     */
    public PDFGains(double kP, double kD) {
        this(kP, kD, 0.0);
    }

    /**
     * Create gains for a PDF controller
     * 
     * @param kP  P gain
     * @param kD  D gain
     * @param kFF FeedForward value
     */
    public PDFGains(double kP, double kD, double kFF) {
        this.kP = kP;
        this.kD = kD;
        this.kFF = kFF;
    }
}