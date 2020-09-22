package io.github.frc5024.libkontrol.statemachines;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import io.github.frc5024.lib5k.logging.RobotLogger;
import io.github.frc5024.lib5k.logging.RobotLogger.Level;

public class StateMachine<T> {
    private RobotLogger logger = RobotLogger.getInstance();

    // The state to be run in the event of an error with state handling. Should be
    // an idle state
    public T defaultStateKey;

    // A mapping of all states to their key
    public HashMap<T, StateHandler<T>> allStates = new HashMap<>();

    // Tracker for the last state to be run
    public T lastStateKey;
    public T desiredStateKey;

    // Telemetry data table
    private String name;

    /**
     * Create a StateMachine
     * 
     * @param name Name of the machine (for logging)
     */
    public StateMachine(String name) {

        // Configure telemetry
        this.name = name;

        // Set default console output
        setConsoleHook(System.out::println);

    }

    /**
     * Deprecated Method
     * 
     * @param hook hook
     */
    @Deprecated(since = "July 2020", forRemoval = true)
    public void setConsoleHook(@Nullable Consumer<String> hook) {
    }

    /**
     * Add a state to the StateMachine
     * 
     * @param key    State key
     * @param action Action to be run
     */
    public void addState(T key, Consumer<StateMetadata<T>> action) {

        // Construct a StateHandler
        StateHandler<T> handler = new StateHandler<T>(key, this, action);

        // Add to mapping
        allStates.put(key, handler);
        logger.log(String.format("Added state: %s", key.toString()));

    }

    /**
     * Add a state to the StateMachine, and set it as the default. This state will
     * be called upon any error in state handling, or when no state is set.
     * 
     * @param key    State key
     * @param action Action to be run
     */
    public void setDefaultState(T key, Consumer<StateMetadata<T>> action) {

        // Add the state to the map
        addState(key, action);

        // Set the default
        defaultStateKey = key;
        logger.log(String.format("Set state %s as default", key.toString()));

        // Make this the current state
        setState(key);

    }

    /**
     * Remove a state from the StateMachine
     * 
     * @param key State key
     */
    public void removeState(T key) {

        // Remove from allstates
        allStates.remove(key);

        // If default, remove too
        if (defaultStateKey == key) {
            defaultStateKey = null;
        }

        logger.log(String.format("Removed state: %s", key.toString()));

    }

    /**
     * Update the machine. This MUST be called periodically
     */
    public void update() {

        // If the desired state key is null, and the default is null, we can't do
        // anything
        if (desiredStateKey == null && defaultStateKey == null) {
            return;
        }

        // If the desired state key is null, overwrite it with the default key.
        boolean defaultStateWasOverridden = false;
        if (desiredStateKey == null) {
            desiredStateKey = defaultStateKey;
            defaultStateWasOverridden = true;
        }

        // If the current, and last state keys differ, this is the first run of the
        // state
        boolean isNew = lastStateKey == null || desiredStateKey == null || defaultStateWasOverridden
                || !lastStateKey.equals(desiredStateKey);

        // Set the last state key
        // This must be placed above state.call()
        lastStateKey = desiredStateKey;

        // Handle the state
        StateHandler<T> state = allStates.get(desiredStateKey);
        state.call(isNew, lastStateKey);
    }

    /**
     * Set the StateMachine's state
     * 
     * @param key State key
     */
    public void setState(T key) {
        logger.log("Switching to state: %s", Level.kDebug, key);
        desiredStateKey = key;
    }

    /**
     * Get the system's current state
     * 
     * @return current state
     */
    public @Nullable T getCurrentState() {
        return desiredStateKey;
    }

}