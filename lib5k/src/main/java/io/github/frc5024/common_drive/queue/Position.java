package io.github.frc5024.common_drive.queue;

import io.github.frc5024.common_drive.gearing.Gear;

/**
 * DriveTrain position data
 */
public class Position {

    // Data
    public double leftEncoder;
    public double rightEncoder;
    public double leftSpeed;
    public double rightSpeed;
    public Gear leftShifter;
    public Gear rightShifter;

    /**
     * Create a position of 0
     */
    public Position() {
        this.zero();
    }

    /**
     * Reset this position to 0
     */
    public void zero() {
        this.leftEncoder = 0.0;
        this.rightEncoder = 0.0;
        this.leftSpeed = 0.0;
        this.rightSpeed = 0.0;
        this.leftShifter = Gear.LOW;
        this.rightShifter = Gear.LOW;

    }
}