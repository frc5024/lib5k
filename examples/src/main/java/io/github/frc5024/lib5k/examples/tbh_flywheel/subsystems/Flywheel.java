package io.github.frc5024.lib5k.examples.tbh_flywheel.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import ca.retrylife.ewmath.MathUtils;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.control_loops.TBHController;
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

    // We need a motor controller, and an encoder.
    private ExtendedSparkMax motorController;
    private CommonEncoder encoder;
    private DCBrushedMotor model;

    // We will use lib5k's in-built TBH controller to perform a "take-back-half"
    // operation.
    private TBHController controller;

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
        model = new DCBrushedMotor(DCMotor.getNEO(1));

        // We will set up the motor controller, then get a reference to it's encoder
        // Here, we will assume the SparkMax has been assigned CAN id #15
        this.motorController = new ExtendedSparkMax(15, MotorType.kBrushless);
        this.encoder = motorController.getCommonEncoder();

        // We will configure the TBH controller with a reasonable gain and set the minimum settling time to 500ms
        this.controller = new TBHController(0.3, 0.5);

        // This will set the acceptable error to (0.4 * Kv) of the velocity goal.
        // Kv is shot for "RPM per volt"
        this.controller.setEpsilon(0.4 * model.getKv());

        // Here, we configure the state machine
        stateMachine = new StateMachine<>("Flywheel");

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
            logger.log("Became idle");

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
            logger.log("Spinning up");

            // Set the controller goal
            controller.setReference(goalVelocity);

            // Disable motion profiling on motor controller
            motorController.setOpenLoopRampRate(0);
        }

        // Calculate motor output
        motorController.set(controller.calculate(encoder.getVelocity()));

        // If we reach the goal velocity, switch state
        if (controller.atReference()) {
            stateMachine.setState(FlywheelStates.kAtGoal);
        }

    }

    // Handler for atgoal state
    private void handleAtGoal(StateMetadata<FlywheelStates> meta) {

        // This is only TRUE once
        if (meta.isFirstRun()) {

            // We can just log that we have made it
            logger.log("Made it to goal");

        }

        // Update controller without consuming output to keep "is at goal" up to date
        controller.calculate(encoder.getVelocity());

        // If the current velocity falls too low, spin back up
        if (!controller.atReference()) {
            stateMachine.setState(FlywheelStates.kSpinup);
        }

    }

    /**
     * Set the desired velocity of the flywheel
     * 
     * @param goalRPM Goal velocity
     */
    public void setGoalVelocity(double goalRPM) {
        logger.log("Setting goal velocity to %.2f", goalRPM);

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