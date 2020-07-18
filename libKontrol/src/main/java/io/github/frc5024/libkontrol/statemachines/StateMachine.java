package io.github.frc5024.libkontrol.statemachines;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class StateMachine<T> {

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
    private Consumer<String> consoleHook;

    public StateMachine(String name) {

        // Configure telemetry
        this.name = name;

        // Set default console output
        setConsoleHook(System.out::println);

    }

    /**
     * Override the default console output function for this object. Setting to null
     * will silence all logs from this object.
     * 
     * @param hook Console logging function
     */
    public void setConsoleHook(@Nullable Consumer<String> hook) {
        this.consoleHook = hook;
    }

    /**
     * Log a console message
     */
    private void log(String message) {

        // Call the console hook
        if (consoleHook != null) {
            consoleHook.accept(message);
        }
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
        log(String.format("Added state: %s", key.toString()));

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
        log(String.format("Set state %s as default", key.toString()));

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

        log(String.format("Removed state: %s", key.toString()));

    }

    public void update() {

        // If the desired state key is null, and the default is null, we can't do
        // anything
        if (desiredStateKey == null && defaultStateKey == null) {
            return;
        }

        // If the desired state key is null, overwrite it with the default key.
        if (desiredStateKey == null) {
            desiredStateKey = defaultStateKey;
        }

        // If the current, and last state keys differ, this is the first run of the
        // state
        boolean isNew = lastStateKey == null || desiredStateKey == null || !lastStateKey.equals(desiredStateKey);

        // Handle the state
        StateHandler<T> state = allStates.get(desiredStateKey);
        state.call(isNew, lastStateKey);

        // Set the last state key
        lastStateKey = desiredStateKey;

    }

    /**
     * Set the StateMachine's state
     * 
     * @param key State key
     */
    public void setState(T key) {
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