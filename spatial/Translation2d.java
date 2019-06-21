package frc.common.spatial;

import java.awt.Point;

/**
 * A relitive point in 2D space.
 * 
 * Taken form 254's library:
 * https://github.com/Team254/FRC-2018-Public/blob/873e8f49e583e40167ea45382ed63cceaa07ac49/cheesy-path/src/main/java/com/team254/lib/geometry/Translation2d.java
 */
public class Translation2d {

    protected final double x;
    protected final double y;

    public Translation2d() {
        this.x = 0;
        this.y = 0;
    }

    public Translation2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Translation2d(final Translation2d other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Translation2d(final Translation2d start, final Translation2d end) {
        this.x = end.x - start.x;
        this.y = end.y - start.y;
    }

    /**
     * The "norm" of a transform is the Euclidean distance in x and y.
     *
     * @return sqrt(x ^ 2 + y ^ 2)
     */
    public double norm() {
        return Math.hypot(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}