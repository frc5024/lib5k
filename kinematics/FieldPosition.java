package frc.lib5k.kinematics;

/**
 * Used to denote a robot position (field-relative)
 */
public class FieldPosition {
    double x, y, theta;

    public FieldPosition(double x, double y) {
        this(x, y, 0);
    }

    public FieldPosition(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    public double getX() {
        return x;

    }

    public double getY() {
        return y;
    }

    public double getTheta() {
        return theta;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

}