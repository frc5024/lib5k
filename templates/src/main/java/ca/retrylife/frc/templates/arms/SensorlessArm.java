package ca.retrylife.frc.templates.arms;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;
import io.github.frc5024.lib5k.utils.ObjectCounter;
import io.github.frc5024.libkontrol.statemachines.StateMachine;
import io.github.frc5024.libkontrol.statemachines.StateMetadata;

/**
 * The SensorlessArm class is designed to control any type of arm that does not
 * have a rotational sensor (like an encoder or potentiometer).
 * 
 * At 5024, we have used these in 2019 (climber) and 2020 (intake). The physical
 * system must consist of an arm that can be in one of two positions, and a
 * limit switch (or other digital input) at each stopping point. Since there is
 * no good sensor for this system, a strong motor must be used, otherwise the
 * arm will not hold its position at all. If the arm needs to hold position, add
 * an encoder.
 */
public class SensorlessArm extends SubsystemBase {
    private RobotLogger logger = RobotLogger.getInstance();

    // System states
    public enum SystemState {
        kLowered, kRaised, kStopped;
    }

    // System state machine
    private StateMachine<SystemState> stateMachine;

    // In this system, we have a separate tracker for what the user actually wants.
    // This reduces code reuse
    private SystemState userDesiredState = SystemState.kStopped;

    // System I/O
    private SpeedController motor;
    private DigitalInput loweredLimitSensor;
    private DigitalInput raisedLimitSensor;

    // Speed controls
    private double speed;
    private double feedForward;

    // ID counter
    private static ObjectCounter counter = new ObjectCounter();

    /**
     * Create a SensorlessArm
     * 
     * @param motor              Arm motor
     * @param loweredLimitSensor Limit sensor at the bottom of the arm's range of
     *                           motion
     * @param raisedLimitSensor  Limit sensor at the top of the arm's range of
     *                           motion
     * @param speed              How fast the arm should move downwards as a
     *                           percentage output
     * @param feedForward        How much faster the arm should move going up than
     *                           it should going down
     */
    public SensorlessArm(SpeedController motor, DigitalInput loweredLimitSensor, DigitalInput raisedLimitSensor,
            double speed, double feedForward) {

        // Set locals
        this.motor = motor;
        this.loweredLimitSensor = loweredLimitSensor;
        this.raisedLimitSensor = raisedLimitSensor;
        this.speed = speed;
        this.feedForward = feedForward;

        // Configure state machine
        String name = String.format("SensorlessArm[%d]", counter.getNewID());
        this.stateMachine = new StateMachine<>(name);
        this.stateMachine.setDefaultState(SystemState.kStopped, this::handleStopped);
        this.stateMachine.addState(SystemState.kRaised, this::handleRaising);
        this.stateMachine.addState(SystemState.kLowered, this::handleLowering);

        // Publish all components over the network
        // TODO: This is a 2021 thing
        // LiveWindow.getInstance().addActuator(name, "motor", motor);
        // LiveWindow.getInstance().addSensor(name, "loweredLimitSensor",
        // loweredLimitSensor);
        // LiveWindow.getInstance().addSensor(name, "raisedLimitSensor",
        // raisedLimitSensor);

    }

    @Override
    public void periodic() {

        // Update the state machine
        this.stateMachine.update();

    }

    /**
     * Handler for the arm being stopped
     * 
     * @param meta State metadata
     */
    private void handleStopped(StateMetadata<SystemState> meta) {

        if (meta.isFirstRun()) {
            // Stop the motor
            motor.set(0.0);
            logger.log("Arm has stopped");
        }

        // Handle the arm moving
        switch (userDesiredState) {
            case kLowered:

                // Check if we aren't touching the lower sensor
                if (!loweredLimitSensor.get()) {
                    logger.log("Arm has lost contact with the lower limit sensor", Level.kDebug);
                    // Go back to trying to lower the arm
                    stateMachine.setState(SystemState.kLowered);
                }
                break;

            case kRaised:

                // Check if we aren't touching the upper sensor
                if (!raisedLimitSensor.get()) {
                    // Go back to trying to raise the arm
                    logger.log("Arm has lost contact with the upper limit sensor", Level.kDebug);
                    stateMachine.setState(SystemState.kRaised);
                }
                break;

            default:
                // Do nothing
                break;
        }

    }

    /**
     * Handle raising the arm
     * 
     * @param meta State metadata
     */
    private void handleRaising(StateMetadata<SystemState> meta) {

        if (meta.isFirstRun()) {
            logger.log("Raising arm");

            // Set motor speed
            motor.set(speed + feedForward);
        }

        // Check if arm has hit the upper sensor
        if (raisedLimitSensor.get()) {
            logger.log("Upper sensor triggered", Level.kDebug);
            stateMachine.setState(SystemState.kStopped);
        }
    }

    /**
     * Handle lowering the arm
     * 
     * @param meta State metadata
     */
    private void handleLowering(StateMetadata<SystemState> meta) {

        if (meta.isFirstRun()) {
            logger.log("Lowering arm");

            // Set motor speed
            motor.set(speed * -1);
        }

        // Check if arm has hit the lower sensor
        if (loweredLimitSensor.get()) {
            logger.log("Lower sensor triggered", Level.kDebug);
            stateMachine.setState(SystemState.kStopped);
        }
    }

    /**
     * Set the desired position for the arm
     * 
     * @param position Desired position
     */
    public void setDesiredPosition(SystemState position) {
        logger.log("Desired position set to: %s", position);
        stateMachine.setState(position);
        userDesiredState = position;
    }

    /**
     * Get the last known position of the arm
     * 
     * @return Last position
     */
    public SystemState getLastKnownPosition() {
        return stateMachine.getCurrentState();
    }

    /**
     * Stop the arm
     */
    public void stop() {
        setDesiredPosition(SystemState.kStopped);
    }

}