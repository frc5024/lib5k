package io.github.frc5024.lib5k.utils;

import java.util.HashMap;

import edu.wpi.first.wpiutil.CircularBuffer;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

import ca.retrylife.ewmath.MathUtils;


/**
 * RobotMath is an extension of MathUtils, with some overloads for robot-specific datatypes (the ewmath library is designed for general use, so excludes these functions)
 */
public class RobotMath extends MathUtils{

    public static void main(String[] args) {
        System.out.println(getWrappedError(270, 0.0));
        System.out.println(getWrappedError(0.0, 270.0));
        System.out.println(wpiAngleTo5k(-90));
        System.out.println(wpiAngleTo5k(90));
    }

    /**
     * Convert from the [-180-180] angles used by WPILib to the [0-360] angles used
     * by lib5k
     * 
     * @param angle Angle
     * @return Angle
     */
    public static double wpiAngleTo5k(double angle) {
        if (angle < 0) {
            return 360 + angle;
        } else {
            return angle;
        }

    }

    public static boolean epsilonEquals(Pose2d a, Pose2d b, Pose2d epsilon) {
        return (epsilonEquals(a.getTranslation().getX(), b.getTranslation().getX(), epsilon.getTranslation().getX()))
                && (epsilonEquals(a.getTranslation().getY(), b.getTranslation().getY(),
                        epsilon.getTranslation().getY()))
                && (epsilonEquals(a.getRotation().getDegrees(), b.getRotation().getDegrees(),
                        epsilon.getRotation().getDegrees()));
    }

    public static boolean epsilonEquals(Translation2d a, Translation2d b, Translation2d epsilon) {
        return (epsilonEquals(a.getX(), b.getX(), epsilon.getX()))
                && (epsilonEquals(a.getY(), b.getY(), epsilon.getY()));
    }

    public static int mode(CircularBuffer array, int size) {
        HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
        int max = 1;
        int temp = 0;

        for (int i = 0; i < size; i++) {

            if (hm.get((int) array.get(i)) != null) {

                int count = hm.get((int) array.get(i));
                count++;
                hm.put((int) array.get(i), count);

                if (count > max) {
                    max = count;
                    temp = (int) array.get(i);
                }
            }

            else
                hm.put((int) array.get(i), 1);
        }
        return temp;
    }

}