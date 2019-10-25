package frc.lib5k.kinematics;

/**
 * Used to denote a robot position
 */
public class FieldPosition {
    double x, y, theta;

    /**
     * Copy constructor for a FieldPosition
     * 
     * @param position FieldPosition to copy from
     */
    public FieldPosition(FieldPosition position) {
        this(position.getX(), position.getY(), position.getTheta());
    }

    public FieldPosition(double x, double y) {
        this(x, y, 0);
    }

    public FieldPosition(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    /**
     * Strafe distance
     */
    public double getX() {
        return x;

    }

    /**
     * Forward distance
     * 
     * @return
     */
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

    /**
     * Get a new field-relative position from origin + rel
     * 
     * @param origin Original field-relative point
     * @param rel    Point relative to origin to transform by
     * @return New point
     */
    public static FieldPosition transformBy(FieldPosition origin, FieldPosition rel) {
        return new FieldPosition(origin.getX() + rel.getY() * Math.cos(rel.getX() + origin.getTheta()),
                origin.getY() + rel.getY() * Math.cos(rel.getX() + origin.getTheta()),
                origin.getTheta() + rel.getTheta());
    }

    public String toString(){
        return "(" + x + ", " + y + ", " + theta + ")";
    }

}