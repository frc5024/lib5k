package frc.common.wrappers;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

/**
 * A wrapper for Jaci's Waypoint.
 * 
 * But this one makes sense
 */
public class Locator extends Waypoint {

    /**
     * A friendly wrapper for Jaci's waypoint
     * 
     * @param distance How many meters forward of last point
     * @param position How many meters to the right of last point
     * @param degs     Final heading in degrees
     */
    public Locator(double distance, double position, double degs) {
        super(distance, position, Pathfinder.d2r(degs));
    }

    public static Waypoint buildWaypoint(double distance, double position, double degs) {
        return new Waypoint(distance, position, Pathfinder.d2r(degs));
    }
}