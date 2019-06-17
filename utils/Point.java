package frc.common.utils;

public class Point {

    private double x;
    private double y;

    /**
     * Constructor that creates a Point object.
     *
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return The x coordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * @return The y coordinate
     */
    public double getY() {
        return this.y;
    }

    /**
     * @param p some Point
     * @return the distance to Point p
     */
    public double distanceTo(Point p){
        return distanceTo(p, 1);
    }

    /**
     * @param p some Point
     * @param scale distance scaling factor
     * @return the scaled distance to Point p
     */
    public double distanceTo(Point p, double scale){
        return Math.sqrt(Math.pow(p.getX()-this.getX(), 2) + Math.pow(p.getY()-this.getY(), 2));
    }

    /**
     *
     * @param p some Point
     * @return the angle of the line between this and Point p
     */
    public double HeadingTo(Point p){
        return Math.atan2(p.getY()-this.getY(),p.getX()-this.getX());
    }
}