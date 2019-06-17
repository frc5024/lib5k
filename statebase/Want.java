package frc.common.statebase;

import java.util.ArrayList;

/**
 * Want class that give a name and a list of States that should be
 * completed in order
 */
public class Want {
    public String name;
    public State starting_state;

    protected void registerStarter(State state) {
        starting_state = state;
    }

}