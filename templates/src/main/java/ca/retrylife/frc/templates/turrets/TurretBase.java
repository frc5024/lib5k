package ca.retrylife.frc.templates.turrets;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

/**
 * TurretBase is the base class for all turret templates. It contains common
 * methods, and utilities
 */
public abstract class TurretBase extends SubsystemBase {
    private RobotLogger logger = RobotLogger.getInstance();

    protected Rotation2d[] deadzone;

    /**
     * Create a TurretBase with a deadzone
     * 
     * @param minDeadzoneAngle Robot-relative angle where the turret deadzone starts
     * @param maxDeadzoneAngle Robot-relative angle where the turret deadzone ends
     */
    public TurretBase(Rotation2d minDeadzoneAngle, Rotation2d maxDeadzoneAngle) {

        // Correctly order the min and max angles (in case the programmer can't read)
        this.deadzone = (MathUtils.epsilonEquals(Math.min(minDeadzoneAngle.getDegrees(), maxDeadzoneAngle.getDegrees()),
                minDeadzoneAngle.getDegrees(), MathUtils.kVerySmallNumber))
                        ? new Rotation2d[] { minDeadzoneAngle, maxDeadzoneAngle }
                        : new Rotation2d[] { maxDeadzoneAngle, minDeadzoneAngle };

    }

    @Override
    public abstract void periodic();

    /**
     * Tell the turret to look at a robot-relative angle
     * 
     * @param angle Robot-relative angle
     */
    public abstract void lookAt(Rotation2d angle);

    /**
     * Tell the turret to look at a robot-relative point in space, where (1,1) is 45 degrees
     * 
     * @param point Robot-relative point in space
     */
    public void lookAt(Translation2d point) {
        lookAt(new Rotation2d(Math.atan2(point.getY(), point.getX()) * -1));
    }

    /**
     * Get if the turret is aligned with its goal
     * 
     * @return Is the turret aligned?
     */
    public abstract boolean isAligned();

    /**
     * Stop the turret
     */
    public abstract void stop();

    /**
     * Adjust any given angle to a new numbering system that makes PID calculation
     * easier
     * 
     * @param angle Angle
     * @return New angle in adjusted degrees
     */
    protected double adjustAngleToDegrees(Rotation2d angle) {

        // Shift the angle so that -180 is now 0 and +180 is now 360
        double shiftedAngle = angle.getDegrees() + 180;

        // Get the min and max of the deadzone and shift them too
        double deadMin = deadzone[0].getDegrees() + 180;
        double deadMax = deadzone[1].getDegrees() + 180;

        // Get the abs distance between both sides of the deadzone
        double deadzoneSize = Math.abs(deadMax - deadMin);

        // The angle between the sides of the deadzone must not be 180 degrees
        if (MathUtils.epsilonEquals(deadzoneSize, 180, MathUtils.kVerySmallNumber)) {
            logger.log("The angle between the min and max of a turret's deadzone must not be exactly 180 degrees!",
                    Level.kRobot);
            throw new RuntimeException(
                    "The angle between the min and max of a turret's deadzone must not be exactly 180 degrees!");
        }

        // Determine the base angle
        // This is the angle that is furthest clockwise if you were to place the range
        // 0-360 in a circle
        double baseAngle = (deadzoneSize > 180) ? deadMin : deadMax;
        double limit = (deadzoneSize < 180) ? deadMin : deadMax;

        // Subtract the base angle from the desired angle to make the base equal to 0
        shiftedAngle = shiftedAngle - baseAngle;

        // Do not allow the setpoint to enter the deadzone
        if (shiftedAngle > 360 - limit) {
            shiftedAngle = limit;
        }

        return shiftedAngle;
    }

}