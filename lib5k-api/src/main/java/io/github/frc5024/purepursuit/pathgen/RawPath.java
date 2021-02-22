package io.github.frc5024.purepursuit.pathgen;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * The RawPath class lets you define Paths completely manually. This is only for
 * use by path importers
 */
public class RawPath extends Path {

    /**
     * Create a raw path from a pre-generated list of points
     * 
     * @param points Points
     */
    public RawPath(Translation2d... points) {
        this.name = "RawPath";

        // Create and fill points list
        this.points = new ArrayList<Translation2d>();

        for (Translation2d point : points) {
            this.points.add(point);
        }

        // Set the first and last point as waypoints
        this.waypoints = new Translation2d[2];
        this.waypoints[0] = this.points.get(0);
        this.waypoints[1] = this.points.get(this.points.size() - 1);
    }

}