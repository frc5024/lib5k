package frc.common.drive;

public class DriveSignal {
    double left_speed;
    double right_speed;

    boolean is_brakes;

    public DriveSignal() {
        this(0, 0, false);
    }

    public DriveSignal(double left, double right) {
        this(left, right, false);
    }

    public DriveSignal(double left, double right,  boolean isBrakes) {
        this.left_speed = left;
        this.right_speed = right;
        this.is_brakes = isBrakes;
    }

    /**
     * Normalizes the wheel speeds to a symmetric range with the given
     * magnitude.
     *
     * @param maxSpeed Maximum wheel speed in normalization range
     *                 [-maxSpeed..maxSpeed].
     */
    public void normalize(double maxSpeed) {
        double maxMagnitude = Math.max(Math.abs(left_speed), Math.abs(right_speed));
        if (maxMagnitude > maxSpeed) {
            left_speed *= maxSpeed / maxMagnitude;
            right_speed *= maxSpeed / maxMagnitude;
        }
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
}