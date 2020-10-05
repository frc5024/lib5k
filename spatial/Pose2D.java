// package frc.lib5k.spatial;

// import frc.lib5k.spatial.Rotation2D;
// import frc.lib5k.spatial.Translation2D;
// import frc.lib5k.utils.Consts;

// /**
//  * Represents a 2D pose (rigid transform) containing translational and
//  * rotational elements.
//  * 
//  * Adapted from 254:
//  * https://github.com/Team254/FRC-2019-Public/blob/f99f09a15a2c8e7b5df868ec3a4a81105dc88112/src/main/java/com/team254/lib/geometry/Pose2D.java
//  */
// public class Pose2D{
//     protected static final Pose2D kIdentity = new Pose2D();

//     public static Pose2D identity() {
//         return kIdentity;
//     }

//     private final static double kEps = 1E-9;

//     protected final Translation2D translation_;
//     protected final Rotation2D rotation_;

//     public Pose2D() {
//         translation_ = new Translation2D();
//         rotation_ = new Rotation2D();
//     }

//     public Pose2D(double x, double y, final Rotation2D rotation) {
//         translation_ = new Translation2D(x, y);
//         rotation_ = rotation;
//     }

//     public Pose2D(final Translation2D translation, final Rotation2D rotation) {
//         translation_ = translation;
//         rotation_ = rotation;
//     }

//     public Pose2D(final Pose2D other) {
//         translation_ = new Translation2D(other.translation_);
//         rotation_ = new Rotation2D(other.rotation_);
//     }

//     public static Pose2D fromTranslation(final Translation2D translation) {
//         return new Pose2D(translation, new Rotation2D());
//     }

//     public static Pose2D fromRotation(final Rotation2D rotation) {
//         return new Pose2D(new Translation2D(), rotation);
//     }

//     /**
//      * Obtain a new Pose2D from a (constant curvature) velocity. See:
//      * https://github.com/strasdat/Sophus/blob/master/sophus/se2.hpp
//     //  */
//     // public static Pose2D exp(final Twist2D delta) {
//     //     double sin_theta = Math.sin(delta.dtheta);
//     //     double cos_theta = Math.cos(delta.dtheta);
//     //     double s, c;
//     //     if (Math.abs(delta.dtheta) < kEps) {
//     //         s = 1.0 - 1.0 / 6.0 * delta.dtheta * delta.dtheta;
//     //         c = .5 * delta.dtheta;
//     //     } else {
//     //         s = sin_theta / delta.dtheta;
//     //         c = (1.0 - cos_theta) / delta.dtheta;
//     //     }
//     //     return new Pose2D(new Translation2D(delta.dx * s - delta.dy * c, delta.dx * c + delta.dy * s),
//     //             new Rotation2D(cos_theta, sin_theta, false));
//     // }

//     // /**
//     //  * Logical inverse of the above.
//     //  */
//     // public static Twist2D log(final Pose2D transform) {
//     //     final double dtheta = transform.getRotation().getRadians();
//     //     final double half_dtheta = 0.5 * dtheta;
//     //     final double cos_minus_one = transform.getRotation().cos() - 1.0;
//     //     double halftheta_by_tan_of_halfdtheta;
//     //     if (Math.abs(cos_minus_one) < kEps) {
//     //         halftheta_by_tan_of_halfdtheta = 1.0 - 1.0 / 12.0 * dtheta * dtheta;
//     //     } else {
//     //         halftheta_by_tan_of_halfdtheta = -(half_dtheta * transform.getRotation().sin()) / cos_minus_one;
//     //     }
//     //     final Translation2D translation_part = transform.getTranslation()
//     //             .rotateBy(new Rotation2D(halftheta_by_tan_of_halfdtheta, -half_dtheta, false));
//     //     return new Twist2D(translation_part.x(), translation_part.y(), dtheta);
//     // }

//     public Translation2D getTranslation() {
//         return translation_;
//     }

//     public Rotation2D getRotation() {
//         return rotation_;
//     }

//     /**
//      * Transforming this RigidTransform2D means first translating by
//      * other.translation and then rotating by other.rotation
//      *
//      * @param other The other transform.
//      * @return This transform * other
//      */

//     public Pose2D transformBy(final Pose2D other) {
//         return new Pose2D(translation_.translateBy(other.translation_.rotateBy(rotation_)),
//                 rotation_.rotateBy(other.rotation_));
//     }

//     /**
//      * The inverse of this transform "undoes" the effect of translating by this
//      * transform.
//      *
//      * @return The opposite of this transform.
//      */
//     public Pose2D inverse() {
//         Rotation2D rotation_inverted = rotation_.inverse();
//         return new Pose2D(translation_.inverse().rotateBy(rotation_inverted), rotation_inverted);
//     }

//     public Pose2D normal() {
//         return new Pose2D(translation_, rotation_.normal());
//     }

//     /**
//      * Finds the point where the heading of this pose intersects the heading of
//      * another. Returns (+INF, +INF) if parallel.
//      */
//     // public Translation2D intersection(final Pose2D other) {
//     //     final Rotation2D other_rotation = other.getRotation();
//     //     if (rotation_.isParallel(other_rotation)) {
//     //         // Lines are parallel.
//     //         return new Translation2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
//     //     }
//     //     if (Math.abs(rotation_.cos()) < Math.abs(other_rotation.cos())) {
//     //         return intersectionInternal(this, other);
//     //     } else {
//     //         return intersectionInternal(other, this);
//     //     }
//     // }

//     // /**
//     //  * Return true if this pose is (nearly) colinear with the another.
//     //  */
//     // public boolean isColinear(final Pose2D other) {
//     //     if (!getRotation().isParallel(other.getRotation()))
//     //         return false;
//     //     final Twist2D twist = log(inverse().transformBy(other));
//     //     return (Util.epsilonEquals(twist.dy, 0.0) && Util.epsilonEquals(twist.dtheta, 0.0));
//     // }

//     public boolean epsilonEquals(final Pose2D other, double epsilon) {
//         return getTranslation().epsilonEquals(other.getTranslation(), epsilon)
//                 && getRotation().isParallel(other.getRotation());
//     }

//     private static Translation2D intersectionInternal(final Pose2D a, final Pose2D b) {
//         final Rotation2D a_r = a.getRotation();
//         final Rotation2D b_r = b.getRotation();
//         final Translation2D a_t = a.getTranslation();
//         final Translation2D b_t = b.getTranslation();

//         final double tan_b = b_r.tan();
//         final double t = ((a_t.x() - b_t.x()) * tan_b + b_t.y() - a_t.y()) / (a_r.sin() - a_r.cos() * tan_b);
//         if (Double.isNaN(t)) {
//             return new Translation2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
//         }
//         return a_t.translateBy(a_r.toTranslation().scale(t));
//     }

//     /**
//      * Do twist interpolation of this pose assuming constant curvature.
//      */

//     public Pose2D interpolate(final Pose2D other, double x) {
//         if (x <= 0) {
//             return new Pose2D(this);
//         } else if (x >= 1) {
//             return new Pose2D(other);
//         }
//         final Twist2D twist = Pose2D.log(inverse().transformBy(other));
//         return transformBy(Pose2D.exp(twist.scaled(x)));
//     }

//     public String toString() {
//         return "T:" + translation_.toString() + ", R:" + rotation_.toString();
//     }

//     public double distance(final Pose2D other) {
//         return Pose2D.log(inverse().transformBy(other)).norm();
//     }

//     public boolean equals(final Object other) {
//         if (!(other instanceof Pose2D)) {
//             return false;
//         }

//         return epsilonEquals((Pose2D) other, Consts.kEpsilon);
//     }

//     public Pose2D getPose() {
//         return this;
//     }

//     public Pose2D mirror() {
//         return new Pose2D(new Translation2D(getTranslation().x(), -getTranslation().y()), getRotation().inverse());
//     }
// }