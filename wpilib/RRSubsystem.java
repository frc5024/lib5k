package frc.common.wpilib;

import java.util.logging.Logger;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RRSubsystem extends Subsystem {
    public String name = "Unnamed RRSubsystem";
    public Logger logger = Logger.getLogger(this.getClass().getName());

    public void initDefaultCommand() {
        logger.warning(this.name + " has no default command. Initializing nothing");
    }

    public void outputTelemetry() {
        // Do nothing
    }
}