package frc.lib5k.spatial;

import frc.lib5k.spatial.Rotation2D;
import frc.lib5k.utils.Consts;

/**
 * A translation in a 2D coordinate frame. Translations are simply shifts in an
 * (x, y) plane.
 * 
 * Adapted from 254:
 * https://github.com/Team254/FRC-2019-Public/blob/f99f09a15a2c8e7b5df868ec3a4a81105dc88112/src/main/java/com/team254/lib/geometry/Translation2D.java
 */
public class Translation2D {
    protected static final Translation2D kIdentity = new Translation2D();

    public static Translation2D identity() {
        return kIdentity;
    }

    protected final double x_;
    protected final double y_;

    public Translation2D() {
        x_ = 0;
        y_ = 0;
    }

    public Translation2D(double x, double y) {
        x_ = x;
        y_ = y;
    }

    public Translation2D(final Translation2D other) {
        x_ = other.x_;
        y_ = other.y_;
    }

    public Translation2D(final Translation2D start, final Translation2D end) {
        x_ = end.x_ - start.x_;
        y_ = end.y_ - start.y_;
    }

    /**
     * The "norm" of a transform is the Euclidean distance in x and y.
     *
     * @return sqrt(x ^ 2 + y ^ 2)
     */
    public double norm() {
        return Math.hypot(x_, y_);
    }

    public double norm2() {
        return x_ * x_ + y_ * y_;
    }

    public double x() {
        return x_;
    }

    public double y() {
        return y_;
    }

    /**
     * We can compose Translation2D's by adding together the x and y shifts.
     *
     * @param other The other translation to add.
     * @return The combined effect of translating by this object and the other.
     */
    public Translation2D translateBy(final Translation2D other) {
        return new Translation2D(x_ + other.x_, y_ + other.y_);
    }

    /**
     * We can also rotate Translation2D's. See:
     * https://en.wikipedia.org/wiki/Rotation_matrix
     *
     * @param rotation The rotation to apply.
     * @return This translation rotated by rotation.
     */
    public Translation2D rotateBy(final Rotation2D rotation) {
        return new Translation2D(x_ * rotation.cos() - y_ * rotation.sin(), x_ * rotation.sin() + y_ * rotation.cos());
    }

    public Rotation2D direction() {
        return new Rotation2D(x_, y_, true);
    }

    /**
     * The inverse simply means a Translation2D that "undoes" this object.
     *
     * @return Translation by -x and -y.
     */
    public Translation2D inverse() {
        return new Translation2D(-x_, -y_);
    }

    public Translation2D interpolate(final Translation2D other, double x) {
        if (x <= 0) {
            return new Translation2D(this);
        } else if (x >= 1) {
            return new Translation2D(other);
        }
        return extrapolate(other, x);
    }

    public Translation2D extrapolate(final Translation2D other, double x) {
        return new Translation2D(x * (other.x_ - x_) + x_, x * (other.y_ - y_) + y_);
    }

    public Translation2D scale(double s) {
        return new Translation2D(x_ * s, y_ * s);
    }

    // public boolean epsilonEquals(final Translation2D other, double epsilon) {
    //     return Util.epsilonEquals(x(), other.x(), epsilon) && Util.epsilonEquals(y(), other.y(), epsilon);
    // }

    public static double dot(final Translation2D a, final Translation2D b) {
        return a.x_ * b.x_ + a.y_ * b.y_;
    }

    public static Rotation2D getAngle(final Translation2D a, final Translation2D b) {
        double cos_angle = dot(a, b) / (a.norm() * b.norm());
        if (Double.isNaN(cos_angle)) {
            return new Rotation2D();
        }
        return Rotation2D.fromRadians(Math.acos(Math.min(cos_angle, 1.0)));
    }

    public static double cross(final Translation2D a, final Translation2D b) {
        return a.x_ * b.y_ - a.y_ * b.x_;
    }

    public double distance(final Translation2D other) {
        return inverse().translateBy(other).norm();
    }

    public boolean equals(final Object other) {
        if (!(other instanceof Translation2D)) {
            return false;
        }

        return distance((Translation2D) other) < Consts.kEpsilon;
    }

    public Translation2D getTranslation() {
        return this;
    }
}