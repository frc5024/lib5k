package frc.common.utils;

/**
 * A simple line from two points
 */
public class Line {
    double m;
    double b;

    public Line(double x1, double x2, double y1, double y2) {
        this.m = (y2 - y1) / (x2 - x1);
        this.b = -(this.m * x1) + y1;
    }

    /**
     * Get the Y for a given X
     */
    public double getY(double x){
        return (this.m * x) + this.b;
    }

    /**
     * Get the X for a given Y
     */
    public double getX(double y) {
        return (y - this.b) / this.m;
    }
}