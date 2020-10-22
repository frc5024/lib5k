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
    private Controller distanceController;
    private Controller rotationController;
    private double Kr;

    // Timer for actions
    private Timer actionTimer = new Timer();

    /**
     * Create a new DualPIDTankDriveTrain
     * 
     * @param distanceController Controller for distance control
     * @param rotationController Controller for rotation control
     */
    public DualPIDTankDriveTrain(Controller distanceController, Controller rotationController) {
        this(distanceController, rotationController, 1.0);
    }

    /**
     * Create a new DualPIDTankDriveTrain
     * 
     * @param distanceController Controller for distance control
     * @param rotationController Controller for rotation control
     */
    public DualPIDTankDriveTrain(Controller distanceController, Controller rotationController, double Kr) {
        super();

        // Set controllers
        this.distanceController = distanceController;
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
            distanceController.reset();

            // Configure the controllers
            rotationController.setEpsilon(1.0);
            rotationController.setReference(0.0);
            distanceController.setEpsilon((epsilon.getX() + epsilon.getY()) / 2);
            distanceController.setReference(0.0);

            // Reset and start the action timer
            actionTimer.reset();
            actionTimer.start();
        }

        // Get the robot's current pose
        Translation2d currentCoordinate = getPose().getTranslation();
        Rotation2d currentHeading = getPose().getRotation();

        // Get the heading error
        // Rotation2d headingError = new Rotation2d(
        // Math.atan2(currentCoordinate.getY() - goalPose.getY(),
        // currentCoordinate.getX() - goalPose.getX()))
        // .minus(currentHeading).plus(Rotation2d.fromDegrees(180.0));
        Rotation2d headingError = new Rotation2d(
                Math.atan2(goalPose.getY() - currentCoordinate.getY(), goalPose.getX() - currentCoordinate.getX()))
                        .minus(currentHeading);// .plus(Rotation2d.fromDegrees(180.0));
        // NOTE: add 180 degrees when following in reverse

        // Get the robot velocity
        double velocity = getSpeed();

        // TEMP: Gain
        double kLookaheadGain = 0.2;

        // Calculate the needed velocity to reach the goal pose
        double throttle;

        // Handle the goal being straight
        // if (RobotMath.epsilonEquals(headingError.getDegrees(), 0.0,
        // RobotMath.kVerySmallNumber)) {
        throttle = RobotMath.clamp(currentCoordinate.getDistance(goalPose), -1, 1);
        // } else {

        // // Calculate a corrective factor
        // double correctiveFactor = kLookaheadGain * velocity;

        // // Calculate a corrected throttle
        // throttle = Math.atan2(2.0 * getWidthMeters() *
        // Math.sin(headingError.getRadians()) / correctiveFactor, 1.0);
        // }

        // Handle the robot being in reverse
        if (getFrontSide().equals(Chassis.Side.kBack)) {
            throttle *= -1;
        }

        // Feed both controllers
        throttle = distanceController.calculate(throttle, 0.0);
        double steering = headingError.getRadians() * Kr;

        // Invert steering to match robot outputs
        steering *= -1;

        // Get the throttle correction factor
        double throttleCorrectiveFactor = calculateThrottleCorrectionFactor(headingError);
        throttle *= throttleCorrectiveFactor;

        // Clamp the inputs
        throttle = RobotMath.clamp(throttle, -1, 1);
        steering = RobotMath.clamp(steering, -1, 1);

        // Calculate motor outputs
        DifferentialVoltages voltages = new DifferentialVoltages(throttle + steering, throttle - steering).normalize()
                .times(12);

        logger.log(voltages.toString());

        // Write output frame
        handleVoltage(voltages.getLeftVolts(), voltages.getRightVolts());

        // // Calculate positional error
        // Translation2d error = goalPose.minus(currentPose.getTranslation());

        // // Get the hypot to determine scalar distance
        // double distanceError = currentPose.getTranslation().getDistance(goalPose) *
        // -1;
        // // Math.sqrt(Math.pow((goalPose.getX() -
        // currentPose.getTranslation().getX()),
        // // 2)
        // // + Math.pow((goalPose.getY() - currentPose.getTranslation().getY()), 2));

        // // Calculate clockwise-positive rotational error
        // Rotation2d angularError =
        // Rotation2d.fromDegrees(Math.toDegrees(Math.atan2(error.getY(), error.getX()))
        // * -1).minus(currentPose.getRotation());

        // // Calculate speed multiplier based on distance from target.
        // // This lets the robot curve towards the target, instead of snapping to it.
        // // This is a trick I learned from a programmer at 1114. It provides really
        // // smooth outputs
        // //
        // https://bitbucket.org/kaleb_dodd/simbot2019public/src/abc56f5220b5c94bca216f86e3b6b5757d0ffeff/src/main/java/frc/subsystems/Drive.java#lines-337
        // double speedMul = ((-1 * (Math.min(Math.abs(angularError.getDegrees()),
        // 90.0)) / 90.0) + 1);

        // // Calculate needed throttle
        // double throttleOutput = distanceController.calculate(distanceError);

        // // Restrict throttle output
        // throttleOutput *= speedMul;

        // // Calculate rotation PIF
        // double turnOutput = rotationController.calculate(angularError.getDegrees());

        // // Calculate motor outputs
        // DifferentialVoltages voltages =
        // DifferentialVoltages.fromThrottleAndSteering(throttleOutput, turnOutput);

        // // Write output frame
        // handleVoltage(voltages.getLeftVolts(), voltages.getRightVolts());

        // If the robot is at its goal, we are done
        if (RobotMath.epsilonEquals(currentCoordinate, goalPose, epsilon)) {
            // Reset the controllers
            rotationController.reset();
            distanceController.reset();

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
        if (distanceController != null) {
            distanceController.reset();
        }
    }

}