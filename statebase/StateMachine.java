package frc.common.statebase;

public class StateMachine {
    private Want current_want;
    private State current_state;

    private boolean isNew = true;

    public StateMachine(Want defaultWant) {
        current_want = defaultWant;
        current_state = current_want.starting_state;
    }

    public void setWant(Want want) {
        current_want = want;
        isNew = true;
    }

    public void onLoop(double timestamp) {
        if (isNew) {
            current_state.start();
        } else {
            if (!current_state.isFinished()) {
                current_state.loop();
            } else {
                current_state.end();
                current_state = current_state.getNext(current_want);
            }
        }
    }
}