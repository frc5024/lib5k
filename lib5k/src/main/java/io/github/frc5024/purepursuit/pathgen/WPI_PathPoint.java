package io.github.frc5024.purepursuit.pathgen;

import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * This class is the datastructure used to parse WPILib Path json files into
 * something Lib5K understands. The fields of this class must match the JS
 * object fields from the json files.
 */
public class WPI_PathPoint {

    /**
     * This class is the datastructure used to denote pose in a WPILib path json
     * file
     */
    public class WPI_PathPose {

        public class WPI_PathTranslation {
            public double x;
            public double y;
        }

        public class WPI_PathRotation {
            public double radians;
        }

        public WPI_PathTranslation translation;
        public WPI_PathRotation rotation;
    }

    // Data
    public double time;
    public double velocity;
    public double acceleration;
    public WPI_PathPose pose;
    public double curvature;

    /**
     * Convert the WPI_PathPoint into a Translation2d
     * 
     * @return Translation2d object
     */
    public Translation2d getTranslation() {
        return new Translation2d(this.pose.translation.x, this.pose.translation.y);
    }
}