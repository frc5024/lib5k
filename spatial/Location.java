package frc.common.spatial;

public class Location {
    double angle;
    double distance;

    public Location(double distance, double angle) {
        this.distance = distance;
        this.angle = angle;
    }

    public Location(Translation2d point) {
        this.distance = Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
        this.angle = Math.atan(point.x / point.y);
    }
}