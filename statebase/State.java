package frc.common.statebase;

public abstract class State {
    public abstract void start();

    public abstract void loop();

    public abstract void end();

    public abstract boolean isFinished();

    public abstract State getNext(Want want);

}