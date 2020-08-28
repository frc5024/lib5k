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
        MockTurret mock = new MockTurret(Rotation2d.fromDegrees(10), Rotation2d.fromDegrees(300));

        // Check that the deadzones are ordered correctly
        assertArrayEquals("Deadzone ordering", new Rotation2d[]{Rotation2d.fromDegrees(10), Rotation2d.fromDegrees(300)}, mock.deadzone);
    }
    
    @Test
    public void testEasyAngleAdjustment() {

        // Create a mock turret
        MockTurret mock = new MockTurret(Rotation2d.fromDegrees(0), Rotation2d.fromDegrees(10));

        // Check that the correct side is chosen, and shifted correctly
        assertEquals(Rotation2d.fromDegrees(0).getDegrees(), mock.adjustAngleToDegrees(Rotation2d.fromDegrees(10)), MathUtils.kVerySmallNumber);
    }

    @Test
    public void testEasyWrappedAngleAdjustment() {

        // Create a mock turret
        MockTurret mock = new MockTurret(Rotation2d.fromDegrees(-170), Rotation2d.fromDegrees(350));

        // Check that the correct side is chosen, and shifted correctly
        assertEquals(Rotation2d.fromDegrees(10).getDegrees(), mock.adjustAngleToDegrees(Rotation2d.fromDegrees(-160)), MathUtils.kVerySmallNumber);
    }
}