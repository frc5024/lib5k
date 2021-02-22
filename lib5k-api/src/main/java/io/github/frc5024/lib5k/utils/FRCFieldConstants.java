package io.github.frc5024.lib5k.utils;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Transform2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.util.Units;

/**
 * Standard FRC field measurements. Anything year-specific should be added as
 * needed
 */
public class FRCFieldConstants {

    public static final Translation2d FIELD_SIZE = new Translation2d(Units.inchesToMeters(629.25),
            Units.inchesToMeters(323.25));
    public static final Translation2d HALF_FIELD_SIZE = FIELD_SIZE.div(2);
    public static final Translation2d HALF_FIELD_WIDTH = new Translation2d(0, HALF_FIELD_SIZE.getY());
    public static final Translation2d HALF_FIELD_LENGTH = new Translation2d(HALF_FIELD_SIZE.getX(), 0);

    // Transformation that will convert a Lib5K coordinate to a WPILib coordinate
    public static final Transform2d LIB5K_TO_WPILIB_COORDINATE_TRANSFORM = new Transform2d(HALF_FIELD_WIDTH, new Rotation2d());

}