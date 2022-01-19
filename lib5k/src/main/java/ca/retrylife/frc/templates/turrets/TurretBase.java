package ca.retrylife.frc.templates.turrets;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
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
     * Tell the turret to look at a robot-relative point in space, where (1,1) is 45
     * degrees
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
     * Find the fastest and safest way to get from A to B. Treat this function as
     * "if I am at A, and I want to go to B, I must turn X degrees to the right"
     * 
     * @param a Current position
     * @param b Goal position
     * @return Best offset to use
     */
    protected double findDistanceFromAToB(Rotation2d a, Rotation2d b) {

        // Remap both A and B from [-180 to 180] to [0 to 360]
        double remappedA = a.getDegrees() + 180;
        double remappedB = b.getDegrees() + 180;

        // Remap both the min and max deadzone from [-180 to 180] to [0 to 360]
        double remappedMinDeadzone = deadzone[0].getDegrees() + 180;
        double remappedMaxDeadzone = deadzone[1].getDegrees() + 180;

        // Get the abs distance between both sides of the deadzone
        double deadzoneSize = Math.abs(remappedMaxDeadzone - remappedMinDeadzone);

        // The angle between the sides of the deadzone must not be 180 degrees
        if (MathUtils.epsilonEquals(deadzoneSize, 180, MathUtils.kVerySmallNumber)) {
            logger.log("The angle between the min and max of a turret's deadzone must not be exactly 180 degrees!",
                    Level.kRobot);
            throw new RuntimeException(
                    "The angle between the min and max of a turret's deadzone must not be exactly 180 degrees!");
        }

        // Determine if the deadzone crosses the 0/360 degree border
        boolean deadzoneCrossesBoundary = deadzoneSize > 180;

        // If the deadzone crosses the boundray
        if (deadzoneCrossesBoundary) {

            // Check if B is not in the zone between min and max
            if (remappedB > remappedMinDeadzone && remappedB < remappedMaxDeadzone) {
                // Return the distance to get to B
                return remappedB - remappedA;
            } else {
                // B is not a valid goal position (it lands inside the deadzone)
                // Return the deadzone boundary closest to b
                return ((Math.abs(remappedB - remappedMinDeadzone) >= Math.abs(remappedB - remappedMaxDeadzone))
                        ? remappedMaxDeadzone
                        : remappedMinDeadzone) - remappedA;
            }
        } else {
            // Check if B is in the zone between min and max
            if (remappedB > remappedMinDeadzone && remappedB < remappedMaxDeadzone) {
                // B is not a valid goal position (it lands inside the deadzone)
                // Return the deadzone boundary closest to b
                return MathUtils.getWrappedError(remappedA,
                        (Math.abs(remappedB - remappedMinDeadzone) >= Math.abs(remappedB - remappedMaxDeadzone))
                                ? remappedMaxDeadzone
                                : remappedMinDeadzone)
                        * -1;
            } else {
                // Return the sum of distances between A, B, and the 0/360 boundary
                return MathUtils.getWrappedError(remappedA, remappedB);
            }
        }

    }

}