package io.github.frc5024.lib5k.utils.algorithmic;

import java.util.Objects;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

/**
 * MutableTranslation is a class only for use in heavy mathematical
 * calculations. It imitates a
 * {@link edu.wpi.first.wpilibj.geometry.Translation2d} object, but is much more
 * GC-friendly.
 */
public class MutableTranslation {

    private double x;
    private double y;

    /**
     * Create a MutableTranslation from a Translation2d
     * 
     * @param other Translation2d
     */
    public MutableTranslation(Translation2d other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    /**
     * Convert back to a Translation2d
     * 
     * @return Translation2d clone
     */
    public Translation2d getAsTranslation2d() {
        return new Translation2d(x, y);
    }

    /**
     * Calculates the distance between two translations in 2d space.
     *
     * <p>
     * This function uses the pythagorean theorem to calculate the distance.
     * distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
     *
     * @param other The translation to compute the distance to.
     * @return The distance between the two translations.
     */
    public double getDistance(MutableTranslation other) {
        return Math.hypot(other.x - x, other.y - y);
    }

    /**
     * Calculates the distance between two translations in 2d space.
     *
     * <p>
     * This function uses the pythagorean theorem to calculate the distance.
     * distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
     *
     * @param other The translation to compute the distance to.
     * @return The distance between the two translations.
     */
    public double getDistance(Translation2d other) {
        return Math.hypot(other.getX() - x, other.getY() - y);
    }

    /**
     * Returns the X component of the translation.
     *
     * @return The x component of the translation.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y component of the translation.
     *
     * @return The y component of the translation.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the norm, or distance from the origin to the translation.
     *
     * @return The norm of the translation.
     */
    public double getNorm() {
        return Math.hypot(x, y);
    }

    /**
     * Applies a rotation to the translation in 2d space.
     *
     * <p>
     * This multiplies the translation vector by a counterclockwise rotation matrix
     * of the given angle. [x_new] [other.cos, -other.sin][x] [y_new] = [other.sin,
     * other.cos][y]
     *
     * <p>
     * For example, rotating a MutableTranslation of {2, 0} by 90 degrees will
     * result in a MutableTranslation of {0, 2}.
     *
     * @param other The rotation to rotate the translation by.
     */
    public void rotateBy(Rotation2d other) {
        this.x = x * other.getCos() - y * other.getSin();
        this.y = x * other.getSin() + y * other.getCos();
    }

    /**
     * Add another translation to this one
     * 
     * @param other Other translation
     */
    public void plus(MutableTranslation other) {
        this.x += other.x;
        this.y += other.y;
    }

    /**
     * Add another translation to this one
     * 
     * @param other Other translation
     */
    public void plus(Translation2d other) {
        this.x += other.getX();
        this.y += other.getY();
    }

    /**
     * Subtracts another translation from this one
     * 
     * @param other Other translation
     */
    public void minus(MutableTranslation other) {
        this.x -= other.x;
        this.y -= other.y;
    }

    /**
     * Subtracts another translation from this one
     * 
     * @param other Other translation
     */
    public void minus(Translation2d other) {
        this.x -= other.getX();
        this.y -= other.getY();
    }

    /**
     * This is equivalent to rotating by 180 degrees, flipping the point over both
     * axes, or simply negating both components of the translation.
     */
    public void unaryMinus() {
        this.x *= -1;
        this.y *= -1;
    }

    /**
     * Multiply this by a scalar
     * 
     * @param scalar Scalar
     */
    public void times(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    /**
     * Divides this by a scalar
     * 
     * @param scalar Scalar
     */
    public void div(double scalar) {
        this.x /= scalar;
        this.y /= scalar;
    }

    @Override
    public String toString() {
        return String.format("MutableTranslation(X: %.2f, Y: %.2f)", x, y);
    }

    /**
     * Checks equality between this MutableTranslation and another object.
     *
     * @param obj The other object.
     * @return Whether the two objects are equal or not.
     */
    public boolean equals(Object obj) {
        if (obj instanceof MutableTranslation) {
            return Math.abs(((MutableTranslation) obj).x - x) < 1E-9
                    && Math.abs(((MutableTranslation) obj).y - y) < 1E-9;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}