package frc.common.drive;

/**
 * This is an implementation of PR #1691
 * on WPIlib.
 */
public class DifferentialDrive {
  private boolean m_reported;

  /**
   * Arcade drive method for differential drive platform.
   *
   * <p>Note: Some drivers may prefer inverted rotation controls. This can be
   * done by negating the value passed for rotation.
   *
   * @param xSpeed        The speed at which the robot should drive along the X
   *                      axis [-1.0..1.0]. Forward is negative.
   * @param zRotation     The rotation rate of the robot around the Z axis
   *                      [-1.0..1.0]. Clockwise is positive.
   */
  public DriveSignal arcadeDrive(double xSpeed, double zRotation) {

    return new DriveSignal(xSpeed + zRotation, xSpeed - zRotation);
  }

  /**
   * Curvature drive method for differential drive platform.
   *
   * <p>The rotation argument controls the curvature of the robot's path rather
   * than its rate of heading change. This makes the robot more controllable at
   * high speeds. Also handles the robot's quick turn functionality - "quick
   * turn" overrides constant-curvature turning for turn-in-place maneuvers.
   *
   * @param xSpeed      The robot's speed along the X axis [-1.0..1.0]. Forward
   *                    is positive.
   * @param zRotation   The robot's rotation rate around the Z axis [-1.0..1.0].
   *                    Clockwise is positive.
   * @param isQuickTurn If set, overrides constant-curvature turning for
   *                    turn-in-place maneuvers.
   */
  public DriveSignal curvatureDrive(double xSpeed, double zRotation, boolean isQuickTurn) {

    DriveSignal speeds = new DriveSignal();

    if (isQuickTurn) {
      speeds.left_speed = xSpeed + zRotation;
      speeds.right_speed = xSpeed - zRotation;
    } else {
      speeds.left_speed = xSpeed + Math.abs(xSpeed) * zRotation;
      speeds.right_speed = xSpeed - Math.abs(xSpeed) * zRotation;
    }

    return speeds;
  }

  /**
   * Tank drive method for differential drive platform.
   *
   * @param leftSpeed     The robot left side's speed along the X axis
   *                      [-1.0..1.0]. Forward is positive.
   * @param rightSpeed    The robot right side's speed along the X axis
   *                      [-1.0..1.0]. Forward is positive.
   */
  public DriveSignal tankDrive(double leftSpeed, double rightSpeed) {
    return new DriveSignal(leftSpeed, rightSpeed);
  }
}