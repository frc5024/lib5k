package io.github.frc5024.lib5k.utils;

import edu.wpi.first.wpilibj.util.Color;
import io.github.frc5024.lib5k.utils.annotations.FieldTested;

@FieldTested(year = 2020)
public class ColorUtils {

    /**
     * Check if two colors are equal to eachother within epsilon
     * 
     * @param a   Color A
     * @param b   Color B
     * @param eps Acceptable range
     * @return Is equal
     */
    public static boolean epsilonEquals(Color a, Color b, double eps) {
        return (RobotMath.epsilonEquals(a.red, b.red, eps) && RobotMath.epsilonEquals(a.green, b.green, eps)
                && RobotMath.epsilonEquals(a.blue, b.blue, eps));
    }

}