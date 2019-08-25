package frc.lib5k.spatial;

import frc.lib5k.utils.Consts;
import java.lang.Math;
import frc.lib5k.spatial.Translation2D;

/**
 * A rotation in a 2d coordinate frame represented a point on the unit circle
 * (cosine and sine).
 * 
 * Adapted from 254:
 * https://github.com/Team254/FRC-2019-Public/blob/f99f09a15a2c8e7b5df868ec3a4a81105dc88112/src/main/java/com/team254/lib/geometry/Rotation2d.java
 */
public class Rotation2D {
    protected static final Rotation2D kIdentity = new Rotation2D();

    public static Rotation2D identity() {
        return kIdentity;
    }

    protected double cos_angle_ = Double.NaN;
    protected double sin_angle_ = Double.NaN;
    protected double radians_ = Double.NaN;

    protected Rotation2D(double x, double y, double radians) {
        cos_angle_ = x;
        sin_angle_ = y;
        radians_ = radians;
    }

    public Rotation2D() {
        this(1.0, 0.0, 0.0);
    }

    public Rotation2D(double radians, boolean normalize) {
        if (normalize) {
            radians = WrapRadians(radians);
        }
        radians_ = radians;
    }

    public Rotation2D(double x, double y, boolean normalize) {
        if (normalize) {
            // From trig, we know that sin^2 + cos^2 == 1, but as we do math on this object
            // we might accumulate rounding errors.
            // Normalizing forces us to re-scale the sin and cos to reset rounding errors.
            double magnitude = Math.hypot(x, y);
            if (magnitude > Consts.kEpsilon) {
                sin_angle_ = y / magnitude;
                cos_angle_ = x / magnitude;
            } else {
                sin_angle_ = 0.0;
                cos_angle_ = 1.0;
            }
        } else {
            cos_angle_ = x;
            sin_angle_ = y;
        }
    }

    public Rotation2D(final Rotation2D other) {
        cos_angle_ = other.cos_angle_;
        sin_angle_ = other.sin_angle_;
        radians_ = other.radians_;
    }

    public Rotation2D(final Translation2D direction, boolean normalize) {
        this(direction.x(), direction.y(), normalize);
    }

    public static Rotation2D fromRadians(double angle_radians) {
        return new Rotation2D(angle_radians, true);
    }

    public static Rotation2D fromDegrees(double angle_degrees) {
        return fromRadians(Math.toRadians(angle_degrees));
    }

    public double cos() {
        ensureTrigComputed();
        return cos_angle_;
    }

    public double sin() {
        ensureTrigComputed();
        return sin_angle_;
    }

    public double tan() {
        ensureTrigComputed();
        if (Math.abs(cos_angle_) < Consts.kEpsilon) {
            if (sin_angle_ >= 0.0) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }
        return sin_angle_ / cos_angle_;
    }

    public double getRadians() {
        ensureRadiansComputed();
        return radians_;
    }

    public double getDegrees() {
        return Math.toDegrees(getRadians());
    }

    /**
     * We can rotate this Rotation2D by adding together the effects of it and
     * another rotation.
     *
     * @param other The other rotation. See:
     *              https://en.wikipedia.org/wiki/Rotation_matrix
     * @return This rotation rotated by other.
     */
    public Rotation2D rotateBy(final Rotation2D other) {
        if (hasTrig() && other.hasTrig()) {
            return new Rotation2D(cos_angle_ * other.cos_angle_ - sin_angle_ * other.sin_angle_,
                    cos_angle_ * other.sin_angle_ + sin_angle_ * other.cos_angle_, true);
        } else {
            return fromRadians(getRadians() + other.getRadians());
        }
    }

    public Rotation2D normal() {
        if (hasTrig()) {
            return new Rotation2D(-sin_angle_, cos_angle_, false);
        } else {
            return fromRadians(getRadians() - Math.PI / 2.0);
        }
    }

    /**
     * The inverse of a Rotation2D "undoes" the effect of this rotation.
     *
     * @return The opposite of this rotation.
     */
    public Rotation2D inverse() {
        if (hasTrig()) {
            return new Rotation2D(cos_angle_, -sin_angle_, false);
        } else {
            return fromRadians(-getRadians());
        }
    }

    // public boolean isParallel(final Rotation2D other) {
    //     if (hasRadians() && other.hasRadians()) {
    //         return Util.epsilonEquals(radians_, other.radians_)
    //                 || Util.epsilonEquals(radians_, WrapRadians(other.radians_ + Math.PI));
    //     } else if (hasTrig() && other.hasTrig()) {
    //         return Util.epsilonEquals(sin_angle_, other.sin_angle_) && Util.epsilonEquals(cos_angle_, other.cos_angle_);
    //     } else {
    //         // Use public, checConsts.kEd version.
    //         return Util.epsilonEquals(getRadians(), other.getRadians())
    //                 || Util.epsilonEquals(radians_, WrapRadians(other.radians_ + Math.PI));
    //     }
    // }

    public Translation2D toTranslation() {
        ensureTrigComputed();
        return new Translation2D(cos_angle_, sin_angle_);
    }

    protected double WrapRadians(double radians) {
        final double k2Pi = 2.0 * Math.PI;
        radians = radians % k2Pi;
        radians = (radians + k2Pi) % k2Pi;
        if (radians > Math.PI)
            radians -= k2Pi;
        return radians;
    }

    private boolean hasTrig() {
        return !Double.isNaN(sin_angle_) && !Double.isNaN(cos_angle_);
    }

    private boolean hasRadians() {
        return !Double.isNaN(radians_);
    }

    private void ensureTrigComputed() {
        if (!hasTrig()) {
            if (Double.isNaN(radians_)) {
                System.err.println("HEY");
            }
            sin_angle_ = Math.sin(radians_);
            cos_angle_ = Math.cos(radians_);
        }
    }

    private void ensureRadiansComputed() {
        if (!hasRadians()) {
            if (Double.isNaN(cos_angle_) || Double.isNaN(sin_angle_)) {
                System.err.println("HEY");
            }
            radians_ = Math.atan2(sin_angle_, cos_angle_);
        }
    }

    
    public Rotation2D interpolate(final Rotation2D other, double x) {
        if (x <= 0.0) {
            return new Rotation2D(this);
        } else if (x >= 1.0) {
            return new Rotation2D(other);
        }
        double angle_diff = inverse().rotateBy(other).getRadians();
        return this.rotateBy(Rotation2D.fromRadians(angle_diff * x));
    }

    
    public double distance(final Rotation2D other) {
        return inverse().rotateBy(other).getRadians();
    }

    
    public boolean equals(final Object other) {
        if (!(other instanceof Rotation2D)) {
            return false;
        }

        return distance((Rotation2D) other) < Consts.kEpsilon;
    }

    
    public Rotation2D getRotation() {
        return this;
    }
}