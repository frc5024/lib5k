package ca.retrylife.frc.templates.turrets;

import java.util.function.Supplier;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import io.github.frc5024.lib5k.control_loops.models.PIDProfile;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.utils.ObjectCounter;
import io.github.frc5024.lib5k.utils.PoseRelation;
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
    private SpeedController motor;
    private Supplier<Rotation2d> turretAngleSupplier;

    // Configuration
    private Rotation2d epsilon;

    // Controllers
    private ProfiledPIDController pidController;

    // System goal
    private double goal = 0.0;

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
     * @param characteristics     Characteristics of the turret's movement
     * @param epsilon             Turret angle epsilon
     */
    public ProfiledTurret(SpeedController motor, Supplier<Rotation2d> turretAngleSupplier, PIDProfile turretPID,
            Rotation2d minDeadzoneAngle, Rotation2d maxDeadzoneAngle, TrapezoidProfile.Constraints characteristics,
            Rotation2d epsilon) {
        super(minDeadzoneAngle, maxDeadzoneAngle);

        // Set locals
        this.motor = motor;
        this.turretAngleSupplier = turretAngleSupplier;
        this.epsilon = epsilon;

        // Set up PID profile controller
        this.pidController = new ProfiledPIDController(turretPID.kp, turretPID.ki, turretPID.kd, characteristics);

        // Set up state machine
        String name = String.format("ProfiledTurret[%d]", objCounter.getNewID());
        this.stateMachine = new StateMachine<>(name);

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

            // Set the PID goal
            pidController.setGoal(new TrapezoidProfile.State(goal, 0.0));

        }
        
        // Get the current angle, and adjust
        double adjCurrentAngle = super.adjustAngleToDegrees(turretAngleSupplier.get());

        // Check if we are at our goal
        if (MathUtils.epsilonEquals(adjCurrentAngle, goal, Math.abs(epsilon.getDegrees()))) {
            stateMachine.setState(SystemState.kHoldingAngle);
            return;
        }

        // Get the PID output
        double output = pidController.calculate(adjCurrentAngle);

        // Do not allow the motor to move if it crosses a deadzone boundry
        if (adjCurrentAngle <= 0 && output < 0) {
            output = 0.0;
        }
        if (adjCurrentAngle >= super.adjustAngleToDegrees(super.deadzone[1]) && output > 0) {
            output = 0.0;
        }

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
        }
        
        // Get the current angle, and adjust
        double adjCurrentAngle = super.adjustAngleToDegrees(turretAngleSupplier.get());

        // Check if we are not at our goal
        if (!MathUtils.epsilonEquals(adjCurrentAngle, goal, Math.abs(epsilon.getDegrees()))) {
            stateMachine.setState(SystemState.kFacingAngle);
            return;
        }

    }

    @Override
    public void lookAt(Rotation2d angle) {
        this.goal = super.adjustAngleToDegrees(angle);
    }

    @Override
    public boolean isAligned() {
        return this.stateMachine.getCurrentState().equals(SystemState.kHoldingAngle) || this.stateMachine.getCurrentState().equals(SystemState.kIdle);
    }

    @Override
    public void stop() {
        motor.set(0.0);
        stateMachine.setState(SystemState.kIdle);
    }

}