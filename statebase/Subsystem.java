package frc.common.statebase;

import frc.common.utils.RobotLogger;

public abstract class Subsystem {
    private RobotLogger logger = RobotLogger.getInstance();

    public void periodicInput(double timestamp) {
    }

    public void periodicOutput(double timestamp) {

    }
    
    public abstract void outputTelemetry();
    
    public abstract void stop();

    public abstract void reset();

    public abstract boolean checkHealth();
}