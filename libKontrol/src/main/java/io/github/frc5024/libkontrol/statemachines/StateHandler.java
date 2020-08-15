package io.github.frc5024.libkontrol.statemachines;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StateHandler<T> {

    private T key;
    private StateMachine<T> parent;
    private Consumer<StateMetadata<T>> action;

    /**
     * Create a StateHandler for a given state
     * 
     * @param key    The key for this state (it's name)
     * @param parent The state machine that owns this object
     * @param action The action to be performed during this state
     */
    public StateHandler(@Nonnull T key, @Nonnull StateMachine<T> parent, @Nonnull Consumer<StateMetadata<T>> action) {
        this.key = key;
        this.parent = parent;
        this.action = action;
    }

    /**
     * Call the action function
     * 
     * @param isNew        Is this the first run?
     * @param lastStateKey Key of the last state to run
     */
    public void call(boolean isNew, @Nullable T lastStateKey) {
        action.accept(new StateMetadata<T>(parent, lastStateKey, isNew));
    }

}