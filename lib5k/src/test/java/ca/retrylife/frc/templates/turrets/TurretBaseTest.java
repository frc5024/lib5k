package ca.retrylife.frc.templates.turrets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class TurretBaseTest {

    class MockTurret extends TurretBase {

        public MockTurret(Rotation2d min, Rotation2d max) {
            super(min, max);
        }

        @Override
        public void periodic() {
        }

        @Override
        public void lookAt(Rotation2d angle) {

        }

        @Override
        public boolean isAligned() {
            return true;
        }

        @Override
        public void stop() {

        }

    }
    
    @Test
    public void testNoDeadzoneOrdering() {
        
        // Create a mock turret
        MockTurret mock = new MockTurret(Rotation2d.fromDegrees(0), Rotation2d.fromDegrees(10));

        // Check that the deadzones are ordered correctly
        assertArrayEquals("Deadzone ordering", new Rotation2d[]{Rotation2d.fromDegrees(0), Rotation2d.fromDegrees(10)}, mock.deadzone);
    }

    @Test
    public void testFlippedDeadzoneOrdering() {

        // Create a mock turret
        MockTurret mock = new MockTurret(Rotation2d.fromDegrees(10), Rotation2d.fromDegrees(0));

        // Check that the deadzones are ordered correctly
        assertArrayEquals("Deadzone ordering",
                new Rotation2d[] { Rotation2d.fromDegrees(0), Rotation2d.fromDegrees(10) }, mock.deadzone);
    }
    
    @Test
    public void testWrappedDeadzoneOrdering() {

        // Create a mock turret
        MockTurret mock = new MockTurret(Rotation2d.fromDegrees(-10), Rotation2d.fromDegrees(100));

        // Check that the deadzones are ordered correctly
        assertArrayEquals("Deadzone ordering",
                new Rotation2d[] { Rotation2d.fromDegrees(-10), Rotation2d.fromDegrees(100) }, mock.deadzone);
    }
    
    @Test
    public void testAtoBCalculationCrossingBoundary() {
        // Create a mock turret
        MockTurret mock = new MockTurret(Rotation2d.fromDegrees(-100), Rotation2d.fromDegrees(120));

        assertEquals("B outside boundary", 70, mock.findDistanceFromAToB(Rotation2d.fromDegrees(-50), Rotation2d.fromDegrees(20)), MathUtils.kVerySmallNumber);        
        assertEquals("B inside boundary", 170, mock.findDistanceFromAToB(Rotation2d.fromDegrees(-50), Rotation2d.fromDegrees(130)), MathUtils.kVerySmallNumber);        
    }

    @Test
    public void testAtoBCalculationNotCrossingBoundary() {
        // Create a mock turret
        MockTurret mock = new MockTurret(Rotation2d.fromDegrees(-50), Rotation2d.fromDegrees(50));

        assertEquals("B outside boundary", 180, mock.findDistanceFromAToB(Rotation2d.fromDegrees(-80), Rotation2d.fromDegrees(100)), MathUtils.kVerySmallNumber);        
        assertEquals("B inside boundary", -50, mock.findDistanceFromAToB(Rotation2d.fromDegrees(100), Rotation2d.fromDegrees(0)), MathUtils.kVerySmallNumber);        
    }
    
}