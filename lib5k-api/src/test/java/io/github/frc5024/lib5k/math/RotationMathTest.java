package io.github.frc5024.lib5k.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import io.github.frc5024.lib5k.utils.RobotMath;

public class RotationMathTest {

    @Test
    public void testAngularError() {

        // Calculate error
        double error = RotationMath.getAngularErrorDegrees(Rotation2d.fromDegrees(-10), Rotation2d.fromDegrees(10));

        assertEquals("Error", 20, error, RobotMath.kVerySmallNumber);
        
    }

    @Test
    public void testWrappedAngularError() {

        // Calculate error
        double error = RotationMath.getAngularErrorDegrees(Rotation2d.fromDegrees(-170), Rotation2d.fromDegrees(170));

        assertEquals("Error", -20, error, RobotMath.kVerySmallNumber);
        
    }
    
}