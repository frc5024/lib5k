package io.github.frc5024.lib5k.bases.drivetrain.implementations;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import io.github.frc5024.lib5k.bases.drivetrain.Chassis;
import io.github.frc5024.lib5k.control_loops.base.Controller;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.math.RotationMath;
import io.github.frc5024.lib5k.utils.RobotMath;
import io.github.frc5024.lib5k.utils.types.DifferentialVoltages;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * DualPIDTankDriveTrain is a TankDriveTrain implementation that uses two PID
 * controllers to control autonomous robot movement
 */
public abstract class DualPIDTankDriveTrain extends TankDriveTrain {

    // Control loops
    private Controller rotationController;
    private double Kr;

    // Timer for actions
    private Timer actionTimer = new Timer();

    /**
     * Create a new DualPIDTankDriveTrain
     * 
     * @param rotationController Controller for rotation control
     */
    public DualPIDTankDriveTrain(Controller rotationController) {
        this(rotationController, 0.5);
    }

    /**
     * Create a new DualPIDTankDriveTrain
     * 
     * @param rotationController Controller for rotation control
     * @param Kr                 Rotational P gain for path following
     */
    public DualPIDTankDriveTrain(Controller rotationController, double Kr) {
        super();

        // Set controllers
        this.rotationController = rotationController;
        this.Kr = Kr;
    }

    @Override
    protected void handleAutonomousRotation(StateMetadata<State> meta, Rotation2d goalHeading, Rotation2d epsilon) {

        if (meta.isFirstRun()) {

            logger.log("Switched to rotation control");
            logger.log(String.format("Turning to %s with epsilon of %s", goalHeading.toString(), epsilon.toString()));

            // Reset the controller
            rotationController.reset();

            // Configure the controller
            rotationController.setEpsilon(epsilon.getDegrees());
            rotationController.setReference(0);

            // Reset and start the action timer
            actionTimer.reset();
            actionTimer.start();
        }

        // Get the current heading
        Rotation2d currentHeading = getPose().getRotation();

        // Determine the error from the goal
        double error = RotationMath.getAngularErrorDegrees(currentHeading, goalHeading);

        // Calculate the needed output
        double output = rotationController.calculate(error) * RR_HAL.MAXIMUM_BUS_VOLTAGE * super.maxSpeedPercent;

        // Write voltage outputs
        handleVoltage(output, -output);

        // Handle reaching the goal heading
        if (rotationController.atReference()) {

            // Reset the controller
            rotationController.reset();

            // Stop the motors
            handleVoltage(0, 0);
            actionTimer.stop();

            // Switch to open loop control
            setOpenLoop(new DifferentialVoltages());

            // Log success
            logger.log(String.format("Reached heading goal after %.2f seconds", actionTimer.get()));
        }

    }

    @Override
    protected void handleDrivingToPose(StateMetadata<State> meta, Translation2d goalPose, Translation2d epsilon) {

        if (meta.isFirstRun()) {

            logger.log("Switched to pose control");
            logger.log(String.format("Driving to pose: %s", goalPose));

            // Reset the controllers
            rotationController.reset();

            // Configure the controllers
            rotationController.setEpsilon(1.0);
            rotationController.setReference(0.0);

            // Reset and start the action timer
            actionTimer.reset();
            actionTimer.start();
        }

        // Get the robot's current pose
        Translation2d currentCoordinate = getPose().getTranslation();
        Rotation2d currentHeading = getPose().getRotation();

        // Get the heading error
        Rotation2d headingError = new Rotation2d(
                Math.atan2(goalPose.getY() - currentCoordinate.getY(), goalPose.getX() - currentCoordinate.getX()))
                        .minus(currentHeading);

        // Calculate the needed velocity to reach the goal pose
        double throttle = RobotMath.clamp(currentCoordinate.getDistance(goalPose), -1, 1);

        // Handle the robot being in reverse
        if (getFrontSide().equals(Chassis.Side.kBack)) {
            throttle *= -1;
        }

        // Calculate the needed steering value
        double steering = headingError.getRadians();
        steering /= (Math.PI / 2);
        steering *= Kr;

        // Get the throttle correction factor and correct
        double throttleCorrectiveFactor = calculateThrottleCorrectionFactor(headingError);
        throttle *= throttleCorrectiveFactor;

        // Clamp the inputs
        throttle = RobotMath.clamp(throttle, -1, 1);
        steering = RobotMath.clamp(steering, -1, 1);

        // Calculate motor outputs
        DifferentialVoltages voltages = new DifferentialVoltages(throttle + steering, throttle - steering).normalize()
                .times(12);

        // This line is rather helpful for debugging:
        // logger.log(String.format("%.2f | %.2f | %.2f, %.2f | %s",
        // currentHeading.getDegrees(), headingError.getDegrees(),
        // currentCoordinate.getX(), currentCoordinate.getY(), voltages.toString()));

        // Write output frame
        handleVoltage(voltages.getLeftVolts(), voltages.getRightVolts());

        // If the robot is at its goal, we are done
        if (RobotMath.epsilonEquals(currentCoordinate, goalPose, epsilon)) {
            // Reset the controllers
            rotationController.reset();

            // Stop the motors
            handleVoltage(0, 0);
            actionTimer.stop();

            // Switch to open loop control
            setOpenLoop(new DifferentialVoltages());

            // Log success
            logger.log(String.format("Reached pose goal after %.2f seconds", actionTimer.get()));
        }

    }

    @Override
    public void reset() {
        super.reset();

        // Reset controllers
        if (rotationController != null) {
            rotationController.reset();
        }
    }

}