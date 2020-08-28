package ca.retrylife.frc.templates.turrets;

import static org.junit.Assert.assertEquals;

import java.util.function.Supplier;

import org.junit.Test;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import io.github.frc5024.lib5k.control_loops.models.PIDProfile;
import io.github.frc5024.lib5k.hardware.generic.motors.MockSpeedController;
import io.github.frc5024.lib5k.logging.RobotLogger;

public class ProfiledTurretTest {

    // I/O
    private MockSpeedController motor = new MockSpeedController();
    private Rotation2d angle = new Rotation2d();
    private Supplier<Rotation2d> angleGetter = () -> {
        return angle;
    };
    private PIDProfile pidGains = new PIDProfile(0.5);
    private Rotation2d minDeadzone = Rotation2d.fromDegrees(-100);
    private Rotation2d maxDeadzone = Rotation2d.fromDegrees(100);
    private TrapezoidProfile.Constraints characteristics = new TrapezoidProfile.Constraints(1.0, 1.0);
    private Rotation2d epsilon = Rotation2d.fromDegrees(2.0);

    @Test
    public void testTurretMovesInCorrectDirection() {

        // Reset I/O
        motor.set(0.0);
        angle = Rotation2d.fromDegrees(0.0);

        // Create a ProfiledTurret
        ProfiledTurret turret = new ProfiledTurret(motor, angleGetter, pidGains, minDeadzone, maxDeadzone,
                characteristics, epsilon);
        RobotLogger.getInstance().flush();

        // Set a sentpoint for moving left
        turret.lookAt(Rotation2d.fromDegrees(-10.0));

        // Run an interation
        turret.periodic();
        RobotLogger.getInstance().flush();

        // Check that the turret is now moving left
        assertEquals("Turret moving left", true, motor.get() < 0.0);

        // Stop
        turret.stop();

        // Run an interation
        turret.periodic();
        RobotLogger.getInstance().flush();

        // Set a sentpoint for moving right
        turret.lookAt(Rotation2d.fromDegrees(10.0));

        // Run an interation
        turret.periodic();
        RobotLogger.getInstance().flush();

        // Check that the turret is now moving right
        assertEquals("Turret moving right", true, motor.get() > 0.0);
    }

    @Test
    public void testTurretAvoidsDeadzone() {

    }

    @Test
    public void testTurretStopsAtGoal() {

    }

    @Test
    public void testTurretHandlesDriftAfterMoving() {

    }
}