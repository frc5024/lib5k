package io.github.frc5024.lib5k.examples.statespace_flywheel.subsystems;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.frc5024.lib5k.control_loops.statespace.wrappers.SimpleFlywheelController;
import io.github.frc5024.lib5k.examples.statespace_flywheel.RobotConfig;
import io.github.frc5024.lib5k.hardware.common.sensors.interfaces.CommonEncoder;
import io.github.frc5024.lib5k.hardware.ni.roborio.fpga.RR_HAL;
import io.github.frc5024.lib5k.hardware.revrobotics.motors.ExtendedSparkMax;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

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

    // We need a motor controller and an encoder.
    private ExtendedSparkMax motorController;
    private CommonEncoder encoder;

    // The star of the show: the flywheel controller and simulator
    private SimpleFlywheelController controller;
    private FlywheelSim simulator;

    // Nearly all of 5024's subsystems run ontop of a State Machine. Here is the
    // states list for a flywheel:
    private enum FlywheelStates {
        kIdle, kSpinup, kAtGoal
    }

    // We also need to create a state machine to run this system
    private StateMachine<FlywheelStates> stateMachine;

    private Flywheel() {

        // We will set up the motor controller, then get references to it's encoder
        // Here, we will assume the SparkMax has been assigned CAN id #15
        this.motorController = new ExtendedSparkMax(15, MotorType.kBrushless);
        this.encoder = motorController.getCommonEncoder();

        // Now, we will set up the state space controller
        this.controller = new SimpleFlywheelController(RobotConfig.FlywheelConfig.MOTOR_TYPE,
                RobotConfig.FlywheelConfig.LAUNCHER_MASS_KG, RobotConfig.FlywheelConfig.LAUNCHER_DIAMETER,
                RobotConfig.FlywheelConfig.FLYWHEEL_MASS_KG, RobotConfig.FlywheelConfig.FLYWHEEL_DIAMETER,
                RobotConfig.FlywheelConfig.REALISTIC_MAX_VELOCITY_RPM, RobotConfig.FlywheelConfig.SENSOR_GEAR_RATIO,
                12.0, RobotConfig.FlywheelConfig.VELOCITY_EPSILON_RPM);
        this.simulator = controller.getSimulator();

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

        // Update the simulation
        this.simulator.setInput(this.motorController.get() * RR_HAL.getSimSafeVoltage());
        this.simulator.update(0.02);
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

            // Disable motion profiling on motor controller
            motorController.setOpenLoopRampRate(0);
        }

        // Update the motor output
        this.motorController.setVoltage(this.controller.computeNextVoltage(getCurrentVelocity()));

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
            logger.log("Made it to goal");

        }

        // Update the motor output
        this.motorController.setVoltage(this.controller.computeNextVoltage(getCurrentVelocity()));

        // If the current velocity falls too low, spin back up
        if (!isVelocityCorrect()) {
            stateMachine.setState(FlywheelStates.kSpinup);
        }

    }

    /**
     * Check if the current velocity matches the goal
     * 
     * @return At goal velocity?
     */
    private boolean isVelocityCorrect() {
        return this.controller.withinEpsilon(getCurrentVelocity());
    }

    /**
     * Set the desired velocity of the flywheel
     * 
     * @param goalRPM Goal velocity
     */
    public void setGoalVelocity(double goalRPM) {
        logger.log("Setting goal velocity to %.2f", goalRPM);

        // Reset the controller
        this.controller.reset(getCurrentVelocity());

        // Set goal
        this.controller.setDesiredVelocity(goalRPM);

        // Set state
        stateMachine.setState(FlywheelStates.kSpinup);
    }

    /**
     * Stop the flywheel
     */
    public void stop() {

        // Force the flywheel to become idle
        stateMachine.setState(FlywheelStates.kIdle);

        // Reset the controllers
        this.controller.reset(getCurrentVelocity());
    }

    /**
     * Get the encoder's velocity
     * 
     * @return Encoder velocity.
     */
    public double getCurrentVelocity() {
        if (RobotBase.isSimulation()) {
            return this.simulator.getAngularVelocityRPM();
        } else {
            return this.encoder.getVelocity();
        }
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