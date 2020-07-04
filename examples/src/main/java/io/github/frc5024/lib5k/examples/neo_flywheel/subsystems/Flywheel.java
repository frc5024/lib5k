package io.github.frc5024.lib5k.examples.neo_flywheel;

import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.control_loops.models.DCBrushedMotor;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.ExtendedSparkMax;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * This is the flywheel subsystem. It needs to do the following:
 * 
 * <ul>
 * <li>Allow other classes to set the "goal RPM"</li>
 * <li>Quickly reach, and maintain that speed</li>
 * </ul>
 * 
 * This example is build around the RevRobotics NEO brushless motor.
 * https://www.revrobotics.com/rev-21-1650/
 * 
 */
public class Flywheel extends SubsystemBase {
    // Get a logger instance
    private RobotLogger logger = RobotLogger.getInstance();

    // Every subsystem needs an instance
    // For info on how instances work, read about Java's "Singleton design pattern"
    private static Flywheel instance;

    public static Flywheel getInstance() {
        if (instance == null) {
            instance = new Flywheel();
        }
        return instance;
    }

    // We need a motor controller, an encoder, a PID controller, and a motor model.
    // Normally, we would use a PID controller that runs on the roboRIO because it
    // gives us more flexibility, but we have had some good experience with the
    // SparkMax's in-built controller.
    private ExtendedSparkMax motorController;
    private CommonEncoder encoder;
    private CANPIDController pidController;
    private DCBrushedMotor model;

    // Nearly all of 5024's subsystems run ontop of a State Machine. Here is the
    // states list for a flywheel:
    private enum FlywheelStates {
        kIdle, kSpinup, kAtGoal
    }

    // We also need to create a state machine to run this system
    private StateMachine<FlywheelStates> stateMachine;

    // Tracker for goal velocity
    private double goalVelocity = 0.0;

    private Flywheel() {

        // We need to set the model to represent the characteristics of the motor being
        // used. This saves us some work when configuring the system later.
        model = DCBrushedMotor.NEO;

        // We will set up the motor controller, then get references to it's encoder and
        // PID controller.
        // Here, we will assume the SparkMax has been assigned CAN id #15
        this.motorController = new ExtendedSparkMax(15, MotorType.kBrushless);
        this.encoder = motorController.getCommonEncoder();
        this.pidController = motorController.getPIDController();

        // We will configure the PID controller with the same gains used on our 2020
        // robot, Darth Raider.
        pidController.setP(4.8e-4);
        pidController.setI(3e-7);
        pidController.setD(0.355);
        pidController.setIZone(0.0);
        pidController.setFF(0.0);
        pidController.setOutputRange(-1.0, 1.0);

        // Here, we configure the state machine and give it access to the robot logger
        stateMachine = new StateMachine<>("Flywheel");
        stateMachine.setConsoleHook(logger::log);

        // Tell the statemachine about all the state handlers
        stateMachine.setDefaultState(FlywheelStates.kIdle, this::handleIdle);
        stateMachine.addState(FlywheelStates.kSpinup, this::handleSpinup);
        stateMachine.addState(FlywheelStates.kAtGoal, this::handleAtGoal);

    }

    @Override
    public void periodic() {
        // We need to update the state machine
        this.stateMachine.update();
    }

    // Handler for idle state
    private void handleIdle(StateMetadata<FlywheelStates> meta) {

        // This is only TRUE once
        if (meta.isFirstRun()) {
            logger.log("Flywheel", "Became idle");

            // Set a 1 second motion profile time on the motor to reduce wear on parts
            motorController.setOpenLoopRampRate(1.0);

            // Stop the motor
            motorController.set(0.0);
        }

    }

    // Handler for spinup state
    private void handleSpinup(StateMetadata<FlywheelStates> meta) {

        // This is only TRUE once
        if (meta.isFirstRun()) {
            logger.log("Flywheel", "Spinning up");

            // Set reference velocity
            pidController.setReference(goalVelocity, ControlType.kVelocity);

            // Disable motion profiling on motor controller
            motorController.setOpenLoopRampRate(0);
        }

        // If we reach the goal velocity, switch state
        if (isVelocityCorrect()) {
            stateMachine.setState(FlywheelStates.kAtGoal);
        }

    }

    // Handler for atgoal state
    private void handleAtGoal(StateMetadata<FlywheelStates> meta) {

        // This is only TRUE once
        if (meta.isFirstRun()) {

            // We can just log that we have made it
            logger.log("Flywheel", "Made it to goal");

        }

        // If the current velocity falls too low, spin back up
        if(!isVelocityCorrect()){
            stateMachine.setState(FlywheelStates.kSpinup);
        }

    }

    /**
     * Check if the current velocity matches the goal
     * 
     * @return At goal velocity?
     */
    private boolean isVelocityCorrect() {
        // This will return true if the current velocity is with in (0.4 * Kv) of the goal.
        // Kv is shot for "RPM per volt"
        return MathUtils.epsilonEquals(getCurrentVelocity(),
                MathUtils.clamp(goalVelocity, 0, model.freeSpeedRPM), 0.4 * model.Kv);
    }

    /**
     * Set the desired velocity of the flywheel
     * 
     * @param goalRPM Goal velocity
     */
    public void setGoalVelocity(double goalRPM) {
        logger.log("Flywheel", String.format("Setting goal velocity to %.2f", goalRPM));

        // Set goal
        goalVelocity = goalRPM;

        // Set state
        stateMachine.setState(FlywheelStates.kSpinup);
    }

    /**
     * Stop the flywheel
     */
    public void stop() {

        // Force the flywheel to become idle
        stateMachine.setState(FlywheelStates.kIdle);

        // Set goal to 0
        goalVelocity = 0.0;
    }

    /**
     * Get the encoder's velocity
     * 
     * @return Encoder velocity.
     */
    public double getCurrentVelocity() {
        return this.encoder.getVelocity();
    }

    /**
     * Get if the flywheel is at it's goal velocity
     * 
     * @return Is at goal?
     */
    public boolean isAtGoal() {
        return this.stateMachine.getCurrentState() == FlywheelStates.kAtGoal;
    }
}