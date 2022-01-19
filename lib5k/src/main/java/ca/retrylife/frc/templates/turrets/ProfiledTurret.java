package ca.retrylife.frc.templates.turrets;

import java.util.function.Supplier;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.utils.ObjectCounter;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * The ProfiledTurret class is to be used to control a turret of any kind. This
 * specifically is designed for turrets that do not support infinite rotation,
 * and will use motion profiling to plan the quickest path around a turret's
 * deadband zone.
 */
public class ProfiledTurret extends TurretBase {
    private RobotLogger logger = RobotLogger.getInstance();

    // System States
    private enum SystemState {
        kIdle, // Stopped, doing nothing
        kFacingAngle, // Facing a robot-relative angle
        kHoldingAngle; // Holding the current angle
    }

    // State machine
    private static ObjectCounter objCounter = new ObjectCounter();
    private StateMachine<SystemState> stateMachine;

    // System I/O
    private MotorController motor;
    private Supplier<Rotation2d> turretAngleSupplier;

    // Configuration
    private Rotation2d epsilon;

    // Controllers
    private ProfiledPIDController pidController;

    // System goal
    private Rotation2d goal = new Rotation2d();

    /**
     * Create a ProfiledTurret
     * 
     * @param motor               Turret motor controller(s)
     * @param turretAngleSupplier Supplier of the turret's robot-relative angle
     * @param turretPID           PID gain constants for the turret
     * @param minDeadzoneAngle    Robot-relative angle where the turret deadzone
     *                            starts
     * @param maxDeadzoneAngle    Robot-relative angle where the turret deadzone
     *                            ends
     * @param epsilon             Turret angle epsilon
     */
    public ProfiledTurret(MotorController motor, Supplier<Rotation2d> turretAngleSupplier, ProfiledPIDController turretPID,
            Rotation2d minDeadzoneAngle, Rotation2d maxDeadzoneAngle, 
            Rotation2d epsilon) {
        super(minDeadzoneAngle, maxDeadzoneAngle);

        // Set locals
        this.motor = motor;
        this.turretAngleSupplier = turretAngleSupplier;
        this.epsilon = epsilon;

        // Set up PID profile controller
        this.pidController = turretPID;

        // Set up state machine
        String name = String.format("ProfiledTurret[%d]", objCounter.getNewID());
        this.stateMachine = new StateMachine<>(name);
        this.stateMachine.setDefaultState(SystemState.kIdle, this::handleIdle);
        this.stateMachine.addState(SystemState.kFacingAngle, this::handleFacingAngle);
        this.stateMachine.addState(SystemState.kHoldingAngle, this::handleHoldingAngle);

    }

    @Override
    public void periodic() {
        // Update the state machine
        stateMachine.update();
    }

    /**
     * Handle the system being idle
     * 
     * @param meta State metadata
     */
    private void handleIdle(StateMetadata<SystemState> meta) {

        if (meta.isFirstRun()) {
            logger.log("Became idle");
            motor.set(0.0);
        }

    }

    /**
     * Handle the turret facing an angle
     * 
     * @param meta State metadata
     */
    private void handleFacingAngle(StateMetadata<SystemState> meta) {

        if (meta.isFirstRun()) {
            logger.log("Starting to face set angle");

            // Set the PID goal to be 0.0 degrees from the goal at 00 RPM
            pidController.setGoal(new TrapezoidProfile.State(0.0, 0.0));

        }

        // Get the current angle
        Rotation2d currentAngle = turretAngleSupplier.get();

        // Determine the distance to the goal
        double distToGoal = super.findDistanceFromAToB(currentAngle, goal);

        // Check if we are at our goal
        if (MathUtils.epsilonEquals(distToGoal, 0.0, Math.abs(epsilon.getDegrees()))) {
            stateMachine.setState(SystemState.kHoldingAngle);
            return;
        }

        // Get the PID output
        // Input is flipped because of some WPILib issues
        double output = pidController.calculate(distToGoal * -1);

        // NOTE: If you were to be building a system that uses limit switches at the
        // deadzone, that code should go right here.

        // Set the motor output
        motor.set(output);

    }

    /**
     * Handle the turret holding an angle
     * 
     * @param meta State metadata
     */
    private void handleHoldingAngle(StateMetadata<SystemState> meta) {

        if (meta.isFirstRun()) {
            logger.log("Holding setpoint angle");
            motor.set(0.0);
        }

        // Get the current angle
        Rotation2d currentAngle = turretAngleSupplier.get();

        // Determine the distance to the goal
        double distToGoal = super.findDistanceFromAToB(currentAngle, goal);

        // Check if we are not at our goal
        if (!MathUtils.epsilonEquals(distToGoal, 0.0, Math.abs(epsilon.getDegrees()))) {
            stateMachine.setState(SystemState.kFacingAngle);
            return;
        }

    }

    @Override
    public void lookAt(Rotation2d angle) {
        logger.log("Now looking at: %s", angle);
        this.goal = angle;
        this.stateMachine.setState(SystemState.kFacingAngle);
    }

    @Override
    public boolean isAligned() {
        return this.stateMachine.getCurrentState().equals(SystemState.kHoldingAngle)
                || this.stateMachine.getCurrentState().equals(SystemState.kIdle);
    }

    @Override
    public void stop() {
        motor.set(0.0);
        stateMachine.setState(SystemState.kIdle);
    }

}