package frc.common;

public class DriveSignal {
    double left_speed;
    double right_speed;

    boolean is_high_gear;
    boolean is_brakes;

    public DriveSignal(double left, double right) {
        this(left, right, false, false);
    }

    public DriveSignal(double left, double right, boolean isHigh, boolean isBrakes) {
        this.left_speed = left;
        this.right_speed = right;
        this.is_high_gear = isHigh;
        this.is_brakes = isBrakes;
    }

    public double getLeft() {
        return this.left_speed;
    }

    public double getRight() {
        return this.left_speed;
    }
    public boolean getBrakes() {
        return this.is_brakes;
    }
    public boolean getHighGear() {
        return this.is_high_gear;
    }
}