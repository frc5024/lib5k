package frc.common.loopables;

import frc.common.utils.RobotLogger;

public abstract class LoopableSubsystem {
    protected RobotLogger logger = RobotLogger.getInstance();

    public String name = "Unnamed Subsystem";
    public double last_timestamp;

    public void periodicInput() {
    }

    public void periodicOutput() {

    }
    
    public abstract void outputTelemetry();
    
    public abstract void stop();

    public abstract void reset();

    public abstract boolean checkHealth();
}